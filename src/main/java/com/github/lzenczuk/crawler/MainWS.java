package com.github.lzenczuk.crawler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lzenczuk.crawler.scenario.impl.poloniex.MessageMapper;
import org.glassfish.tyrus.client.ClientManager;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by dev on 12/07/16.
 */
public class MainWS {
    public static void main(String[] args) throws URISyntaxException, IOException, DeploymentException, InterruptedException {

        CountDownLatch openLatch = new CountDownLatch(1);
        CountDownLatch closeLatch = new CountDownLatch(1);

        ClientEndpointConfig clientEndpointConfig = ClientEndpointConfig.Builder.create().build();

        AtomicReference<RemoteEndpoint.Basic> basicAtomicReference = new AtomicReference<>(null);

        MessageMapper messageMapper = new MessageMapper();

        ClientManager client = ClientManager.createClient();
        client.connectToServer(new Endpoint() {
                                   @Override
                                   public void onOpen(Session session, EndpointConfig config) {

                                       System.out.println("Open session");

                                       basicAtomicReference.set(session.getBasicRemote());

                                       openLatch.countDown();

                                       session.addMessageHandler(new MessageHandler.Whole<String>() {
                                           @Override
                                           public void onMessage(String message) {
                                               //System.out.println("Receive message: " + message);

                                               try{
                                                messageMapper.processMessage(message);
                                               } catch (IOException e) {
                                                   e.printStackTrace();
                                               }
                                           }
                                       });
                                   }

                                   @Override
                                   public void onClose(Session session, CloseReason closeReason) {
                                       super.onClose(session, closeReason);
                                       System.out.println("On close");

                                       closeLatch.countDown();
                                   }

                                   @Override
                                   public void onError(Session session, Throwable thr) {
                                       super.onError(session, thr);
                                       System.out.println("On error");

                                       closeLatch.countDown();
                                   }
                               },
                clientEndpointConfig,
                new URI("wss://api2.poloniex.com/"));

        System.out.println("Wait for open.");
        openLatch.await();
        System.out.println("Sending message.");

        RemoteEndpoint.Basic remoteEndpoint = basicAtomicReference.get();

        // -----------------------------------------------------------------------------------
        /*
        1001 - Messages between users
        [1001,8429624,"btqiang","its 3000?",13]
        [1001,8429626,"mikeymg2","froggy, +1 .. thats the thing we all need to remember.",0]
        [1001,8429625,"shorts","\/me dusts off teh gold particles ",1971]

        type, messageNumber, username, message, reputation
         */
        // remoteEndpoint.sendText("{\"command\":\"subscribe\",\"channel\":1001}"); // user discussions

        // -----------------------------------------------------------------------------------
        /*
        1002 - Ticker - prices between different currencies

        example message:

        [1002,null,[28,"0.00000185","0.00000185","0.00000175","0.02209944","18.69803536","10941204.48781659",0,"0.00000194","0.00000150"]]
        currencyPair, last, lowestAsk, highestBid, percentChange, baseVolume, quoteVolume, isFrozen, 24hrHigh, 24hrLow

        Pairs ids are from: https://poloniex.com/public?command=returnTicker

        {
            "BTC_1CR":{
                "id":1,
                "last":"0.00047707",
                "lowestAsk":"0.00047707",
                "highestBid":"0.00044989",
                "percentChange":"0.05478785",
                "baseVolume":"0.12650226",
                "quoteVolume":"280.28988983",
                "isFrozen":"0",
                "high24hr":"0.00047708",
                "low24hr":"0.00042183"}
            ...


         */
        //remoteEndpoint.sendText("{\"command\":\"subscribe\",\"channel\":1002}");

        // -----------------------------------------------------------------------------------
        /*
        Summary data in footer:
        24hr Volume: 17407 BTC / 104983 ETH / 3297 XMR / 431083 USDT

        Repeated every 19 of 1010
        ...
        17 - [1010]
        18 - [1010]
        19 - [1010]
        20 - [1003,null,["2016-07-12 16:14",6011,{"BTC":"16316.303","ETH":"105437.219","XMR":"3347.388","USDT":"395794.108"}]]
        01 - [1010]
        ...
         */
        //remoteEndpoint.sendText("{\"command\":\"subscribe\",\"channel\":1003}");

        // -----------------------------------------------------------------------------------
        // 1004 - nothing
        // 1005 - nothing


        // -----------------------------------------------------------------------------------
        /*
        148 - buy, sell operations

        148 == "BTC_ETH" from returnTicker

        plx_exchange.js - line 2290

        case marketChannel:
		    	var args = [];
		    	var kwargs = {seq: msg[1]};
		    	if (logseq){
			    	if (logseq-- <= 0)logseq=false;
			    	console.log(kwargs.seq);
			    }
		    	for (var i in msg[2]){
			    	var arg = msg[2][i];
			    	switch (arg[0]){
			    		case "o":
				    		args.push({
					    		type: "orderBook" + (arg[3] === "0.00000000" ? "Remove" : "Modify"),
					    		data: { type: (arg[1] == 1 ? "bid" : "ask"),
						    			rate: arg[2],
						    			amount: arg[3]
					    		}
				    		});
				    		break;

				    	case "t":
				    		args.push({
					    		type: "newTrade",
					    		data: {	tradeID: arg[1],
						    			type: (arg[2] == 1 ? "buy" : "sell"),
						    			rate: arg[3],
						    			amount: arg[4],
						    			total: fix(parseFloat(arg[3]) * parseFloat(arg[4])),
						    			date: timestampToDate(arg[5],true)
					    		}
				    		});
				    		break;
			    	}
		    	}

         */
        remoteEndpoint.sendText("{\"command\":\"subscribe\",\"channel\":148}");
        //remoteEndpoint.sendText("{\"command\":\"subscribe\",\"channel\":\"BTC_ETH\"}");

        // ?
        //remoteEndpoint.sendText("{\"command\":\"subscribe\",\"channel\":1000}");

        closeLatch.await();
        System.out.println("End");
    }
}
