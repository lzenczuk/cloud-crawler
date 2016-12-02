package com.github.lzenczuk.crawler;

import com.github.lzenczuk.crawler.httpclient.HttpCrawlerMethod;
import com.github.lzenczuk.crawler.httpclient.HttpCrawlerRequest;
import com.github.lzenczuk.crawler.httpclient.HttpCrawlerResponse;
import com.github.lzenczuk.crawler.httpclient.impl.apache.ApacheHttpCrawlerClient;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Created by dev on 29/11/16.
 */
public class MainWykop {

    public static void main(String[] args) throws MalformedURLException, InterruptedException, ExecutionException, FileNotFoundException {
        ApacheHttpCrawlerClient apacheHttpCrawlerClient = new ApacheHttpCrawlerClient();

        //http://www.wykop.pl/link/3474825
        // http://www.wykop.pl/ajax2/links/Upvoters/3475525/
        // http://www.wykop.pl/ajax2/links/downvoters/3475525

        // Not there yet
        //http://www.wykop.pl/wpis/18863629/


        for(long id=2000400; id<2001000; id++) {

            System.out.println("---------------> get "+id);

            HttpCrawlerRequest request = new HttpCrawlerRequest(new URL("http://www.wykop.pl/link/"+id), HttpCrawlerMethod.GET);

            HttpCrawlerResponse response1 = apacheHttpCrawlerClient.process(request).get();

            if (response1.getResponseCode() == 200) {
                System.out.println("---------------> get "+id+" OK");
                PrintWriter writer1 = new PrintWriter("/home/dev/Documents/html/wykop/pages/" + id + ".html");
                writer1.write(response1.getContent());
                writer1.close();

                System.out.println("---------------> get "+id+" up");
                HttpCrawlerRequest upRequest = new HttpCrawlerRequest(new URL("http://www.wykop.pl/ajax2/links/Upvoters/"+id), HttpCrawlerMethod.GET);
                HttpCrawlerResponse upResponse = apacheHttpCrawlerClient.process(upRequest).get();
                if(upResponse.getResponseCode() == 200){
                    System.out.println("---------------> get "+id+" up OK");
                    PrintWriter upWriter = new PrintWriter("/home/dev/Documents/html/wykop/pages/" + id + "up.txt");
                    upWriter.write(upResponse.getContent());
                    upWriter.close();
                }

                System.out.println("---------------> get "+id+" down");
                HttpCrawlerRequest downRequest = new HttpCrawlerRequest(new URL("http://www.wykop.pl/ajax2/links/downvoters/"+id), HttpCrawlerMethod.GET);
                HttpCrawlerResponse downResponse = apacheHttpCrawlerClient.process(downRequest).get();
                if(downResponse.getResponseCode() == 200){
                    System.out.println("---------------> get "+id+" down OK");
                    PrintWriter downWriter = new PrintWriter("/home/dev/Documents/html/wykop/pages/" + id + "down.txt");
                    downWriter.write(downResponse.getContent());
                    downWriter.close();
                }
            }
        }
    }
}
