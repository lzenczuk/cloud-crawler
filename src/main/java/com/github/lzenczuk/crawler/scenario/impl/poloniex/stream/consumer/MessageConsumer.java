package com.github.lzenczuk.crawler.scenario.impl.poloniex.stream.consumer;

import com.github.lzenczuk.crawler.scenario.impl.poloniex.stream.Message;

import java.util.List;

/**
 * Created by dev on 20/07/16.
 */
public interface MessageConsumer {
    void consume(List<Message> messages);
}
