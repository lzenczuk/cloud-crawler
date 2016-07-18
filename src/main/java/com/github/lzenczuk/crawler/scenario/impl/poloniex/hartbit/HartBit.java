package com.github.lzenczuk.crawler.scenario.impl.poloniex.hartbit;

import com.github.lzenczuk.crawler.scenario.impl.poloniex.Message;

import java.util.Date;

/**
 * Created by dev on 15/07/16.
 */
public class HartBit implements Message {
    private Date receiveDate;

    public HartBit(Date receiveDate) {
        this.receiveDate = receiveDate;
    }

    public Date getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(Date receiveDate) {
        this.receiveDate = receiveDate;
    }

    @Override
    public String toString() {
        return "HartBit{" +
                "receiveDate=" + receiveDate +
                '}';
    }
}
