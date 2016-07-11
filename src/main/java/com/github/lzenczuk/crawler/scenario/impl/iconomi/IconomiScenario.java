package com.github.lzenczuk.crawler.scenario.impl.iconomi;

import com.github.lzenczuk.crawler.httpclient.HttpCrawlerClient;
import com.github.lzenczuk.crawler.httpclient.HttpCrawlerMethod;
import com.github.lzenczuk.crawler.httpclient.HttpCrawlerRequest;
import com.github.lzenczuk.crawler.scenario.Scenario;
import com.github.lzenczuk.crawler.scenario.ScenarioExecutionResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

/**
 * Created by dev on 11/07/16.
 */
public class IconomiScenario implements Scenario {

    private static final Logger logger = LogManager.getLogger(IconomiScenario.class);

    private HttpCrawlerClient httpCrawlerClient;
    private IconomiPriceStorage priceStorage;

    public IconomiScenario(HttpCrawlerClient httpCrawlerClient, IconomiPriceStorage priceStorage) {
        this.httpCrawlerClient = httpCrawlerClient;
        this.priceStorage = priceStorage;
    }

    @Override
    public CompletableFuture<ScenarioExecutionResult> execute() {

        CompletableFuture<ScenarioExecutionResult> resultFuture = new CompletableFuture<>();

        HttpCrawlerRequest initialRequest = new HttpCrawlerRequest();
        try {
            initialRequest.setUrl(new URL("https://www.iconomi.net/"));
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

                Elements priceElements = document.select("div.top-bar-price span[data-static=exchange-rate]");
                if(priceElements==null){
                    resultFuture.complete(new ScenarioExecutionResult("Price element not found on page."));
                }

                if(priceElements.size()!=1){
                    resultFuture.complete(new ScenarioExecutionResult("Incorrect number of price elements on page. Expecting one but was "+priceElements.size()));
                }

                String priceString = priceElements.get(0).text();
                String[] priceStringArray = priceString.split(" ");
                if(priceStringArray.length!=5){
                    resultFuture.complete(new ScenarioExecutionResult("Incorrect number of elements in price string. Expecting 5 but was "+priceStringArray.length));
                }

                try {
                    Date date = new Date();
                    BigDecimal price = new BigDecimal(priceStringArray[3]);

                    logger.debug("Date: "+ date +"; Price: "+price);
                    priceStorage.storePrice(date, price);

                }catch (NumberFormatException e){
                    logger.error("Price parsing error: "+priceStringArray[3]+"; "+e.getMessage());
                    resultFuture.complete(new ScenarioExecutionResult("Error parsing price sub string: "+priceStringArray[3]+"; "+e.getMessage()));
                } catch (IconomiStorageException e) {
                    logger.error("Error storing result price."+e.getMessage());
                    resultFuture.complete(new ScenarioExecutionResult("Error parsing price sub string: "+priceStringArray[3]+"; "+e.getMessage()));
                }

                resultFuture.complete(new ScenarioExecutionResult());
            }else{
                resultFuture.complete(new ScenarioExecutionResult("No content."));
            }
        });

        return resultFuture;
    }
}
