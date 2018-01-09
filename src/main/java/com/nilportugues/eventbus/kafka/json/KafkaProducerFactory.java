package com.nilportugues.eventbus.kafka.json;

import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.Properties;

public class KafkaProducerFactory {

    private final Properties props;

    public KafkaProducerFactory(final Properties properties) {
        this.props = properties;
    }

    public KafkaProducer<String, String> build() {
        return new KafkaProducer<>(props);
    }
}
