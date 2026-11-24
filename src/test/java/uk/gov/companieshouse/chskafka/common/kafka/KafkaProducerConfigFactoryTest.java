package uk.gov.companieshouse.chskafka.common.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import uk.gov.companieshouse.filing.processed.FilingProcessed;

class KafkaProducerConfigFactoryTest {

    @Test
    void shouldConfigureKafkaProducerTimeoutsAndIdempotence() throws Exception {
        // given
        KafkaProducerConfigFactory factory = new KafkaProducerConfigFactory(
                "localhost:9092", 5000, 5000, 10000);

        // when
        KafkaProducer<FilingProcessed> producer = factory.kafkaProducer("filing-processed", FilingProcessed.class);

        // then
        KafkaTemplate<String, FilingProcessed> kafkaTemplate = getPrivateField(producer, "kafkaTemplate", KafkaTemplate.class);
        DefaultKafkaProducerFactory<String, FilingProcessed> producerFactory =
                (DefaultKafkaProducerFactory<String, FilingProcessed>) kafkaTemplate.getProducerFactory();
        Map<String, Object> config = producerFactory.getConfigurationProperties();

        assertEquals("localhost:9092", config.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals("all", config.get(ProducerConfig.ACKS_CONFIG));
        assertEquals(true, config.get(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG));
        assertEquals(5000, config.get(ProducerConfig.MAX_BLOCK_MS_CONFIG));
        assertEquals(5000, config.get(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG));
        assertEquals(10000, config.get(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG));
        assertTrue((int) config.get(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG)
                >= (int) config.get(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG));
    }

    private static <T> T getPrivateField(Object target, String fieldName, Class<T> type)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return type.cast(field.get(target));
    }
}
