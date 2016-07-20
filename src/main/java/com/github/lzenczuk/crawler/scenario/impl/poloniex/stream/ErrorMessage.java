package com.github.lzenczuk.crawler.scenario.impl.poloniex.stream;

/**
 * Created by dev on 15/07/16.
 */
public class ErrorMessage implements Message {
    private MessageMappingErrorType errorType;
    private String errorMessage;
    private String message;

    public ErrorMessage(MessageMappingErrorType errorType, String errorMessage, String message) {
        this.errorType = errorType;
        this.errorMessage = errorMessage;
        this.message = message;
    }

    public MessageMappingErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(MessageMappingErrorType errorType) {
        this.errorType = errorType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ErrorMessage{" +
                "errorType=" + errorType +
                ", errorMessage='" + errorMessage + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
