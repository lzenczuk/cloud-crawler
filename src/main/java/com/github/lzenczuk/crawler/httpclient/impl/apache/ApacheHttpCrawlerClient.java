package com.github.lzenczuk.crawler.httpclient.impl.apache;

import com.github.lzenczuk.crawler.httpclient.HttpCrawlerClient;
import com.github.lzenczuk.crawler.httpclient.HttpCrawlerErrorCode;
import com.github.lzenczuk.crawler.httpclient.HttpCrawlerRequest;
import com.github.lzenczuk.crawler.httpclient.HttpCrawlerResponse;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Created by dev on 09/07/16.
 */
public class ApacheHttpCrawlerClient implements HttpCrawlerClient{

    private static final Logger logger = LogManager.getLogger(ApacheHttpCrawlerClient.class);

    public static final int DEFAULT_TIMEOUT = 5000;

    private final CloseableHttpAsyncClient httpAsyncClient;

    public ApacheHttpCrawlerClient() {
        // setup client with blocked default redirection
        this.httpAsyncClient = HttpAsyncClientBuilder.create()
                .setRedirectStrategy(new RedirectStrategy() {
                    @Override
                    public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
                        return false;
                    }

                    @Override
                    public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
                        return null;
                    }
                })
                .setMaxConnTotal(100)
                .setMaxConnPerRoute(100)
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setSocketTimeout(DEFAULT_TIMEOUT)
                        .setConnectTimeout(DEFAULT_TIMEOUT)
                        .setConnectionRequestTimeout(DEFAULT_TIMEOUT)
                        .build()
                )
                .build();

        httpAsyncClient.start();

        logger.info("Asyc apache http client started");
    }

    public ApacheHttpCrawlerClient(CloseableHttpAsyncClient httpAsyncClient) {
        this.httpAsyncClient = httpAsyncClient;
    }

    @Override
    public CompletableFuture<HttpCrawlerResponse> process(HttpCrawlerRequest httpCrawlerRequest) {

        logger.info("Processing request: "+httpCrawlerRequest.getUrl());

        CompletableFuture<HttpCrawlerResponse> responseCompletableFuture = new CompletableFuture<>();

        HttpUriRequest request;

        try {
            request = ApacheHttpCrawlerRequestFactory.fromHttpCrawlerRequest(httpCrawlerRequest);
        } catch (ApacheHttpCrawlerClientException e) {
            responseCompletableFuture.complete(new HttpCrawlerResponse(HttpCrawlerErrorCode.REQUEST_PREPARING_ERROR, e.getMessage()));
            return responseCompletableFuture;
        }

        logger.info("Executing request: "+request);
        httpAsyncClient.execute(request,new ApacheHttpCrawlerFutureCallback(httpAsyncClient, request, httpCrawlerRequest, responseCompletableFuture));

        return responseCompletableFuture;

    }

    @Override
    public void stop() {
        if(httpAsyncClient!=null){
            try {
                httpAsyncClient.close();
            } catch (IOException e) {
                logger.error("Error closing apache http client. "+e.getMessage());
            }
        }
    }
}
