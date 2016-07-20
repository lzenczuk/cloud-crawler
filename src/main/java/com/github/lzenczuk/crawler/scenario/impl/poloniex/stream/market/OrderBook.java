package com.github.lzenczuk.crawler.scenario.impl.poloniex.stream.market;

import com.github.lzenczuk.crawler.scenario.impl.poloniex.stream.Message;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dev on 15/07/16.
 */
public class OrderBook implements Message {
    private long marketId;
    private String marketName;
    private Map<BigDecimal, BigDecimal> book;

    public OrderBook(long marketId, String marketName) {
        this.marketId = marketId;
        this.marketName = marketName;

        book = new HashMap<>();
    }

    public long getMarketId() {
        return marketId;
    }

    public void setMarketId(long marketId) {
        this.marketId = marketId;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public Map<BigDecimal, BigDecimal> getBook() {
        return book;
    }

    public void addBookEntry(BigDecimal rate, BigDecimal amount){
        book.put(rate, amount);
    }

    @Override
    public String toString() {
        return "OrderBook{" +
                "marketId=" + marketId +
                ", marketName='" + marketName + '\'' +
                ", book=" + book +
                '}';
    }
}
