package com.github.lzenczuk.crawler.scenario.impl.poloniex.market;

import com.github.lzenczuk.crawler.scenario.impl.poloniex.Message;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by dev on 14/07/16.
 */
public class Trade implements Message{
    private long marketId;
    private long messageId;
    private long tradeId;
    private TradeType tradeType;
    private BigDecimal rate;
    private BigDecimal amount;
    private Date date;

    public Trade() {
    }

    public Trade(long marketId, long messageId, long tradeId, TradeType tradeType, String rate, String amount, Date date) {
        this.marketId = marketId;
        this.messageId = messageId;
        this.tradeId = tradeId;
        this.tradeType = tradeType;
        this.rate = new BigDecimal(rate);
        this.amount = new BigDecimal(amount);
        this.date = date;
    }

    public long getMarketId() {
        return marketId;
    }

    public void setMarketId(long marketId) {
        this.marketId = marketId;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getTradeId() {
        return tradeId;
    }

    public void setTradeId(long tradeId) {
        this.tradeId = tradeId;
    }

    public TradeType getTradeType() {
        return tradeType;
    }

    public void setTradeType(TradeType tradeType) {
        this.tradeType = tradeType;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Trade{" +
                "marketId=" + marketId +
                ", messageId=" + messageId +
                ", tradeId=" + tradeId +
                ", tradeType=" + tradeType +
                ", rate=" + rate +
                ", amount=" + amount +
                ", date=" + date +
                '}';
    }
}
