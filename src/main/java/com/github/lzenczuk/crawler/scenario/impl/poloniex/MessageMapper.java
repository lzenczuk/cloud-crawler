package com.github.lzenczuk.crawler.scenario.impl.poloniex;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by dev on 14/07/16.
 */
public class MessageMapper {

    private static ObjectMapper jsonMapper = new ObjectMapper();

    private static MarketMessageMapper marketMessageMapper = new MarketMessageMapper();

    public void processMessage(String message) throws IOException {
        JsonNode jsonNode = jsonMapper.readTree(message);

        if(jsonNode!=null && jsonNode.isArray()){
            MessageType messageType = messageTypeIdToMessageType(getMessageTypeId(jsonNode));

            if(MessageType.MARKET_EVENT==messageType){
                marketMessageMapper.processMessage(jsonNode);
            }else{
                System.out.println("Other event: "+message);
            }
        }else{
            System.out.println("Something wrong with message: "+message);
        }
    }

    private long getMessageTypeId(JsonNode jsonNode) {
        return jsonNode.get(0).asLong();
    }

    private MessageType messageTypeIdToMessageType(long id) {
        if(id>0 && id<1000) return MessageType.MARKET_EVENT;

        if(id==1000) return MessageType.USER_EVENT;
        if(id==1001) return MessageType.TROLL_BOX_EVENT;
        if(id==1002) return MessageType.TICKER_EVENT;
        if(id==1003) return MessageType.STATS_EVENT;
        if(id==1010) return MessageType.HART_BEAT;

        return MessageType.UNKNOWN_EVENT;
    }
}
