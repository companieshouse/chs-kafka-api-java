package uk.gov.companieshouse.chskafka.strikeoffpartnerobjectionsprocessed;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.chskafka.common.KafkaConfig;
import uk.gov.companieshouse.chskafka.common.kafka.KafkaProducer;
import uk.gov.companieshouse.chskafka.common.kafka.KafkaProducerConfigFactory;
import uk.gov.companieshouse.strikeoff.partner.objections.StrikeOffPartnerObjectionsProcessed;

@Configuration
class StrikeOffPartnerObjectionsProcessedKafkaConfig implements KafkaConfig<StrikeOffPartnerObjectionsProcessed> {

    @Bean(name = "strikeOffPartnerObjectionsProcessedFactory")
    @Override
    public KafkaProducer<StrikeOffPartnerObjectionsProcessed> kafkaProducer(@Value("${kafka.topic.strike-off-partner-objections-processed}") String topic,
            KafkaProducerConfigFactory producerConfigFactory) {
        return producerConfigFactory.kafkaProducer(topic, StrikeOffPartnerObjectionsProcessed.class);
    }
}