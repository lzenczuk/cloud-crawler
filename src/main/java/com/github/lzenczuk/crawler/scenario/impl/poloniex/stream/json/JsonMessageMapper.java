package com.github.lzenczuk.crawler.scenario.impl.poloniex.stream.json;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.jsontype.*;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.github.lzenczuk.crawler.scenario.impl.poloniex.stream.Message;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by dev on 20/07/16.
 */
public class JsonMessageMapper {

    private ObjectMapper objectMapper;

    public JsonMessageMapper() {
        objectMapper = new ObjectMapper();
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
