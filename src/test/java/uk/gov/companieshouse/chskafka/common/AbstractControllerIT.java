package uk.gov.companieshouse.chskafka.common;

import com.google.common.collect.Iterables;
import org.apache.avro.Schema;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.commons.io.IOUtils;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.kafka.ConfluentKafkaContainer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@AutoConfigureMockMvc
@Import(TestKafkaConfig.class)
public abstract class AbstractControllerIT<T> {

    protected static final ConfluentKafkaContainer kafka = new ConfluentKafkaContainer("confluentinc/cp-kafka:7.6.1");

    @BeforeAll
    static void startKafka() {
        kafka.start();
    }

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected KafkaConsumer<String, byte[]> testConsumer;

    private final String topic;
    private final Class<T> type;
    private final Schema schema;

    protected AbstractControllerIT(String topic, Class<T> type, Schema schema) {
        this.topic = topic;
        this.type = type;
        this.schema = schema;
    }

    @BeforeEach
    protected void setup() {
        testConsumer.subscribe(List.of(topic));
        testConsumer.poll(Duration.ofMillis(1000));
    }

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    protected static String readResource(String filename) {
        try {
            return IOUtils.resourceToString(filename, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected T readAndDeserialise(String filename) throws IOException {
        String expectedJson = readResource(filename);
        Decoder decoder = DecoderFactory.get().jsonDecoder(schema, expectedJson);
        DatumReader<T> reader = new SpecificDatumReader<>(type);
        return reader.read(null, decoder);
    }

    protected ResultActions mockMvcPost(String requestBody, String requestUri) throws Exception {
        return mockMvc.perform(post(requestUri)
                .header("ERIC-Identity", "123")
                .header("ERIC-Identity-Type", "key")
                .header("ERIC-Authorised-Key-Privileges", "internal-app")
                .header("X-Request-Id", "test-request-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));
    }

    protected T consumeAndDeserialise() throws IOException {
        ConsumerRecords<?, ?> consumerRecords = KafkaTestUtils.getRecords(testConsumer, Duration.ofMillis(5000L), 1);
        byte[] actualBytes = (byte[]) consumerRecords.records(topic)
                .iterator()
                .next()
                .value();

        Decoder decoder = DecoderFactory.get().binaryDecoder(actualBytes, null);
        DatumReader<T> reader = new SpecificDatumReader<>(type);
        return reader.read(null, decoder);
    }

    protected void assertZeroMessagesPublished() {
        ConsumerRecords<?, ?> consumerRecords = KafkaTestUtils.getRecords(testConsumer, Duration.ofMillis(5000L), 1);
        assertEquals(0, Iterables.size(consumerRecords.records(topic)));
    }
}