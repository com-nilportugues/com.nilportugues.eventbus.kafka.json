package com.nilportugues.eventbus.kafka.json;

import com.nilportugues.eventbus.EventHandlerDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaJsonEventConsumer<T> {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaJsonEventPublisher.class);
    private static final long TIMEOUT = Long.MAX_VALUE;

    private final KafkaConsumer<String, String> consumer;
    private final JsonMarshaller<T> marshaller;
    private final EventHandlerDispatcher dispatcher;

    public KafkaJsonEventConsumer(final EventHandlerDispatcher dispatcher,
        final KafkaConsumerFactory consumerFactory,
        final JsonMarshaller<T> marshaller,
        final String topic) {

        this.consumer = consumerFactory.build(topic);
        this.marshaller = marshaller;
        this.dispatcher = dispatcher;
    }

    public void consume() {

        new Thread(() -> {
            while (true) {
                for (ConsumerRecord<String, String> record : consumer.poll(TIMEOUT)) {
                    forwardToEventHandlers(record);
                }
            }
        }).start();
    }

    private void forwardToEventHandlers(final ConsumerRecord<String, String> record) {
        try {
            T event = marshaller.deserialize(record.value());
            LOG.debug("Consumed: offset = %d, key = %s, value = %s \n", record.offset(), record.key(), event);

            dispatcher.dispatch(event);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e.getMessage());
        }
    }
}
