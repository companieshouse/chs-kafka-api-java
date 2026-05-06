package uk.gov.companieshouse.chskafka.config;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import uk.gov.companieshouse.chskafka.kafka.KafkaProducer;

public interface KafkaConfig<K> {

    ProducerFactory<String, K> producerFactory(String bootstrapAddress);

    KafkaTemplate<String, K> kafkaTemplate(ProducerFactory<String, K> producerFactory);

    KafkaProducer<K> kafkaProducer(String topic, KafkaTemplate<String, K> kafkaTemplate);
}