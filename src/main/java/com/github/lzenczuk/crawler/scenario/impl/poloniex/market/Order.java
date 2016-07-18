package com.github.lzenczuk.crawler.scenario.impl.poloniex.market;

import com.github.lzenczuk.crawler.scenario.impl.poloniex.Message;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by dev on 14/07/16.
 */
public class Order implements Message {
    private long marketId;
    private long messageId;
    private OrderType orderType;
    private BigDecimal rate;
    private BigDecimal amount;
    private Date receiveDate;

    public Order() {
    }

    public Order(long marketId, long messageId, OrderType orderType, String rate, String amount, Date receiveDate) {
        this.marketId = marketId;
        this.messageId = messageId;
        this.orderType = orderType;
        this.rate = new BigDecimal(rate);
        this.amount = new BigDecimal(amount);
        this.receiveDate = receiveDate;
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

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
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

    public Date getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(Date receiveDate) {
        this.receiveDate = receiveDate;
    }

    @Override
    public String toString() {
        return "Order{" +
                "marketId=" + marketId +
                ", messageId=" + messageId +
                ", orderType=" + orderType +
                ", rate=" + rate +
                ", amount=" + amount +
                ", receiveDate=" + receiveDate +
                '}';
    }
}
