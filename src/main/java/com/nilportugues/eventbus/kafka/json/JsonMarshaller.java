package com.nilportugues.eventbus.kafka.json;

public interface JsonMarshaller<T> {

    String serialize(T event) throws Exception;

    T deserialize(String event) throws Exception;
}
