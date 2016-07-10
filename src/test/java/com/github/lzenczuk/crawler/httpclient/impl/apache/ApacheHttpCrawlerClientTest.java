package com.github.lzenczuk.crawler.httpclient.impl.apache;

import com.github.lzenczuk.crawler.httpclient.*;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;

/**
 * Created by dev on 09/07/16.
 */
public class ApacheHttpCrawlerClientTest{

    private static final Logger logger = LogManager.getLogger(ApacheHttpCrawlerClientTest.class);

    public static final int HTTP_MOCK_SERVER_PROXY = 8089;

    private ClientAndServer serverMock;

    @Before
    public void startMockServer(){
        serverMock = startClientAndServer(HTTP_MOCK_SERVER_PROXY);
    }

    @After
    public void stopMockServer(){
        if(serverMock!=null){
            serverMock.stop();
        }

        serverMock=null;
    }

    @Test
    public void shouldProcessSuccessfulRequestWithoutRedirection() throws MalformedURLException, InterruptedException, ExecutionException, TimeoutException {
        serverMock.when(
                org.mockserver.model.HttpRequest.request()
                        .withMethod("GET")
                        .withHeaders(
                                new Header("rq_header1", "header1_value1"),
                                new Header("rq_header1", "header1_value2"),
                                new Header("rq_header2", "header2_value"),
                                new Header("rq_header3", "header3_value")
                        )
                        .withPath("/success")
        ).respond(
                org.mockserver.model.HttpResponse.response()
                        .withStatusCode(200)
                        .withHeaders(
                                new Header("header1", "header1_value1"),
                                new Header("header1", "header1_value2"),
                                new Header("header2", "header2_value"),
                                new Header("header3", "header3_value")
                        )
                        .withBody("Content OK")
        );

        ApacheHttpCrawlerClient httpCrawlerClient = new ApacheHttpCrawlerClient();

        URL url = new URL("http://localhost:" + HTTP_MOCK_SERVER_PROXY + "/success");

        HashMap<String, List<String>> headers = new HashMap<>();
        headers.put("rq_header1", Arrays.asList("header1_value1", "header1_value2"));
        headers.put("rq_header2", Arrays.asList("header2_value"));
        headers.put("rq_header3", Arrays.asList("header3_value"));

        HttpCrawlerRequest httpCrawlerRequest = new HttpCrawlerRequest(url, HttpCrawlerMethod.GET);
        httpCrawlerRequest.setHeaders(headers);

        CompletableFuture<HttpCrawlerResponse> future = httpCrawlerClient.process(httpCrawlerRequest);

        HttpCrawlerResponse httpCrawlerResponse = future.get(1000L, TimeUnit.MILLISECONDS);

        assertNotNull(httpCrawlerClient);

        assertNull(httpCrawlerResponse.getCrawlerError());

        assertEquals(url,httpCrawlerResponse.getUrl());
        assertEquals(200, httpCrawlerResponse.getResponseCode());
        assertEquals("OK", httpCrawlerResponse.getResponseMessage());
        assertEquals("Content OK", httpCrawlerResponse.getContent());

        if(httpCrawlerResponse.getRedirectionList()!=null){
            assertEquals(0, httpCrawlerResponse.getRedirectionList().size());
        }

        assertNotNull(httpCrawlerResponse.getHeaders());

        assertTrue(httpCrawlerResponse.getHeaders().containsKey("header1"));
        assertEquals(2, httpCrawlerResponse.getHeaders().get("header1").size());
        assertTrue(httpCrawlerResponse.getHeaders().get("header1").contains("header1_value1"));
        assertTrue(httpCrawlerResponse.getHeaders().get("header1").contains("header1_value2"));

        assertTrue(httpCrawlerResponse.getHeaders().containsKey("header2"));
        assertEquals(1, httpCrawlerResponse.getHeaders().get("header2").size());
        assertTrue(httpCrawlerResponse.getHeaders().get("header2").contains("header2_value"));

        assertTrue(httpCrawlerResponse.getHeaders().containsKey("header3"));
        assertEquals(1, httpCrawlerResponse.getHeaders().get("header3").size());
        assertTrue(httpCrawlerResponse.getHeaders().get("header3").contains("header3_value"));
    }

    @Test
    public void shouldProcess404RequestWithoutRedirection() throws MalformedURLException, InterruptedException, ExecutionException, TimeoutException {
        serverMock.when(
                org.mockserver.model.HttpRequest.request()
                        .withMethod("GET")
                        .withHeaders(
                                new Header("rq_header1", "header1_value1"),
                                new Header("rq_header1", "header1_value2"),
                                new Header("rq_header2", "header2_value"),
                                new Header("rq_header3", "header3_value")
                        )
                        .withPath("/404")
        ).respond(
                org.mockserver.model.HttpResponse.response()
                        .withStatusCode(404)
                        .withHeaders(
                                new Header("header1", "header1_value1"),
                                new Header("header1", "header1_value2"),
                                new Header("header2", "header2_value"),
                                new Header("header3", "header3_value")
                        )
                        .withBody("Content 404")
        );

        ApacheHttpCrawlerClient httpCrawlerClient = new ApacheHttpCrawlerClient();

        URL url = new URL("http://localhost:" + HTTP_MOCK_SERVER_PROXY + "/404");

        HashMap<String, List<String>> headers = new HashMap<>();
        headers.put("rq_header1", Arrays.asList("header1_value1", "header1_value2"));
        headers.put("rq_header2", Arrays.asList("header2_value"));
        headers.put("rq_header3", Arrays.asList("header3_value"));

        HttpCrawlerRequest httpCrawlerRequest = new HttpCrawlerRequest(url, HttpCrawlerMethod.GET);
        httpCrawlerRequest.setHeaders(headers);

        CompletableFuture<HttpCrawlerResponse> future = httpCrawlerClient.process(httpCrawlerRequest);

        HttpCrawlerResponse httpCrawlerResponse = future.get(1000L, TimeUnit.MILLISECONDS);

        assertNotNull(httpCrawlerClient);

        assertNull(httpCrawlerResponse.getCrawlerError());

        assertEquals(url,httpCrawlerResponse.getUrl());
        assertEquals(404, httpCrawlerResponse.getResponseCode());
        assertEquals("Not Found", httpCrawlerResponse.getResponseMessage());
        assertEquals("Content 404", httpCrawlerResponse.getContent());

        if(httpCrawlerResponse.getRedirectionList()!=null){
            assertEquals(0, httpCrawlerResponse.getRedirectionList().size());
        }

        assertNotNull(httpCrawlerResponse.getHeaders());

        assertTrue(httpCrawlerResponse.getHeaders().containsKey("header1"));
        assertEquals(2, httpCrawlerResponse.getHeaders().get("header1").size());
        assertTrue(httpCrawlerResponse.getHeaders().get("header1").contains("header1_value1"));
        assertTrue(httpCrawlerResponse.getHeaders().get("header1").contains("header1_value2"));

        assertTrue(httpCrawlerResponse.getHeaders().containsKey("header2"));
        assertEquals(1, httpCrawlerResponse.getHeaders().get("header2").size());
        assertTrue(httpCrawlerResponse.getHeaders().get("header2").contains("header2_value"));

        assertTrue(httpCrawlerResponse.getHeaders().containsKey("header3"));
        assertEquals(1, httpCrawlerResponse.getHeaders().get("header3").size());
        assertTrue(httpCrawlerResponse.getHeaders().get("header3").contains("header3_value"));
    }

    @Test
    public void shouldTimeoutAfterDefaultTimeoutWhenServerNotResponding() throws MalformedURLException, InterruptedException, ExecutionException, TimeoutException {
        serverMock.when(
                org.mockserver.model.HttpRequest.request()
                        .withMethod("GET")
                        .withHeaders(
                                new Header("rq_header1", "header1_value1"),
                                new Header("rq_header1", "header1_value2"),
                                new Header("rq_header2", "header2_value"),
                                new Header("rq_header3", "header3_value")
                        )
                        .withPath("/success")
        ).respond(
                org.mockserver.model.HttpResponse.response()
                        .withStatusCode(200)
                        .withHeaders(
                                new Header("header1", "header1_value1"),
                                new Header("header1", "header1_value2"),
                                new Header("header2", "header2_value"),
                                new Header("header3", "header3_value")
                        )
                        .withBody("Content OK")
                        .withDelay(TimeUnit.MILLISECONDS, ApacheHttpCrawlerClient.DEFAULT_TIMEOUT+100)
        );

        ApacheHttpCrawlerClient httpCrawlerClient = new ApacheHttpCrawlerClient();

        URL url = new URL("http://localhost:" + HTTP_MOCK_SERVER_PROXY + "/success");

        HashMap<String, List<String>> headers = new HashMap<>();
        headers.put("rq_header1", Arrays.asList("header1_value1", "header1_value2"));
        headers.put("rq_header2", Arrays.asList("header2_value"));
        headers.put("rq_header3", Arrays.asList("header3_value"));

        HttpCrawlerRequest httpCrawlerRequest = new HttpCrawlerRequest(url, HttpCrawlerMethod.GET);
        httpCrawlerRequest.setHeaders(headers);

        CompletableFuture<HttpCrawlerResponse> future = httpCrawlerClient.process(httpCrawlerRequest);

        HttpCrawlerResponse httpCrawlerResponse = future.get(ApacheHttpCrawlerClient.DEFAULT_TIMEOUT+200, TimeUnit.MILLISECONDS);

        assertNotNull(httpCrawlerClient);
        assertNotNull(httpCrawlerResponse.getCrawlerError());
        assertEquals(HttpCrawlerErrorCode.REQUEST_TIMEOUT_ERROR, httpCrawlerResponse.getCrawlerError().getErrorCode());
    }

    @Test
    public void shouldProcessRequestWhenServerNotExists() throws MalformedURLException, InterruptedException, ExecutionException, TimeoutException {
        serverMock.stop();

        ApacheHttpCrawlerClient httpCrawlerClient = new ApacheHttpCrawlerClient();

        URL url = new URL("http://localhost:" + HTTP_MOCK_SERVER_PROXY + "/success");

        HashMap<String, List<String>> headers = new HashMap<>();
        headers.put("rq_header1", Arrays.asList("header1_value1", "header1_value2"));
        headers.put("rq_header2", Arrays.asList("header2_value"));
        headers.put("rq_header3", Arrays.asList("header3_value"));

        HttpCrawlerRequest httpCrawlerRequest = new HttpCrawlerRequest(url, HttpCrawlerMethod.GET);
        httpCrawlerRequest.setHeaders(headers);

        CompletableFuture<HttpCrawlerResponse> future = httpCrawlerClient.process(httpCrawlerRequest);

        HttpCrawlerResponse httpCrawlerResponse = future.get(ApacheHttpCrawlerClient.DEFAULT_TIMEOUT+200, TimeUnit.MILLISECONDS);

        assertNotNull(httpCrawlerClient);
        assertNotNull(httpCrawlerResponse.getCrawlerError());
        assertEquals(HttpCrawlerErrorCode.CONNECTION_ERROR, httpCrawlerResponse.getCrawlerError().getErrorCode());
    }

    @Test
    public void shouldProcessSuccessfulRequestWithRedirection() throws MalformedURLException, InterruptedException, ExecutionException, TimeoutException {
        serverMock.when(
                org.mockserver.model.HttpRequest.request()
                        .withMethod("GET")
                        .withHeaders(
                                new Header("rq_header1", "header1_value1"),
                                new Header("rq_header1", "header1_value2"),
                                new Header("rq_header2", "header2_value"),
                                new Header("rq_header3", "header3_value")
                        )
                        .withPath("/redirect")
        ).respond(
                org.mockserver.model.HttpResponse.response()
                        .withStatusCode(301)
                        .withHeaders(
                                new Header("header1", "header1_value1"),
                                new Header("header1", "header1_value2"),
                                new Header("header2", "header2_value"),
                                new Header("header3", "header3_value"),
                                new Header("Location", "/redirected")
                        )
                        .withBody("Content redirect")
        );

        serverMock.when(
                org.mockserver.model.HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/redirected")
        ).respond(
                org.mockserver.model.HttpResponse.response()
                        .withStatusCode(200)
                        .withHeaders(
                                new Header("header1", "header1_value1"),
                                new Header("header1", "header1_value2"),
                                new Header("header2", "header2_value"),
                                new Header("header3", "header3_value")
                        )
                        .withBody("Content redirected OK")
        );

        ApacheHttpCrawlerClient httpCrawlerClient = new ApacheHttpCrawlerClient();

        URL url = new URL("http://localhost:" + HTTP_MOCK_SERVER_PROXY + "/redirect");

        HashMap<String, List<String>> headers = new HashMap<>();
        headers.put("rq_header1", Arrays.asList("header1_value1", "header1_value2"));
        headers.put("rq_header2", Arrays.asList("header2_value"));
        headers.put("rq_header3", Arrays.asList("header3_value"));

        HttpCrawlerRequest httpCrawlerRequest = new HttpCrawlerRequest(url, HttpCrawlerMethod.GET);
        httpCrawlerRequest.setHeaders(headers);

        CompletableFuture<HttpCrawlerResponse> future = httpCrawlerClient.process(httpCrawlerRequest);

        HttpCrawlerResponse httpCrawlerResponse = future.get(5000L, TimeUnit.MILLISECONDS);

        assertNotNull(httpCrawlerClient);

        assertNull(httpCrawlerResponse.getCrawlerError());

        assertEquals(url, httpCrawlerResponse.getUrl());
        assertEquals(200, httpCrawlerResponse.getResponseCode());
        assertEquals("OK", httpCrawlerResponse.getResponseMessage());
        assertEquals("Content redirected OK", httpCrawlerResponse.getContent());

        assertNotNull(httpCrawlerResponse.getRedirectionList());
        assertEquals(1, httpCrawlerResponse.getRedirectionList().size());

        HttpCrawlerRedirection redirection = httpCrawlerResponse.getRedirectionList().get(0);
        assertEquals(url, redirection.getUrl());
        assertEquals(new URL("http://localhost:" + HTTP_MOCK_SERVER_PROXY + "/redirected"), redirection.getRedirectUrl());
        assertEquals(301, redirection.getResponseCode());
        assertEquals("Moved Permanently", redirection.getResponseMessage());
        assertEquals("Content redirect", redirection.getContent());

        assertNotNull(httpCrawlerResponse.getHeaders());

        assertTrue(httpCrawlerResponse.getHeaders().containsKey("header1"));
        assertEquals(2, httpCrawlerResponse.getHeaders().get("header1").size());
        assertTrue(httpCrawlerResponse.getHeaders().get("header1").contains("header1_value1"));
        assertTrue(httpCrawlerResponse.getHeaders().get("header1").contains("header1_value2"));

        assertTrue(httpCrawlerResponse.getHeaders().containsKey("header2"));
        assertEquals(1, httpCrawlerResponse.getHeaders().get("header2").size());
        assertTrue(httpCrawlerResponse.getHeaders().get("header2").contains("header2_value"));

        assertTrue(httpCrawlerResponse.getHeaders().containsKey("header3"));
        assertEquals(1, httpCrawlerResponse.getHeaders().get("header3").size());
        assertTrue(httpCrawlerResponse.getHeaders().get("header3").contains("header3_value"));
    }
}
