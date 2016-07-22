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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by dev on 11/07/16.
 */
public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException, MessageConsumerException, IOException {

        ApacheHttpCrawlerClient apacheHttpCrawlerClient = new ApacheHttpCrawlerClient();

        String iconomiOutputFilePath = "iconomi_prices.csv";
        String coindeskOutputFilePath = "coindesk_prices.csv";
        String poloniexOutputFilePath = "poloniex.json.gz";

        String storageFolder = System.getenv("storage_folder");
        if(storageFolder!=null){
            iconomiOutputFilePath = storageFolder+"/"+iconomiOutputFilePath;
            coindeskOutputFilePath = storageFolder+"/"+coindeskOutputFilePath;
            poloniexOutputFilePath = storageFolder+"/"+poloniexOutputFilePath;
        }

        logger.info("Iconomi output data will be store in "+iconomiOutputFilePath);
        IconomiPriceStorage iconomiPriceStorage = new IconomiPriceStorage(iconomiOutputFilePath);
        CoinDeskPriceStorage coinDeskPriceStorage = new CoinDeskPriceStorage(coindeskOutputFilePath);

        IconomiScenario iconomiScenario = new IconomiScenario(apacheHttpCrawlerClient, iconomiPriceStorage);
        CoinDeskScenario coinDeskScenario = new CoinDeskScenario(apacheHttpCrawlerClient, coinDeskPriceStorage);

        FileMessageConsumerImpl poloniexFileMessageConsumer = new FileMessageConsumerImpl(poloniexOutputFilePath);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if(poloniexFileMessageConsumer!=null) {
                    poloniexFileMessageConsumer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        new Thread(() -> {
            PoloniexScenario poloniexScenario = new PoloniexScenario(apacheHttpCrawlerClient, poloniexFileMessageConsumer);
            poloniexScenario.execute().whenComplete((scenarioExecutionResult, throwable) -> {
                logger.info("Completed: "+scenarioExecutionResult);
                apacheHttpCrawlerClient.stop();

                if(poloniexFileMessageConsumer!=null){
                    try {
                        poloniexFileMessageConsumer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }).start();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
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
        );
    }
}
