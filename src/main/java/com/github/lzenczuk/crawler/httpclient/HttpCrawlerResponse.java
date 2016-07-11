package com.github.lzenczuk.crawler.httpclient;

import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by dev on 09/07/16.
 */
public class HttpCrawlerResponse {
    private URL url;
    private int responseCode;
    private String responseMessage;
    private Map<String, List<String>> headers;

    private List<HttpCrawlerRedirection> redirectionList;

    private String content;

    private HttpCrawlerError crawlerError;

    public HttpCrawlerResponse() {
    }

    public HttpCrawlerResponse(URL url) {
        this.url = url;
    }

    public HttpCrawlerResponse(HttpCrawlerErrorCode errorCode, String errorMessage) {
        this.crawlerError = new HttpCrawlerError(errorCode, errorMessage);
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
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

    public List<HttpCrawlerRedirection> getRedirectionList() {
        return redirectionList;
    }

    public void setRedirectionList(List<HttpCrawlerRedirection> redirectionList) {
        this.redirectionList = redirectionList;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public HttpCrawlerError getCrawlerError() {
        return crawlerError;
    }

    public void setCrawlerError(HttpCrawlerError crawlerError) {
        this.crawlerError = crawlerError;
    }

    public boolean isError() {
        return crawlerError!=null;
    }

    @Override
    public String toString() {
        return "HttpCrawlerResponse{" +
                "url=" + url +
                ", responseCode=" + responseCode +
                ", responseMessage='" + responseMessage + '\'' +
                ", headers=" + headers +
                ", redirectionList=" + redirectionList +
                ", content='" + content + '\'' +
                ", crawlerError=" + crawlerError +
                '}';
    }
}
