package com.github.lzenczuk.crawler.httpclient;

/**
 * Created by dev on 09/07/16.
 */
public enum HttpCrawlerErrorCode {

    REQUEST_PREPARING_ERROR(100),
    RESPONSE_CONTENT_EXTRACTING_ERROR(101),
    REDIRECTION_PROCESSING_ERROR(102),
    HTTP_CLIENT_ERROR(103),
    REQUEST_CANCELED_ERROR(104),
    REQUEST_TIMEOUT_ERROR(105),
    CONNECTION_ERROR(106);

    public final int codeNumber;

    HttpCrawlerErrorCode(int codeNumber) {
        this.codeNumber = codeNumber;
    }
}
