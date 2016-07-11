package com.github.lzenczuk.crawler.scenario.impl.coindesk;

import com.github.lzenczuk.crawler.httpclient.HttpCrawlerClient;
import com.github.lzenczuk.crawler.httpclient.HttpCrawlerMethod;
import com.github.lzenczuk.crawler.httpclient.HttpCrawlerRequest;
import com.github.lzenczuk.crawler.scenario.Scenario;
import com.github.lzenczuk.crawler.scenario.ScenarioExecutionResult;
import com.github.lzenczuk.crawler.scenario.impl.iconomi.IconomiStorageException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

/**
 * Created by dev on 11/07/16.
 */
public class CoinDeskScenario implements Scenario {

    private static final Logger logger = LogManager.getLogger(CoinDeskScenario.class);

    private HttpCrawlerClient httpCrawlerClient;
    private CoinDeskPriceStorage priceStorage;

    public CoinDeskScenario(HttpCrawlerClient httpCrawlerClient, CoinDeskPriceStorage priceStorage) {
        this.httpCrawlerClient = httpCrawlerClient;
        this.priceStorage = priceStorage;
    }

    @Override
    public CompletableFuture<ScenarioExecutionResult> execute() {
        CompletableFuture<ScenarioExecutionResult> resultFuture = new CompletableFuture<>();

        HttpCrawlerRequest initialRequest = new HttpCrawlerRequest();
        try {
            initialRequest.setUrl(new URL("http://www.coindesk.com/"));
        } catch (MalformedURLException e) {
            resultFuture.complete(new ScenarioExecutionResult("Incorrect url. "+e.getMessage()));
        }
        initialRequest.setMethod(HttpCrawlerMethod.GET);

        httpCrawlerClient.process(initialRequest).thenAccept(httpCrawlerResponse -> {
            if(httpCrawlerResponse.isError()){
                resultFuture.complete(new ScenarioExecutionResult(httpCrawlerResponse.getCrawlerError().getErrorMessage()));
            }

            if(httpCrawlerResponse.getContent()!=null){
                Document document = Jsoup.parse(httpCrawlerResponse.getContent());

                Elements pricesElements = document.select("div.header div.row div.header-prices");
                if(pricesElements==null){
                    resultFuture.complete(new ScenarioExecutionResult("Prices element not found on page."));
                }

                if(pricesElements.size()!=1){
                    resultFuture.complete(new ScenarioExecutionResult("Incorrect number of prices elements on page. Expecting one but was "+pricesElements.size()));
                }

                Element pricesElement = pricesElements.get(0);

                String priceUsd = priceStringToPrice(pricesElement.select("div.price-usd div.bpi-value").text(), "$");
                String priceCny = priceStringToPrice(pricesElement.select("div.price-cny div.bpi-value").text(), "¥");
                String priceEur = priceStringToPrice(pricesElement.select("div.price-eur div.bpi-value").text(), "€");
                String priceGbp = priceStringToPrice(pricesElement.select("div.price-gbp div.bpi-value").text(), "£");

                logger.info("Coindesk prices USD: "+priceUsd+"; EUR: "+priceEur+"; CNY: "+priceCny+"; GBP: "+priceGbp);

                try {
                    priceStorage.storePrice(new Date(), priceUsd, priceEur, priceGbp, priceCny);
                } catch (IconomiStorageException e) {
                    logger.error("Error storing result price."+e.getMessage());
                    resultFuture.complete(
                            new ScenarioExecutionResult("Error storing prices USD: "+priceUsd+"; EUR: "+priceEur+"; CNY: "+priceCny+"; GBP: "+priceGbp+"; "+e.getMessage()));
                }

                resultFuture.complete(new ScenarioExecutionResult());
            }else{
                resultFuture.complete(new ScenarioExecutionResult("No content."));
            }
        });

        return resultFuture;
    }

    private String priceStringToPrice(String priceString, String currencySign) {
        String price;
        if(!priceString.startsWith(currencySign)){
            price="incorrectCur="+priceString;
        }else{
            price = priceString.substring(1).replace(",","").trim();
        }

        return price;
    }
}
