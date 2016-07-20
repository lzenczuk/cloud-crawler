package com.github.lzenczuk.crawler.scenario.impl.poloniex.stream;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * Created by dev on 15/07/16.
 */
public interface MessageMapper {
    List<Message> processMessage(JsonNode jsonNode, String sourceMessage);
}
