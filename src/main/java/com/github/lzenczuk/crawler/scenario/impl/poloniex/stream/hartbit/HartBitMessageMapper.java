package com.github.lzenczuk.crawler.scenario.impl.poloniex.stream.hartbit;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.lzenczuk.crawler.scenario.impl.poloniex.stream.ErrorMessage;
import com.github.lzenczuk.crawler.scenario.impl.poloniex.stream.Message;
import com.github.lzenczuk.crawler.scenario.impl.poloniex.stream.MessageMapper;
import com.github.lzenczuk.crawler.scenario.impl.poloniex.stream.MessageMappingErrorType;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by dev on 15/07/16.
 */
public class HartBitMessageMapper implements MessageMapper {
    @Override
    public List<Message> processMessage(JsonNode jsonNode, String sourceMessage) {

        int hb = jsonNode.get(0).asInt();
        if(hb!=1010){
            return Collections.singletonList(new ErrorMessage(
                    MessageMappingErrorType.INVALID_MESSAGE_STRUCTURE,
                    "Incorrect hart bit message id. Expect 1010",
                    sourceMessage
            ));
        }

        return Collections.singletonList(new HartBit(new Date()));
    }
}
