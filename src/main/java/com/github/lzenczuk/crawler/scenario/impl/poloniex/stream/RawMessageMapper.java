package com.github.lzenczuk.crawler.scenario.impl.poloniex.stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lzenczuk.crawler.scenario.impl.poloniex.stream.hartbit.HartBitMessageMapper;
import com.github.lzenczuk.crawler.scenario.impl.poloniex.stream.market.MarketMessageMapper;
import com.github.lzenczuk.crawler.scenario.impl.poloniex.stream.ticker.TickerMessageMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created by dev on 14/07/16.
 */
public class RawMessageMapper {

    private static ObjectMapper jsonMapper = new ObjectMapper();

    private static MarketMessageMapper marketMessageMapper = new MarketMessageMapper();
    private static TickerMessageMapper tickerMessageMapper = new TickerMessageMapper();
    private static HartBitMessageMapper hartBitMessageMapper = new HartBitMessageMapper();

    public List<Message> processMessage(String message) throws IOException {
        JsonNode jsonNode = jsonMapper.readTree(message);

        if(jsonNode!=null && jsonNode.isArray()){
            long messageTypeId = getMessageTypeId(jsonNode);
            MessageType messageType = messageTypeIdToMessageType(messageTypeId);

            if(MessageType.MARKET_EVENT==messageType) {
                return marketMessageMapper.processMessage(jsonNode, message);
            }else if(MessageType.TICKER_EVENT==messageType) {
                if (jsonNode.size() == 2) {
                    // subscribe or unsubscribe message: [1002,1] or [1002,0]
                    return Collections.emptyList();
                }
                return tickerMessageMapper.processMessage(jsonNode, message);
            }else if(MessageType.HART_BEAT==messageType){
                return hartBitMessageMapper.processMessage(jsonNode, message);
            }else{
                return Collections.singletonList(
                        new ErrorMessage(
                                MessageMappingErrorType.UNKNOWN_MESSAGE_TYPE,
                                "Unknown message type: "+messageTypeId,
                                message
                        )
                );
            }
        }else{
            return Collections.singletonList(
                    new ErrorMessage(
                            MessageMappingErrorType.INVALID_MESSAGE_STRUCTURE,
                            "Incorrect message structure. Expect json array.",
                            message
                    )
            );
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
