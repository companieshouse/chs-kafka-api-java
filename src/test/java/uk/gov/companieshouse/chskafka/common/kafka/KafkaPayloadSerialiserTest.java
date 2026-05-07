package uk.gov.companieshouse.chskafka.common.kafka;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

import java.io.IOException;
import org.apache.avro.io.DatumWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.chskafka.common.exception.InvalidPayloadException;
import uk.gov.companieshouse.filing.processed.FilingProcessed;
import uk.gov.companieshouse.filing.processed.PresenterRecord;
import uk.gov.companieshouse.filing.processed.ResponseRecord;
import uk.gov.companieshouse.filing.processed.SubmissionRecord;

@ExtendWith(MockitoExtension.class)
class KafkaPayloadSerialiserTest {

    @Mock
    private DatumWriter<FilingProcessed> writer;

    @Test
    void testSerialiseFilingProcessed() {
        // given
        try (KafkaPayloadSerialiser<FilingProcessed> serialiser = new KafkaPayloadSerialiser<>(FilingProcessed.class)) {
            FilingProcessed filingProcessed = new FilingProcessed();
            filingProcessed.setApplicationId("applicationId");
            filingProcessed.setChannelId("channelId");
            PresenterRecord presenter = new PresenterRecord("language", "userId");
            filingProcessed.setPresenter(presenter);
            SubmissionRecord submission = new SubmissionRecord("transactionId");
            filingProcessed.setSubmission(submission);
            ResponseRecord response = new ResponseRecord("companyName", "companyNumber", "dateOfCreation", "processedAt",
                    "status", "submissionId", null);
            filingProcessed.setResponse(response);

            // when
            byte[] actual = serialiser.serialize("topic", filingProcessed);

            // then
            assertTrue(actual.length > 0);
        }
    }

    @Test
    void testSerialiseThrowsNonRetryableExceptionWhenIOException() throws IOException {
        // given
        KafkaPayloadSerialiser<FilingProcessed> serialiser = spy(new KafkaPayloadSerialiser<>(FilingProcessed.class));
        doReturn(writer).when(serialiser).getDatumWriter();
        doThrow(IOException.class).when(writer).write(any(), any());

        // when
        Executable actual = () -> serialiser.serialize("topic", new FilingProcessed());

        // then
        InvalidPayloadException exception = assertThrows(InvalidPayloadException.class, actual);
        assertInstanceOf(IOException.class, exception.getCause());
    }
}
