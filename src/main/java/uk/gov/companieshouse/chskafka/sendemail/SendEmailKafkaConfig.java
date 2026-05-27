package uk.gov.companieshouse.chskafka.sendemail;

import email.email_send;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.chskafka.common.KafkaConfig;
import uk.gov.companieshouse.chskafka.common.kafka.KafkaProducer;
import uk.gov.companieshouse.chskafka.common.kafka.KafkaProducerConfigFactory;

@Configuration
class SendEmailKafkaConfig implements KafkaConfig<email_send> {

    @Bean(name = "emailSendProducerFactory")
    @Override
    public KafkaProducer<email_send> kafkaProducer(@Value("${kafka.topic.email-send}") String topic,
                                                   KafkaProducerConfigFactory producerConfigFactory) {
        return producerConfigFactory.kafkaProducer(topic, email_send.class);
    }
}
