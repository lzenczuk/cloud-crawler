package com.github.lzenczuk.crawler.httpclient;

/**
 * Created by dev on 09/07/16.
 */
public class HttpCrawlerError {
    private final HttpCrawlerErrorCode errorCode;
    private final String errorMessage;

    public HttpCrawlerError(HttpCrawlerErrorCode errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public HttpCrawlerErrorCode getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return "HttpCrawlerError{" +
                "errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
