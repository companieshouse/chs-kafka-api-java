package uk.gov.companieshouse.chskafka.kafka.filingprocessed;

import static uk.gov.companieshouse.chskafka.Application.NAMESPACE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.apache.kafka.common.serialization.Serializer;
import uk.gov.companieshouse.chskafka.exceptions.BadGatewayException;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

public class KafkaPayloadSerialiser<T> implements Serializer<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NAMESPACE);

    private final Class<T> type;

    public KafkaPayloadSerialiser(Class<T> type) {
        this.type = type;
    }

    @Override
    public byte[] serialize(String topic, T data) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Encoder encoder = EncoderFactory.get().directBinaryEncoder(outputStream, null);
        DatumWriter<T> writer = getDatumWriter();
        try {
            writer.write(data, encoder);
        } catch (IOException ex) {
            LOGGER.error("Error serialising message payload", ex);
            throw new BadGatewayException("Error serialising message payload", ex);
        }
        return outputStream.toByteArray();
    }

    public DatumWriter<T> getDatumWriter() {
        return new ReflectDatumWriter<>(type);
    }
}
