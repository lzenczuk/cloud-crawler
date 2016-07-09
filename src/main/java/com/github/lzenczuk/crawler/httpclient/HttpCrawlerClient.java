package com.github.lzenczuk.crawler.httpclient;

import java.util.concurrent.CompletableFuture;

/**
 * Created by dev on 09/07/16.
 */
public interface HttpCrawlerClient {
    CompletableFuture<HttpCrawlerResponse> process(HttpCrawlerRequest httpCrawlerRequest);
}
