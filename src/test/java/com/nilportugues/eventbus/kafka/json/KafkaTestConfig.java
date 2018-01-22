package com.nilportugues.eventbus.kafka.json;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.BytesDeserializer;
import org.apache.kafka.common.serialization.BytesSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.UUID;

public class KafkaTestConfig {

    public static Properties buildProducerProps() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "kafka:9092");

        // MANDATORY
        props.put("key.serializer", BytesSerializer.class);
        props.put("value.serializer", StringSerializer.class);
        return props;
    }

    public static Properties buildConsumerProps() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "kafka:9092");
        props.put("session.timeout.ms", "6000");
        props.put("group.id", "event_consumer_json");
        props.put("client.id", UUID.randomUUID().toString());

        // FOR TESTING PURPOSES
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // MANDATORY
        props.put("key.deserializer", BytesDeserializer.class);
        props.put("value.deserializer", StringDeserializer.class);
        return props;
    }
}
