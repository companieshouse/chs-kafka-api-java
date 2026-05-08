package uk.gov.companieshouse.chskafka.messagesend;

import email.message_send;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.chskafka.common.KafkaConfig;
import uk.gov.companieshouse.chskafka.common.kafka.KafkaProducer;
import uk.gov.companieshouse.chskafka.common.kafka.KafkaProducerConfigFactory;

@Configuration
class MessageSendKafkaConfig implements KafkaConfig<message_send> {

    @Bean(name = "messageSendProducerFactory")
    @Override
    public KafkaProducer<message_send> kafkaProducer(@Value("${kafka.topic.message-send}") String topic,
            KafkaProducerConfigFactory producerConfigFactory) {
        return producerConfigFactory.kafkaProducer(topic, message_send.class);
    }
}
