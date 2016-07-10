package com.github.lzenczuk.crawler.httpclient.impl.apache;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicHeader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by dev on 10/07/16.
 */
public class ApacheHttpCrawlerUtil {

    private static final Logger logger = LogManager.getLogger(ApacheHttpCrawlerUtil.class);

    public static Optional<String> getLocation(HttpResponse response, HttpUriRequest request) {
        return Optional.ofNullable(response.getLastHeader("Location")).map(Header::getValue).map(location -> {
            if(location.startsWith("http://") || location.startsWith("https://")){
                logger.debug("Location with full url: "+location);
                return location;
            }else{
                logger.debug("Relative location: "+location);
                String host = request.getURI().getHost();
                int port = request.getURI().getPort();
                String scheme = request.getURI().getScheme();
                String fullUrl = scheme+"://"+host+":"+port+location;
                logger.debug("Full url location: "+fullUrl);
                return fullUrl;
            }
        });
    }

    public static boolean isRedirection(HttpResponse response) {
        return response.getStatusLine().getStatusCode() >= 300 && response.getStatusLine().getStatusCode() < 400;
    }

    public static HashMap<String, List<String>> extractHeaders(HttpResponse response) {
        HashMap<String, List<String>> headers = new HashMap<>();

        Arrays.asList(response.getAllHeaders()).forEach(header -> {
            if(!headers.containsKey(header.getName())){
                headers.put(header.getName(), new LinkedList<>());
            }

            headers.get(header.getName()).add(header.getValue());
        });
        return headers;
    }

    public static Header[] createHeaders(Map<String, List<String>> headersMap) {

        Header[] noHeaders = {};
        if(headersMap==null) return noHeaders;

        List<BasicHeader> headers = headersMap.entrySet().stream().flatMap(entry -> {
            String headerName = entry.getKey();
            return entry.getValue().stream().map(headerValue -> new BasicHeader(headerName, headerValue));
        }).collect(Collectors.toList());

        return headers.toArray(new Header[headers.size()]);
    }
}
