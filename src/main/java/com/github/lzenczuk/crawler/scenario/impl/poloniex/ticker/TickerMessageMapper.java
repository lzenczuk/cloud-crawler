package com.github.lzenczuk.crawler.scenario.impl.poloniex.ticker;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.lzenczuk.crawler.scenario.impl.poloniex.ErrorMessage;
import com.github.lzenczuk.crawler.scenario.impl.poloniex.Message;
import com.github.lzenczuk.crawler.scenario.impl.poloniex.MessageMapper;
import com.github.lzenczuk.crawler.scenario.impl.poloniex.MessageMappingErrorType;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by dev on 15/07/16.
 */
public class TickerMessageMapper implements MessageMapper {

    @Override
    public List<Message> processMessage(JsonNode jsonNode, String sourceMessage) {

        LinkedList<Message> resultMessages = new LinkedList<>();

        if (jsonNode.size() != 3) {
            resultMessages.add(new ErrorMessage(
                    MessageMappingErrorType.INVALID_MESSAGE_STRUCTURE,
                    "Incorrect number of message elements. Expect 3",
                    sourceMessage
            ));

            return resultMessages;
        }

        JsonNode eventNode = jsonNode.get(2);

        int marketId = eventNode.get(0).asInt();

        Ticker ticker = new Ticker(
                marketId,
                eventNode.get(1).asText(),
                eventNode.get(2).asText(),
                eventNode.get(3).asText(),
                eventNode.get(4).asText(),
                eventNode.get(5).asText(),
                eventNode.get(6).asText(),
                eventNode.get(7).asInt(),
                eventNode.get(8).asText(),
                eventNode.get(9).asText(),
                new Date()
        );

        resultMessages.add(ticker);

        return resultMessages;
    }
}
