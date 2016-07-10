package com.github.lzenczuk.crawler.httpclient.impl.apache;

import com.github.lzenczuk.crawler.httpclient.HttpCrawlerMethod;
import com.github.lzenczuk.crawler.httpclient.HttpCrawlerRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;

import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by dev on 09/07/16.
 */
public class ApacheHttpCrawlerRequestFactory {

    public static HttpUriRequest fromHttpCrawlerRequest(HttpCrawlerRequest httpCrawlerRequest) throws ApacheHttpCrawlerClientException {

        HttpUriRequest request = mapCrawlerRequestToApacheHttpRequest(httpCrawlerRequest.getMethod(), httpCrawlerRequest.getUrl());

        request.setHeaders(ApacheHttpCrawlerUtil.createHeaders(httpCrawlerRequest.getHeaders()));

        return request;
    }

    private static HttpUriRequest mapCrawlerRequestToApacheHttpRequest(HttpCrawlerMethod method, URL url) throws ApacheHttpCrawlerClientException {
        HttpUriRequest request;

        try {

            switch (method) {
                case GET:
                    request = new HttpGet(url.toURI());
                    break;
                case POST:
                    request = new HttpPost(url.toURI());
                    break;
                case PUT:
                    request = new HttpPut(url.toURI());
                    break;
                default:
                    throw new ApacheHttpCrawlerClientException("Unknown http method: "+ method);
            }
        } catch (URISyntaxException e) {
            throw new ApacheHttpCrawlerClientException("Incorrect URL: "+url+"; "+e.getMessage());
        }
        return request;
    }

    public static HttpUriRequest fromApacheHttpRedirectResponse(HttpResponse response, HttpCrawlerRequest httpCrawlerRequest, URL redirection) throws ApacheHttpCrawlerClientException {
        HttpUriRequest request = mapCrawlerRequestToApacheHttpRequest(httpCrawlerRequest.getMethod(), redirection);

        return request;
    }
}
