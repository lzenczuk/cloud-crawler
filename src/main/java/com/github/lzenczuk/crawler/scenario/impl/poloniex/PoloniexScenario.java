package com.github.lzenczuk.crawler.scenario.impl.poloniex;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lzenczuk.crawler.httpclient.HttpCrawlerClient;
import com.github.lzenczuk.crawler.httpclient.HttpCrawlerMethod;
import com.github.lzenczuk.crawler.httpclient.HttpCrawlerRequest;
import com.github.lzenczuk.crawler.scenario.Scenario;
import com.github.lzenczuk.crawler.scenario.ScenarioExecutionResult;
import com.github.lzenczuk.crawler.scenario.impl.poloniex.stream.PoloniexWsClient;
import com.github.lzenczuk.crawler.scenario.impl.poloniex.stream.consumer.MessageConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

/**
 * Created by dev on 18/07/16.
 */
public class PoloniexScenario implements Scenario {

    private static final Logger logger = LogManager.getLogger(PoloniexScenario.class);

    private HttpCrawlerClient httpCrawlerClient;

    private final MessageConsumer messageConsumer;

    public PoloniexScenario(HttpCrawlerClient httpCrawlerClient, MessageConsumer messageConsumer) {
        this.httpCrawlerClient = httpCrawlerClient;
        this.messageConsumer = messageConsumer;
    }

    @Override
    public CompletableFuture<ScenarioExecutionResult> execute() {

        CompletableFuture<ScenarioExecutionResult> resultFuture = new CompletableFuture<>();

        HttpCrawlerRequest initialRequest = new HttpCrawlerRequest();
        try {
            initialRequest.setUrl(new URL("https://poloniex.com/public?command=returnTicker"));
        } catch (MalformedURLException e) {
            resultFuture.complete(new ScenarioExecutionResult("Incorrect url. "+e.getMessage()));
        }
        initialRequest.setMethod(HttpCrawlerMethod.GET);

        httpCrawlerClient.process(initialRequest).thenAccept(httpCrawlerResponse -> {

            if(httpCrawlerResponse.isError()){
                logger.info("Error fetching tickets from poloniex");
                resultFuture.complete(new ScenarioExecutionResult(httpCrawlerResponse.getCrawlerError().getErrorMessage()));
                return;
            }

            ObjectMapper objectMapper = new ObjectMapper();

            try {
                TypeReference<HashMap<String, MarketDTO>> marketsRef = new TypeReference<HashMap<String, MarketDTO>>() {};

                HashMap<String, MarketDTO> marketsMapDTO = objectMapper.readValue(httpCrawlerResponse.getContent(), marketsRef);

                Markets markets = new Markets();
                marketsMapDTO.entrySet().forEach(entry -> markets.addMarket(entry.getKey(), entry.getValue().getId()));

                PoloniexWsClient poloniexWsClient = new PoloniexWsClient(messageConsumer);
                CompletableFuture<Void> stopFeature = poloniexWsClient.start();

                poloniexWsClient.subscribe(markets.getMarketIdByName("BTC_ETH"), "BTC", "ETH");

                stopFeature.join();
                resultFuture.complete(new ScenarioExecutionResult());

            } catch (IOException e) {
                logger.error("Error mapping.", e.getMessage());
                resultFuture.complete(new ScenarioExecutionResult(e.getMessage()));
            } catch (InterruptedException e) {
                logger.error("Error thread exception.", e.getMessage());
                resultFuture.complete(new ScenarioExecutionResult(e.getMessage()));
            } catch (DeploymentException e) {
                logger.error("Error deployment.", e.getMessage());
                resultFuture.complete(new ScenarioExecutionResult(e.getMessage()));
            } catch (URISyntaxException e) {
                logger.error("Error URI.", e.getMessage());
                resultFuture.complete(new ScenarioExecutionResult(e.getMessage()));
            }
        });

        return resultFuture;
    }
}
