package uk.gov.companieshouse.chskafka.kafka;

import static uk.gov.companieshouse.chskafka.Application.LOGGER;

import java.util.concurrent.CompletionException;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import uk.gov.companieshouse.chskafka.exceptions.BadGatewayException;
import uk.gov.companieshouse.chskafka.logging.DataMapHolder;

public class KafkaProducer<T> {

    private final String topic;
    private final KafkaTemplate<String, T> kafkaTemplate;

    public KafkaProducer(String topic, KafkaTemplate<String, T> kafkaTemplate) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishMessage(T message) {
        try {
            DataMapHolder.get().topic(topic);
            LOGGER.info("Publishing message to Kafka", DataMapHolder.getLogMap());
            kafkaTemplate.send(topic, message).join();
        } catch (CompletionException ex) {
            final String msg = "Completion error during Kafka send future";
            LOGGER.error(msg, ex, DataMapHolder.getLogMap());
            throw new BadGatewayException(msg, ex);
        } catch (KafkaException ex) {
            final String msg = "Error publishing to Kafka";
            LOGGER.error(msg, ex, DataMapHolder.getLogMap());
            throw new BadGatewayException(msg, ex);
        }
    }
}
