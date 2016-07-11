package com.github.lzenczuk.crawler;

import com.github.lzenczuk.crawler.httpclient.impl.apache.ApacheHttpCrawlerClient;
import com.github.lzenczuk.crawler.scenario.impl.iconomi.IconomiPriceStorage;
import com.github.lzenczuk.crawler.scenario.impl.iconomi.IconomiScenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by dev on 11/07/16.
 */
public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException {
        ApacheHttpCrawlerClient apacheHttpCrawlerClient = new ApacheHttpCrawlerClient();
        IconomiPriceStorage iconomiPriceStorage = new IconomiPriceStorage("out.csv");

        IconomiScenario iconomiScenario = new IconomiScenario(apacheHttpCrawlerClient, iconomiPriceStorage);

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(
                () -> iconomiScenario.execute()
                        .whenComplete((scenarioExecutionResult, throwable) -> {
                            if(throwable!=null){
                                logger.error("Throwable. This shouldn't happen. ", throwable);
                            }

                            if(scenarioExecutionResult.isSuccess()){
                                logger.debug("Execution successful");
                            }else{
                                logger.error("Execution error: "+scenarioExecutionResult.getErrorMessage());
                            }
                        }),
                0,
                5,
                TimeUnit.MINUTES
        );
    }
}
