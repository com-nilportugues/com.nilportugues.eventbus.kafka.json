package com.nilportugues.eventbus.kafka.json;

import com.nilportugues.eventbus.EventPublisher;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

public class KafkaJsonEventPublisher<T> implements EventPublisher<T> {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaJsonEventPublisher.class);

    private final KafkaProducer<String, String> producer;
    private final JsonMarshaller<T> marshaller;
    private final String topic;

    public KafkaJsonEventPublisher(final KafkaProducerFactory factory,
        final JsonMarshaller<T> marshaller,
        final String topic) {

        this.producer = factory.build();
        this.marshaller = marshaller;
        this.topic = topic;
    }

    @Override
    public void publish(final T event) {
        try {
            final String eventString = marshaller.serialize(event);
            publishToKafka(topic, eventString);
            LOG.debug(eventString);
        } catch (Exception exception) {
            exception.printStackTrace();
            LOG.error(exception.getMessage());
        }
    }

    private void publishToKafka(final String topicName,
        final String event) throws ExecutionException, InterruptedException {

        LOG.debug("---------------------------------------------");
        LOG.debug("Sending message to {}...", topicName);

        final RecordMetadata m = producer.send(new ProducerRecord<>(topicName, event)).get();

        LOG.debug("Message produced, offset: " + m.offset());
        LOG.debug("Message produced, partition : " + m.partition());
        LOG.debug("Message produced, topic: " + m.topic());
        LOG.debug("---------------------------------------------");
    }
}
