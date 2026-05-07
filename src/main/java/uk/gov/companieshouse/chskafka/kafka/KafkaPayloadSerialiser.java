package uk.gov.companieshouse.chskafka.kafka;

import static uk.gov.companieshouse.chskafka.Application.LOGGER;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.apache.kafka.common.serialization.Serializer;
import uk.gov.companieshouse.chskafka.exception.InvalidPayloadException;

public class KafkaPayloadSerialiser<T> implements Serializer<T> {

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
            throw new InvalidPayloadException("Error serialising message payload", ex);
        }
        return outputStream.toByteArray();
    }

    public DatumWriter<T> getDatumWriter() {
        return new ReflectDatumWriter<>(type);
    }
}
