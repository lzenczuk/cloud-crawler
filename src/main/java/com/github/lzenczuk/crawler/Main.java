package com.github.lzenczuk.crawler;

import com.github.lzenczuk.crawler.httpclient.impl.apache.ApacheHttpCrawlerClient;
import com.github.lzenczuk.crawler.scenario.impl.coindesk.CoinDeskPriceStorage;
import com.github.lzenczuk.crawler.scenario.impl.coindesk.CoinDeskScenario;
import com.github.lzenczuk.crawler.scenario.impl.iconomi.IconomiPriceStorage;
import com.github.lzenczuk.crawler.scenario.impl.iconomi.IconomiScenario;
import com.github.lzenczuk.crawler.scenario.impl.poloniex.PoloniexScenario;
import com.github.lzenczuk.crawler.scenario.impl.poloniex.stream.consumer.MessageConsumerException;
import com.github.lzenczuk.crawler.scenario.impl.poloniex.stream.consumer.file.FileMessageConsumerImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Created by dev on 11/07/16.
 */
public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException, MessageConsumerException, IOException {

        ApacheHttpCrawlerClient apacheHttpCrawlerClient = new ApacheHttpCrawlerClient();

        /*String iconomiOutputFilePath = "iconomi_prices.csv";
        String coindeskOutputFilePath = "coindesk_prices.csv";

        String storageFolder = System.getenv("storage_folder");
        if(storageFolder!=null){
            iconomiOutputFilePath = storageFolder+"/"+iconomiOutputFilePath;
            coindeskOutputFilePath = storageFolder+"/"+coindeskOutputFilePath;
        }

        logger.info("Iconomi output data will be store in "+iconomiOutputFilePath);
        IconomiPriceStorage iconomiPriceStorage = new IconomiPriceStorage(iconomiOutputFilePath);
        CoinDeskPriceStorage coinDeskPriceStorage = new CoinDeskPriceStorage(coindeskOutputFilePath);

        IconomiScenario iconomiScenario = new IconomiScenario(apacheHttpCrawlerClient, iconomiPriceStorage);
        CoinDeskScenario coinDeskScenario = new CoinDeskScenario(apacheHttpCrawlerClient, coinDeskPriceStorage);*/

        /*ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(
                () -> {

                    logger.info("Running scenario iconomi.");
                    iconomiScenario.execute()
                        .whenComplete((scenarioExecutionResult, throwable) -> {
                            if (throwable != null) {
                                logger.error("Throwable. This shouldn't happen. ", throwable);
                            }

                            if (scenarioExecutionResult.isSuccess()) {
                                logger.info("Scenario iconomi executed.");
                            } else {
                                logger.error("Execution error: " + scenarioExecutionResult.getErrorMessage());
                            }
                        });


                    logger.info("Running scenario coindesk.");
                    coinDeskScenario.execute()
                            .whenComplete((scenarioExecutionResult, throwable) -> {
                                if (throwable != null) {
                                    logger.error("Throwable. This shouldn't happen. ", throwable);
                                }

                                if (scenarioExecutionResult.isSuccess()) {
                                    logger.info("Scenario coindesk executed.");
                                } else {
                                    logger.error("Execution error: " + scenarioExecutionResult.getErrorMessage());
                                }
                            });
                },
                0,
                5,
                TimeUnit.MINUTES
        );*/

        String outputFilePath = "poloniex.json.gz";

        String storageFolder = System.getenv("storage_folder");
        if(storageFolder!=null){
            outputFilePath = storageFolder+"/"+outputFilePath;
        }

        FileMessageConsumerImpl fileMessageConsumer = new FileMessageConsumerImpl(outputFilePath);

        PoloniexScenario poloniexScenario = new PoloniexScenario(apacheHttpCrawlerClient, fileMessageConsumer);
        poloniexScenario.execute().whenComplete((scenarioExecutionResult, throwable) -> {
            logger.info("Completed: "+scenarioExecutionResult);
            apacheHttpCrawlerClient.stop();

            if(fileMessageConsumer!=null){
                try {
                    fileMessageConsumer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if(fileMessageConsumer!=null) {
                    System.out.println("-------------------> gz file closed");
                    fileMessageConsumer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }
}
