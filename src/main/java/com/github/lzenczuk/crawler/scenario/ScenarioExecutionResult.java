package com.github.lzenczuk.crawler.scenario;

/**
 * Created by dev on 11/07/16.
 */
public class ScenarioExecutionResult {
    private final String errorMessage;

    public ScenarioExecutionResult(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ScenarioExecutionResult() {
        errorMessage = null;
    }

    public boolean isError(){
        return errorMessage!=null;
    }

    public boolean isSuccess(){
        return errorMessage==null;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return "ScenarioExecutionResult{" +
                "errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
