package com.github.lzenczuk.crawler.scenario.impl.poloniex.stream.market;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.lzenczuk.crawler.scenario.impl.poloniex.stream.Message;
import com.github.lzenczuk.crawler.scenario.impl.poloniex.stream.MessageMapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by dev on 14/07/16.
 */
public class MarketMessageMapper implements MessageMapper {

    @Override
    public List<Message> processMessage(JsonNode jsonNode, String sourceMessage){

        LinkedList<Message> resultMessages = new LinkedList<>();

        long marketId = jsonNode.get(0).asLong();
        long messageId = jsonNode.get(1).asLong();

        Date receiveDate = new Date();

        JsonNode eventsArrayNode = jsonNode.get(2);
        eventsArrayNode.forEach(eventNode -> {
            switch(eventNode.get(0).asText()){
                case "o":
                    OrderType orderType = eventNode.get(1).asInt()==1? OrderType.BID : OrderType.ASK;
                    resultMessages.add(new Order(marketId, messageId, orderType, eventNode.get(2).asText(), eventNode.get(3).asText(), receiveDate));
                    break;
                case "t":
                    TradeType tradeType = eventNode.get(2).asInt()==1? TradeType.BUY : TradeType.SELL;
                    int tradeId = eventNode.get(1).asInt();
                    Date tradeDate = new Date(eventNode.get(5).asLong() * 1000);
                    resultMessages.add(new Trade(marketId, messageId, tradeId, tradeType, eventNode.get(3).asText(), eventNode.get(4).asText(), tradeDate));
                    break;
                case "i":
                    JsonNode orderBookNode = eventNode.get(1);

                    OrderBook book = new OrderBook(marketId, orderBookNode.get("currencyPair").asText());

                    JsonNode orderBook = orderBookNode.get("orderBook").get(0);
                    orderBook.fieldNames().forEachRemaining(key -> {
                        String value = orderBook.get(key).asText();
                        book.addBookEntry(new BigDecimal(key), new BigDecimal(value));
                    });

                    resultMessages.add(book);
            }
        });

        return resultMessages;
    }
}
