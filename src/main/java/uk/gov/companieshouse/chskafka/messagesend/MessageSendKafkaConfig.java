package uk.gov.companieshouse.chskafka.messagesend;

import email.message_send;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import uk.gov.companieshouse.chskafka.common.KafkaConfig;
import uk.gov.companieshouse.chskafka.common.kafka.KafkaPayloadSerialiser;
import uk.gov.companieshouse.chskafka.common.kafka.KafkaProducer;
import uk.gov.companieshouse.chskafka.common.kafka.KafkaUtils;

@Configuration
class MessageSendKafkaConfig implements KafkaConfig<message_send> {

    @Bean(name = "messageSendProducerFactory")
    @Override
    public ProducerFactory<String, message_send> producerFactory(
            @Value("${kafka.bootstrap-servers}") String bootstrapAddress) {
        return new DefaultKafkaProducerFactory<>(
                KafkaUtils.defaultProducerProps(bootstrapAddress),
                new StringSerializer(),
                new KafkaPayloadSerialiser<>(message_send.class));
    }

    @Bean(name = "messageSendKafkaTemplate")
    @Override
    public KafkaTemplate<String, message_send> kafkaTemplate(
            @org.springframework.beans.factory.annotation.Qualifier("messageSendProducerFactory")
            ProducerFactory<String, message_send> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean(name = "messageSendKafkaProducer")
    @Override
    public KafkaProducer<message_send> kafkaProducer(
            @Value("${kafka.topic.message-send}") String topic,
            @org.springframework.beans.factory.annotation.Qualifier("messageSendKafkaTemplate")
            KafkaTemplate<String, message_send> kafkaTemplate) {
        return new KafkaProducer<>(topic, kafkaTemplate);
    }
}
