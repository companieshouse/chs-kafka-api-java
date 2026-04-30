package uk.gov.companieshouse.chskafka.kafka.filingprocessed;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import uk.gov.companieshouse.chskafka.exceptions.BadGatewayException;
import uk.gov.companieshouse.chskafka.logging.DataMapHolder;
import uk.gov.companieshouse.chskafka.util.TestUtils;
import uk.gov.companieshouse.filing.processed.FilingProcessed;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FilingProcessedKafkaProducerTest {

    private static final String FILING_PROCESSED_TOPIC = "filing-processed";
    private static final String CONTEXT_ID = "context_id";

    @Mock
    private KafkaTemplate<String, FilingProcessed> kafkaTemplate;

    @Mock
    private SendResult<String, FilingProcessed> sendResult;

    private FilingProcessedKafkaProducer filingProcessedKafkaProducer;

    @BeforeEach
    void setup() {
        filingProcessedKafkaProducer = new FilingProcessedKafkaProducer(kafkaTemplate, FILING_PROCESSED_TOPIC);
    }

    @Test
    void shouldPublishFilingProcessedMessage() {
        // given
        FilingProcessed filingProcessed = TestUtils.getFilingProcessed();

        DataMapHolder.get().requestId(CONTEXT_ID);
        when(kafkaTemplate.send(anyString(), any())).thenReturn(CompletableFuture.completedFuture(sendResult));

        // when
        filingProcessedKafkaProducer.publishMessage(filingProcessed);

        // then
        verify(kafkaTemplate).send(FILING_PROCESSED_TOPIC, filingProcessed);
    }

    @Test
    void shouldThrowBadGatewayExceptionWhenKafkaExceptionCaught() {
        // given
        FilingProcessed filingProcessed = TestUtils.getFilingProcessed();

        DataMapHolder.get().requestId(CONTEXT_ID);
        when(kafkaTemplate.send(anyString(), any())).thenThrow(KafkaException.class);

        // when
        Executable executable = () -> filingProcessedKafkaProducer.publishMessage(filingProcessed);

        // then
        assertThrows(BadGatewayException.class, executable);
        verify(kafkaTemplate).send(FILING_PROCESSED_TOPIC, filingProcessed);
    }

    @Test
    void shouldThrowBadGatewayExceptionWhenCompletableFutureFails() {
        // given
        FilingProcessed filingProcessed = TestUtils.getFilingProcessed();

        DataMapHolder.get().requestId(CONTEXT_ID);
        when(kafkaTemplate.send(anyString(), any())).thenReturn(CompletableFuture.failedFuture(new RuntimeException()));

        // when
        Executable executable = () -> filingProcessedKafkaProducer.publishMessage(filingProcessed);

        // then
        assertThrows(BadGatewayException.class, executable);
        verify(kafkaTemplate).send(FILING_PROCESSED_TOPIC, filingProcessed);
    }
}
