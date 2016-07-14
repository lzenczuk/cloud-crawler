package com.github.lzenczuk.crawler.scenario.impl.poloniex;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.lzenczuk.crawler.scenario.impl.poloniex.market.Order;
import com.github.lzenczuk.crawler.scenario.impl.poloniex.market.OrderType;
import com.github.lzenczuk.crawler.scenario.impl.poloniex.market.Trade;
import com.github.lzenczuk.crawler.scenario.impl.poloniex.market.TradeType;

import java.util.Date;

/**
 * Created by dev on 14/07/16.
 */
public class MarketMessageMapper {

    public void processMessage(JsonNode jsonNode){
        long marketId = jsonNode.get(0).asLong();
        Long messageId = null;

        if(jsonNode.get(1).canConvertToLong()){
            messageId = jsonNode.get(1).asLong();
        }

        JsonNode eventsArrayNode = jsonNode.get(2);
        eventsArrayNode.forEach(eventNode -> {
            switch(eventNode.get(0).asText()){
                case "o":
                    OrderType orderType = eventNode.get(1).asInt()==1? OrderType.BID : OrderType.ASK;
                    System.out.println(new Order(orderType, eventNode.get(2).asText(), eventNode.get(3).asText()));
                    break;
                case "t":
                    TradeType tradeType = eventNode.get(2).asInt()==1? TradeType.BUY : TradeType.SELL;
                    int tradeId = eventNode.get(1).asInt();
                    Date tradeDate = new Date(eventNode.get(5).asLong() * 1000);
                    System.out.println(new Trade(tradeId, tradeType, eventNode.get(3).asText(), eventNode.get(4).asText(), tradeDate));
            }
        });
    }
}
