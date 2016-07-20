package com.github.lzenczuk.crawler.scenario.impl.poloniex.stream.json;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.github.lzenczuk.crawler.scenario.impl.poloniex.stream.Message;

import java.io.IOException;

/**
 * Created by dev on 20/07/16.
 */
public class JsonMessageMapper {

    private ObjectMapper objectMapper;

    public JsonMessageMapper() {
        objectMapper = new ObjectMapper();

        StdTypeResolverBuilder stdTypeResolverBuilder = new StdTypeResolverBuilder();
        stdTypeResolverBuilder.init(JsonTypeInfo.Id.CLASS, null);
        stdTypeResolverBuilder.inclusion(JsonTypeInfo.As.PROPERTY);

        objectMapper.setDefaultTyping(stdTypeResolverBuilder);
    }

    public JsonMessageMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Message parseJsonString(String json) throws IOException {
        return objectMapper.readValue(json, Message.class);
    }

    public String formatMessage(Message message) throws JsonProcessingException {
        return objectMapper.writeValueAsString(message);
    }
}
