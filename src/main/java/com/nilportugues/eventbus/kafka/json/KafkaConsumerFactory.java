package com.nilportugues.eventbus.kafka.json;

import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class KafkaConsumerFactory {

    private final Properties props;
    private Map<String, KafkaConsumer<String, String>> consumers = new HashMap<>();

    public KafkaConsumerFactory(final Properties properties) {
        this.props = properties;
    }

    public KafkaConsumer<String, String> build(final String topic) {
        if (null != consumers.get(topic)) {
            return consumers.get(topic);
        }

        final KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(topic));
        consumers.put(topic, consumer);

        return consumer;
    }
}
