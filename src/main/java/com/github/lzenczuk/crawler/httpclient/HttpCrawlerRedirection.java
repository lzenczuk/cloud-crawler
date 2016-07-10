package com.github.lzenczuk.crawler.httpclient;

import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by dev on 09/07/16.
 */
public class HttpCrawlerRedirection {
    private URL url;
    private URL redirectUrl;
    private int responseCode;
    private String responseMessage;
    private Map<String, List<String>> headers;
    private String content;

    public HttpCrawlerRedirection() {
    }

    public HttpCrawlerRedirection(URL url, URL redirectUrl, int responseCode, String responseMessage) {
        this.url = url;
        this.redirectUrl = redirectUrl;
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public URL getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(URL redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "HttpCrawlerRedirection{" +
                "url=" + url +
                ", redirectUrl=" + redirectUrl +
                ", responseCode=" + responseCode +
                ", responseMessage='" + responseMessage + '\'' +
                ", headers=" + headers +
                ", content='" + content + '\'' +
                '}';
    }
}
