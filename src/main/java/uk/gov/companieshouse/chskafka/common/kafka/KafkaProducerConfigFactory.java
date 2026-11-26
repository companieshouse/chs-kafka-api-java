package uk.gov.companieshouse.chskafka.common.kafka;

import java.util.Map;
import org.apache.avro.util.ClassSecurityValidator;
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
    private final int maxBlockMs;
    private final int requestTimeoutMs;
    private final int deliveryTimeoutMs;

    public KafkaProducerConfigFactory(
            @Value("${kafka.bootstrap-servers}") String bootstrapAddress,
            @Value("${kafka.producer.max-block-ms}") int maxBlockMs,
            @Value("${kafka.producer.request-timeout-ms}") int requestTimeoutMs,
            @Value("${kafka.producer.delivery-timeout-ms}") int deliveryTimeoutMs) {
        // Avro 1.12+ requires explicit class trust for serialization.
        // This accepts all classes. Long term, we could consider using the schema registry to avoid
        // runtime class validation entirely. However, that would make integration testing more complex.
        ClassSecurityValidator.setGlobal((clazz -> true));
        this.bootstrapAddress = bootstrapAddress;
        this.maxBlockMs = maxBlockMs;
        this.requestTimeoutMs = requestTimeoutMs;
        this.deliveryTimeoutMs = deliveryTimeoutMs;
    }

    public <K> KafkaProducer<K> kafkaProducer(String topic, Class<K> type) {
        ProducerFactory<String, K> producerFactory = new DefaultKafkaProducerFactory<>(
                Map.of(
                        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress,
                        ProducerConfig.ACKS_CONFIG, "all",
                        ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true,
                        ProducerConfig.MAX_BLOCK_MS_CONFIG, maxBlockMs,
                        ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeoutMs,
                        ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, deliveryTimeoutMs),
                new StringSerializer(),
                new KafkaPayloadSerialiser<>(type));

        KafkaTemplate<String, K> kafkaTemplate = new KafkaTemplate<>(producerFactory);
        kafkaTemplate.setObservationEnabled(true);
        return new KafkaProducer<>(topic, kafkaTemplate);
    }
}
