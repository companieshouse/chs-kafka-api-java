package uk.gov.companieshouse.chskafka.common;

import uk.gov.companieshouse.chskafka.common.kafka.KafkaProducer;
import uk.gov.companieshouse.chskafka.common.kafka.KafkaProducerConfigFactory;

public interface KafkaConfig<K> {

    KafkaProducer<K> kafkaProducer(String topic, KafkaProducerConfigFactory producerConfigFactory);
}