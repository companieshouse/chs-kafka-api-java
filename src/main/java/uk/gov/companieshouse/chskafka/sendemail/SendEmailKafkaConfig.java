package uk.gov.companieshouse.chskafka.sendemail;

import java.util.Map;
import email.email_send;
import org.apache.kafka.clients.producer.ProducerConfig;
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

@Configuration
class SendEmailKafkaConfig implements KafkaConfig<email_send> {

    @Bean
    @Override
    public ProducerFactory<String, email_send> producerFactory(
            @Value("${kafka.bootstrap-servers}") String bootstrapAddress) {
        return new DefaultKafkaProducerFactory<>(
                Map.of(
                        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress,
                        ProducerConfig.ACKS_CONFIG, "all",
                        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaPayloadSerialiser.class),
                new StringSerializer(),
                new KafkaPayloadSerialiser<>(email_send.class));
    }

    @Bean
    @Override
    public KafkaTemplate<String, email_send> kafkaTemplate(ProducerFactory<String, email_send> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    @Override
    public KafkaProducer<email_send> kafkaProducer(@Value("${kafka.topic.email-send}") String topic,
                                                        KafkaTemplate<String, email_send> kafkaTemplate) {
        return new KafkaProducer<>(topic, kafkaTemplate);
    }
}