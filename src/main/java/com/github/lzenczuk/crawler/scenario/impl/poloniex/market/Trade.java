package com.github.lzenczuk.crawler.scenario.impl.poloniex.market;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by dev on 14/07/16.
 */
public class Trade {
    private final long tradeId;
    private final TradeType tradeType;
    private final BigDecimal rate;
    private final BigDecimal amount;
    private final Date date;

    public Trade(long tradeId, TradeType tradeType, String rate, String amount, Date date) {
        this.tradeId = tradeId;
        this.tradeType = tradeType;
        this.rate = new BigDecimal(rate);
        this.amount = new BigDecimal(amount);
        this.date = date;
    }

    public long getTradeId() {
        return tradeId;
    }

    public TradeType getTradeType() {
        return tradeType;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Trade{" +
                "tradeId=" + tradeId +
                ", tradeType=" + tradeType +
                ", rate=" + rate +
                ", amount=" + amount +
                ", date=" + date +
                '}';
    }
}
