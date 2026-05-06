package uk.gov.companieshouse.chskafka.config;

import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import uk.gov.companieshouse.chskafka.kafka.KafkaPayloadSerialiser;
import uk.gov.companieshouse.chskafka.kafka.KafkaProducer;
import uk.gov.companieshouse.filing.processed.FilingProcessed;

@Configuration
public class FilingProcessedKafkaConfig implements KafkaConfig<FilingProcessed> {

    @Bean
    @Override
    public ProducerFactory<String, FilingProcessed> producerFactory(
            @Value("${kafka.bootstrap-servers}") String bootstrapAddress) {
        return new DefaultKafkaProducerFactory<>(
                Map.of(
                        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress,
                        ProducerConfig.ACKS_CONFIG, "all",
                        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaPayloadSerialiser.class),
                new StringSerializer(),
                new KafkaPayloadSerialiser<>(FilingProcessed.class));
    }

    @Bean
    @Override
    public KafkaTemplate<String, FilingProcessed> kafkaTemplate(ProducerFactory<String, FilingProcessed> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    @Override
    public KafkaProducer<FilingProcessed> kafkaProducer(@Value("${kafka.topic.filing-processed}") String topic,
            KafkaTemplate<String, FilingProcessed> kafkaTemplate) {
        return new KafkaProducer<>(topic, kafkaTemplate);
    }
}