package com.github.lzenczuk.crawler.scenario;

import java.util.concurrent.CompletableFuture;

/**
 * Created by dev on 11/07/16.
 */
public interface Scenario {
    CompletableFuture<ScenarioExecutionResult> execute();
}
