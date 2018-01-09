package com.nilportugues.eventbus.kafka.json;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

public class JsonMarshallerImpl<T extends Serializable> implements JsonMarshaller<T> {

    private final static ObjectMapper objectMapper = new ObjectMapper();
    private final Class<T> clazz;

    public JsonMarshallerImpl(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public String serialize(T event) throws Exception {
        return objectMapper.writeValueAsString(event);
    }

    @Override
    public T deserialize(String event) throws Exception {
        return objectMapper.readValue(event, clazz);
    }
}
