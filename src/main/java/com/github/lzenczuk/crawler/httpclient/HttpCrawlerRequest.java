package com.github.lzenczuk.crawler.httpclient;

import java.net.URL;
import java.util.Map;

/**
 * Created by dev on 09/07/16.
 */
public class HttpCrawlerRequest {
    private URL url;
    private HttpCrawlerMethod method;
    private boolean redirect;
    private Map<String, String> headers;

    public HttpCrawlerRequest() {
        this.redirect = true;
    }

    public HttpCrawlerRequest(URL url, HttpCrawlerMethod method) {
        this.url = url;
        this.method = method;
        this.redirect = true;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public HttpCrawlerMethod getMethod() {
        return method;
    }

    public void setMethod(HttpCrawlerMethod method) {
        this.method = method;
    }

    public boolean isRedirect() {
        return redirect;
    }

    public void setRedirect(boolean redirect) {
        this.redirect = redirect;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public String toString() {
        return "HttpCrawlerRequest{" +
                "url=" + url +
                ", method=" + method +
                ", redirect=" + redirect +
                ", headers=" + headers +
                '}';
    }
}
