package com.github.lzenczuk.crawler.httpclient.impl.apache;

import com.github.lzenczuk.crawler.httpclient.*;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Created by dev on 09/07/16.
 */
public class ApacheHttpCrawlerFutureCallback implements FutureCallback<HttpResponse> {

    private static final Logger logger = LogManager.getLogger(ApacheHttpCrawlerFutureCallback.class);

    private final CompletableFuture<HttpCrawlerResponse> responseCompletableFuture;
    private final CloseableHttpAsyncClient httpAsyncClient;
    private final HttpCrawlerRequest httpCrawlerRequest;
    private final HttpCrawlerResponse httpCrawlerResponse;

    private HttpUriRequest request;

    public ApacheHttpCrawlerFutureCallback(CloseableHttpAsyncClient httpAsyncClient, HttpUriRequest request, HttpCrawlerRequest httpCrawlerRequest, CompletableFuture<HttpCrawlerResponse> responseCompletableFuture) {
        this.responseCompletableFuture = responseCompletableFuture;
        this.httpAsyncClient = httpAsyncClient;
        this.httpCrawlerRequest = httpCrawlerRequest;
        this.httpCrawlerResponse = new HttpCrawlerResponse(httpCrawlerRequest.getUrl());
        this.request = request;
    }

    @Override
    public void completed(HttpResponse response) {

        logger.info("Complete: " + response);

        //https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
        if (ApacheHttpCrawlerUtil.isRedirection(response) && httpCrawlerRequest.isRedirect()) {
            logger.info("Redirect");
            redirect(response);
        } else {
            logger.info("No redirection. Updating response.");
            updateResponse(response);
            this.responseCompletableFuture.complete(this.httpCrawlerResponse);
        }
    }

    @Override
    public void failed(Exception e) {
        logger.info("Failed: "+e.getClass().toString());

        if(e instanceof java.net.SocketTimeoutException){
            this.httpCrawlerResponse.setCrawlerError(new HttpCrawlerError(HttpCrawlerErrorCode.REQUEST_TIMEOUT_ERROR, e.getMessage()));
        }else if(e instanceof java.net.ConnectException) {
            this.httpCrawlerResponse.setCrawlerError(new HttpCrawlerError(HttpCrawlerErrorCode.CONNECTION_ERROR, e.getMessage()));
        }else{
            this.httpCrawlerResponse.setCrawlerError(new HttpCrawlerError(HttpCrawlerErrorCode.HTTP_CLIENT_ERROR, e.getMessage()));
        }

        this.responseCompletableFuture.complete(this.httpCrawlerResponse);
    }

    @Override
    public void cancelled() {
        logger.info("Canceled");

        this.httpCrawlerResponse.setCrawlerError(new HttpCrawlerError(HttpCrawlerErrorCode.REQUEST_CANCELED_ERROR, "Request canceled by http client"));
        this.responseCompletableFuture.complete(this.httpCrawlerResponse);
    }

    private void redirect(HttpResponse response) {
        logger.info("Redirecting");
        Optional<String> optionalLocation = ApacheHttpCrawlerUtil.getLocation(response, request);

        if (optionalLocation.isPresent()) {

            String location = optionalLocation.get();

            if(this.httpCrawlerResponse.getRedirectionList()==null){
                this.httpCrawlerResponse.setRedirectionList(new LinkedList<>());
            }

            HttpCrawlerRedirection httpCrawlerRedirection = new HttpCrawlerRedirection();
            httpCrawlerRedirection.setUrl(httpCrawlerResponse.getUrl());
            httpCrawlerRedirection.setResponseCode(response.getStatusLine().getStatusCode());
            httpCrawlerRedirection.setResponseMessage(response.getStatusLine().getReasonPhrase());
            httpCrawlerRedirection.setHeaders(ApacheHttpCrawlerUtil.extractHeaders(response));

            this.httpCrawlerResponse.getRedirectionList().add(httpCrawlerRedirection);

            try {
                URL redirection = new URL(location);

                httpCrawlerRedirection.setRedirectUrl(redirection);
                httpCrawlerRedirection.setContent(IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset()));

                HttpUriRequest request = ApacheHttpCrawlerRequestFactory.fromApacheHttpRedirectResponse(response, this.httpCrawlerRequest, redirection);
                logger.info("Calling redirected url: "+request);
                this.httpAsyncClient.execute(request, this);

            } catch (MalformedURLException e) {
                this.httpCrawlerResponse.setCrawlerError(new HttpCrawlerError(HttpCrawlerErrorCode.REDIRECTION_PROCESSING_ERROR, "Incorrect location URL: "+e.getMessage()));
                this.responseCompletableFuture.complete(this.httpCrawlerResponse);
            } catch (ApacheHttpCrawlerClientException e) {
                this.httpCrawlerResponse.setCrawlerError(new HttpCrawlerError(HttpCrawlerErrorCode.REDIRECTION_PROCESSING_ERROR, "Redirection error. "+e.getMessage()));
                this.responseCompletableFuture.complete(this.httpCrawlerResponse);
            } catch (IOException e) {
                logger.error("Can't read redirection response content. "+e.getMessage());
            }
        } else {
            updateResponse(response);
            this.httpCrawlerResponse.setCrawlerError(new HttpCrawlerError(HttpCrawlerErrorCode.REDIRECTION_PROCESSING_ERROR, "Missing location to redirect."));
            this.responseCompletableFuture.complete(this.httpCrawlerResponse);
        }
    }

    private void updateResponse(HttpResponse response) {

        this.httpCrawlerResponse.setResponseCode(response.getStatusLine().getStatusCode());
        this.httpCrawlerResponse.setResponseMessage(response.getStatusLine().getReasonPhrase());
        this.httpCrawlerResponse.setHeaders(ApacheHttpCrawlerUtil.extractHeaders(response));

        try {
            this.httpCrawlerResponse.setContent(IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset()));
        } catch (IOException e) {
            this.httpCrawlerResponse.setCrawlerError(new HttpCrawlerError(HttpCrawlerErrorCode.RESPONSE_CONTENT_EXTRACTING_ERROR, e.getMessage()));
        }
    }
}
