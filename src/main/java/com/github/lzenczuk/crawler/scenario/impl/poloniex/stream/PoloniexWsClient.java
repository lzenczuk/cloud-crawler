package com.github.lzenczuk.crawler.scenario.impl.poloniex.stream;

import com.github.lzenczuk.crawler.scenario.impl.poloniex.stream.consumer.MessageConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.tyrus.client.ClientManager;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by dev on 19/07/16.
 */
public class PoloniexWsClient {

    private static final Logger logger = LogManager.getLogger(PoloniexWsClient.class);

    private AtomicReference<RemoteEndpoint.Basic> endpointReference;
    private AtomicReference<Session> sessionReference;
    private ClientEndpointConfig clientEndpointConfig;
    private RawMessageMapper rawMessageMapper;

    private final MessageConsumer messageConsumer;

    public PoloniexWsClient(MessageConsumer messageConsumer) {
        this.messageConsumer = messageConsumer;
        endpointReference = new AtomicReference<>(null);
        sessionReference = new AtomicReference<>(null);
        clientEndpointConfig = ClientEndpointConfig.Builder.create().build();

        rawMessageMapper = new RawMessageMapper();
    }

    public void setRawMessageMapper(RawMessageMapper rawMessageMapper) {
        this.rawMessageMapper = rawMessageMapper;
    }

    public CompletableFuture<Void> start() throws URISyntaxException, IOException, DeploymentException, InterruptedException {

        CompletableFuture<Void> stopedFuture = new CompletableFuture<>();
        CountDownLatch initLatch = new CountDownLatch(1);

        ClientManager client = ClientManager.createClient();
        client.connectToServer(new Endpoint() {
                                   @Override
                                   public void onOpen(Session session, EndpointConfig config) {

                                       logger.info("WS session to poloniex opened");

                                       sessionReference.set(session);
                                       endpointReference.set(session.getBasicRemote());

                                       initLatch.countDown();

                                       session.addMessageHandler(new MessageHandler.Whole<String>() {

                                           AtomicLong messageCounter = new AtomicLong();

                                           @Override
                                           public void onMessage(String message) {
                                               try {
                                                   List<Message> messages = rawMessageMapper.processMessage(message);
                                                   if(messageConsumer!=null) {
                                                       messageConsumer.consume(messages);
                                                   }

                                                   long numberOfMessages = messageCounter.incrementAndGet();
                                                   if(numberOfMessages % 100 == 0){
                                                       logger.info("Consumed messages from poloniex: "+numberOfMessages);
                                                   }
                                               } catch (IOException e) {
                                                   logger.error("Error consuming messages: "+e.getMessage());
                                               }
                                           }
                                       });
                                   }

                                   @Override
                                   public void onClose(Session session, CloseReason closeReason) {
                                       super.onClose(session, closeReason);
                                       stopedFuture.complete(null);
                                       initLatch.countDown();
                                   }

                                   @Override
                                   public void onError(Session session, Throwable thr) {
                                       super.onError(session, thr);
                                       stopedFuture.complete(null);
                                       initLatch.countDown();
                                   }
                               },
                clientEndpointConfig,
                new URI("wss://api2.poloniex.com/"));

        initLatch.await();
        return stopedFuture;
    }

    public void subscribe(int marketId, String currencyIn, String currencyFor) throws IOException {
        endpointReference.get().sendText("{\"command\":\"subscribe\",\"channel\":"+marketId+"}");
    }
}
