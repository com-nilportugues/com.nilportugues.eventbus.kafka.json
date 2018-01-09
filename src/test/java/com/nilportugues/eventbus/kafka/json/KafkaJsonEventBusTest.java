package com.nilportugues.eventbus.kafka.json;

import com.nilportugues.eventbus.*;
import com.nilportugues.eventbus.kafka.json.sample.UserRegisteredEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.ZonedDateTime;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class KafkaJsonEventBusTest {

    private static EventBus eventBus;
    private static SpyKafkaJsonEventConsumer eventConsumer;
    private static SpyKafkaJsonEventPublisher eventPublisher;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    public void cleanUpStreams() {
        System.setOut(null);
        System.setErr(null);
    }

    @BeforeClass
    public static void setUp() {
        String topic = "test_topic_" + ZonedDateTime.now().getNano();

        final JsonMarshallerImpl<UserRegisteredEvent> marshaller = new JsonMarshallerImpl<>(UserRegisteredEvent.class);

        // Create Produces and its dependencies
        final Properties producerProps = KafkaTestConfig.buildProducerProps();
        final KafkaProducerFactory producerFactory = new KafkaProducerFactory(producerProps);
        eventPublisher = new SpyKafkaJsonEventPublisher<>(producerFactory, marshaller, topic);

        // Create Consumer and its dependencies
        final ClassResolverImpl classResolver = new ClassResolverImpl();
        final TestBeanProvider beanProvider = new TestBeanProvider();
        final Properties consumerProps = KafkaTestConfig.buildConsumerProps();
        final EventSubscriber eventSubscriber = new EventSubscriber(beanProvider, classResolver);
        final EventHandlerDispatcherImpl eventDispatcher = new EventHandlerDispatcherImpl(eventSubscriber, classResolver);
        final KafkaConsumerFactory consumerFactory = new KafkaConsumerFactory(consumerProps);
        eventConsumer = new SpyKafkaJsonEventConsumer(eventDispatcher, consumerFactory, marshaller, topic);

        // Create Event bus
        eventBus = new EventBus(eventPublisher);

        // Register Event Handlers
        eventSubscriber.subscribe(UserRegisteredEvent.class, "EventHandler");
    }

    @Test
    public void testItCanProduceAndConsume() throws Exception {

        final UserRegisteredEvent event = new UserRegisteredEvent();
        event.hello = "world";
        event.id = UUID.randomUUID().toString();

        eventBus.publish(event);
        eventConsumer.consume();

        Thread.sleep(5000);

        final String expected = eventPublisher.getPublishedEvent();
        final String actual = eventConsumer.getConsumedEvent();

        System.out.println("EXPECTED:\t" + expected);
        System.out.println("ACTUAL:\t\t" + actual);

        Assert.assertEquals(expected, actual);
    }

    private static class SpyKafkaJsonEventPublisher<T> extends KafkaJsonEventPublisher<T> {
        private final JsonMarshaller<T> marshaller;
        private String publishedEvent;

        public SpyKafkaJsonEventPublisher(KafkaProducerFactory factory,
            JsonMarshaller<T> marshaller,
            String topic) {

            super(factory, marshaller, topic);
            this.marshaller = marshaller;
        }

        @Override
        public void publish(final T event) {
            super.publish(event);
            try {
                this.publishedEvent = marshaller.serialize(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String getPublishedEvent() {
            return publishedEvent;
        }
    }

    private static class SpyKafkaJsonEventConsumer extends KafkaJsonEventConsumer {

        private static final int TIMEOUT = 100;

        private final KafkaConsumer<String, String> consumer;
        private final EventHandlerDispatcher dispatcher;
        private final JsonMarshaller marshaller;
        private String consumedEvent;

        SpyKafkaJsonEventConsumer(EventHandlerDispatcher dispatcher,
            KafkaConsumerFactory consumerFactory,
            JsonMarshaller marshaller,
            String topic) {

            super(dispatcher, consumerFactory, marshaller, topic);
            this.consumer = consumerFactory.build(topic);
            this.dispatcher = dispatcher;
            this.marshaller = marshaller;
        }

        @Override
        public void consume() {
            while(true) {
                for (ConsumerRecord<String, String> record : consumer.poll(TIMEOUT)) {
                    if (null != record.value()) {
                        consumedEvent = record.value();

                        try {
                            dispatcher.dispatch(marshaller.deserialize(consumedEvent));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                    System.out.println("record.value is null ...");
                }
            }
        }

        String getConsumedEvent() {
            return consumedEvent;
        }
    }

    private static class TestBeanProvider implements com.nilportugues.eventbus.BeanProvider {
        @Override
        public Object get(String s) {
            System.out.println("Running the bean provider for: " + s);
            return new EventHandler();
        }
    }

    private static class EventHandler implements com.nilportugues.eventbus.EventHandler<UserRegisteredEvent> {

        @Override
        public CompletableFuture<Void> handle(UserRegisteredEvent o) {
            System.out.println("Running the event handler for: " + o.getClass().getCanonicalName());
            Assert.assertTrue(true);
            return CompletableFuture.runAsync(() -> {
            });
        }
    }
}
