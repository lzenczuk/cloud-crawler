package com.github.lzenczuk.crawler.scenario.impl.poloniex.stream;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Created by dev on 15/07/16.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.PROPERTY, use = JsonTypeInfo.Id.CLASS, property = "messageType")
public interface Message {
}
