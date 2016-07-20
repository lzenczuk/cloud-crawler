package com.github.lzenczuk.crawler.scenario.impl.poloniex.stream.ticker;

import com.github.lzenczuk.crawler.scenario.impl.poloniex.stream.Message;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by dev on 15/07/16.
 */
public class Ticker implements Message{
    private long marketId;
    private BigDecimal last;
    private BigDecimal lowestAsk;
    private BigDecimal highestBid;
    private BigDecimal percentageChange;
    private BigDecimal baseVolume;
    private BigDecimal quoteVolume;
    private boolean frozen;
    private BigDecimal high24;
    private BigDecimal low24;
    private Date receiveDate;

    public Ticker(long marketId, String last, String lowestAsk, String highestBid, String percentageChange, String baseVolume, String quoteVolume, int frozen, String high24, String low24, Date receiveDate) {
        this.marketId = marketId;
        this.last = new BigDecimal(last);
        this.lowestAsk = new BigDecimal(lowestAsk);
        this.highestBid = new BigDecimal(highestBid);
        this.percentageChange = new BigDecimal(percentageChange);
        this.baseVolume = new BigDecimal(baseVolume);
        this.quoteVolume = new BigDecimal(quoteVolume);
        this.frozen = frozen==1? true : false;
        this.high24 = new BigDecimal(high24);
        this.low24 = new BigDecimal(low24);
        this.receiveDate = receiveDate;
    }

    public long getMarketId() {
        return marketId;
    }

    public void setMarketId(long marketId) {
        this.marketId = marketId;
    }

    public BigDecimal getLast() {
        return last;
    }

    public void setLast(BigDecimal last) {
        this.last = last;
    }

    public BigDecimal getLowestAsk() {
        return lowestAsk;
    }

    public void setLowestAsk(BigDecimal lowestAsk) {
        this.lowestAsk = lowestAsk;
    }

    public BigDecimal getHighestBid() {
        return highestBid;
    }

    public void setHighestBid(BigDecimal highestBid) {
        this.highestBid = highestBid;
    }

    public BigDecimal getPercentageChange() {
        return percentageChange;
    }

    public void setPercentageChange(BigDecimal percentageChange) {
        this.percentageChange = percentageChange;
    }

    public BigDecimal getBaseVolume() {
        return baseVolume;
    }

    public void setBaseVolume(BigDecimal baseVolume) {
        this.baseVolume = baseVolume;
    }

    public BigDecimal getQuoteVolume() {
        return quoteVolume;
    }

    public void setQuoteVolume(BigDecimal quoteVolume) {
        this.quoteVolume = quoteVolume;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    public BigDecimal getHigh24() {
        return high24;
    }

    public void setHigh24(BigDecimal high24) {
        this.high24 = high24;
    }

    public BigDecimal getLow24() {
        return low24;
    }

    public void setLow24(BigDecimal low24) {
        this.low24 = low24;
    }

    public Date getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(Date receiveDate) {
        this.receiveDate = receiveDate;
    }

    @Override
    public String toString() {
        return "Ticker{" +
                "marketId=" + marketId +
                ", last=" + last +
                ", lowestAsk=" + lowestAsk +
                ", highestBid=" + highestBid +
                ", percentageChange=" + percentageChange +
                ", baseVolume=" + baseVolume +
                ", quoteVolume=" + quoteVolume +
                ", frozen=" + frozen +
                ", high24=" + high24 +
                ", low24=" + low24 +
                ", receiveDate=" + receiveDate +
                '}';
    }
}
