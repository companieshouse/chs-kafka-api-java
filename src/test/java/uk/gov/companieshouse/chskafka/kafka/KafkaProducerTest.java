package uk.gov.companieshouse.chskafka.kafka;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import uk.gov.companieshouse.chskafka.exception.BadGatewayException;
import uk.gov.companieshouse.filing.processed.FilingProcessed;

@ExtendWith(MockitoExtension.class)
class KafkaProducerTest {

    private static final String FILING_PROCESSED_TOPIC = "filing-processed";

    @Mock
    private KafkaTemplate<String, FilingProcessed> kafkaTemplate;
    private KafkaProducer<FilingProcessed> kafkaProducer;

    @Mock
    private FilingProcessed filingProcessed;
    @Mock
    private SendResult<String, FilingProcessed> sendResult;

    @BeforeEach
    void setup() {
        kafkaProducer = new KafkaProducer<>(FILING_PROCESSED_TOPIC, kafkaTemplate);
    }

    @Test
    void shouldPublishFilingProcessedMessage() {
        // given
        when(kafkaTemplate.send(anyString(), any())).thenReturn(CompletableFuture.completedFuture(sendResult));

        // when
        kafkaProducer.publishMessage(filingProcessed);

        // then
        verify(kafkaTemplate).send(FILING_PROCESSED_TOPIC, filingProcessed);
    }

    @Test
    void shouldThrowBadGatewayExceptionWhenKafkaExceptionCaught() {
        // given
        when(kafkaTemplate.send(anyString(), any())).thenThrow(KafkaException.class);

        // when
        Executable executable = () -> kafkaProducer.publishMessage(filingProcessed);

        // then
        assertThrows(BadGatewayException.class, executable);
        verify(kafkaTemplate).send(FILING_PROCESSED_TOPIC, filingProcessed);
    }

    @Test
    void shouldThrowBadGatewayExceptionWhenCompletableFutureFails() {
        // given
        when(kafkaTemplate.send(anyString(), any())).thenReturn(CompletableFuture.failedFuture(new RuntimeException()));

        // when
        Executable executable = () -> kafkaProducer.publishMessage(filingProcessed);

        // then
        assertThrows(BadGatewayException.class, executable);
        verify(kafkaTemplate).send(FILING_PROCESSED_TOPIC, filingProcessed);
    }
}
