package uk.gov.companieshouse.chskafka.filingprocessed;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.chskafka.common.KafkaConfig;
import uk.gov.companieshouse.chskafka.common.kafka.KafkaProducer;
import uk.gov.companieshouse.chskafka.common.kafka.KafkaProducerConfigFactory;
import uk.gov.companieshouse.filing.processed.FilingProcessed;

@Configuration
class FilingProcessedKafkaConfig implements KafkaConfig<FilingProcessed> {

    @Bean(name = "filingProcessedProducerFactory")
    @Override
    public KafkaProducer<FilingProcessed> kafkaProducer(@Value("${kafka.topic.filing-processed}") String topic,
            KafkaProducerConfigFactory producerConfigFactory) {
        return producerConfigFactory.kafkaProducer(topic, FilingProcessed.class);
    }
}