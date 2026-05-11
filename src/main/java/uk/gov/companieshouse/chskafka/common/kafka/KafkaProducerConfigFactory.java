package uk.gov.companieshouse.chskafka.common.kafka;

import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducerConfigFactory {

    private final String bootstrapAddress;

    public KafkaProducerConfigFactory(@Value("${kafka.bootstrap-servers}") String bootstrapAddress) {
        this.bootstrapAddress = bootstrapAddress;
    }

    public <K> KafkaProducer<K> kafkaProducer(String topic, Class<K> type) {
        ProducerFactory<String, K> producerFactory = new DefaultKafkaProducerFactory<>(
                Map.of(
                        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress,
                        ProducerConfig.ACKS_CONFIG, "all",
                        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaPayloadSerialiser.class),
                new StringSerializer(),
                new KafkaPayloadSerialiser<>(type));

        KafkaTemplate<String, K> kafkaTemplate = new KafkaTemplate<>(producerFactory);
        return new KafkaProducer<>(topic, kafkaTemplate);
    }
}
