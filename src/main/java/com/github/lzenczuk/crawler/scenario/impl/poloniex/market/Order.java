package com.github.lzenczuk.crawler.scenario.impl.poloniex.market;

import java.math.BigDecimal;

/**
 * Created by dev on 14/07/16.
 */
public class Order {
    private final OrderType orderType;
    private final BigDecimal rate;
    private final BigDecimal amount;

    public Order(OrderType orderType, String rate, String amount) {
        this.orderType = orderType;
        this.rate = new BigDecimal(rate);
        this.amount = new BigDecimal(amount);
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderType=" + orderType +
                ", rate=" + rate +
                ", amount=" + amount +
                '}';
    }
}
