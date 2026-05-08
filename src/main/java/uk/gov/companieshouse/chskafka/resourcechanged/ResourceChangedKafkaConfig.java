package uk.gov.companieshouse.chskafka.resourcechanged;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.chskafka.common.KafkaConfig;
import uk.gov.companieshouse.chskafka.common.kafka.KafkaProducer;
import uk.gov.companieshouse.chskafka.common.kafka.KafkaProducerConfigFactory;
import uk.gov.companieshouse.stream.ResourceChanged;

@Configuration
class ResourceChangedKafkaConfig implements KafkaConfig<ResourceChanged> {
    @Bean(name = "resourceChangedProducerFactory")
    @Override
    public KafkaProducer<ResourceChanged> kafkaProducer(@Value("${kafka.topic.resource-changed}") String topic,
                                                        KafkaProducerConfigFactory producerConfigFactory) {
        return producerConfigFactory.kafkaProducer(topic, ResourceChanged.class);
}
}