package uk.gov.companieshouse.chskafka.service.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.chskafka.ProcessedFiling;
import uk.gov.companieshouse.chskafka.exceptions.BadRequestRuntimeException;
import uk.gov.companieshouse.chskafka.kafka.filingprocessed.FilingProcessedProducer;
import uk.gov.companieshouse.chskafka.util.TestUtils;
import uk.gov.companieshouse.filing.processed.FilingProcessed;
import uk.gov.companieshouse.filing.processed.ResponseRecord;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class FilingProcessStrategyTest {

    private final String REQUEST_ID = "13579";

    @Mock
    private FilingProcessedProducer filingProcessedProducer;

    private FilingProcessStrategy filingProcessStrategy;

    @BeforeEach
    void setUp() {
        filingProcessStrategy = new FilingProcessStrategy(filingProcessedProducer);
    }

    @Test
    void testSuccessfullyMapFilingProcessed() {
        // given
        ProcessedFiling processedFiling = TestUtils.getProcessedFiling();

        // when
        FilingProcessed result = filingProcessStrategy.map(processedFiling);

        // then
        assertNotNull(result.getResponse().getDateOfCreation());

        // remove dynamic creation date to do direct comparison between actual and result
        ResponseRecord responseRecordWithoutDate = result.getResponse();
        responseRecordWithoutDate.setDateOfCreation(null);
        result.setResponse(responseRecordWithoutDate);
        assertEquals(TestUtils.getFilingProcessed(), result);
    }

    @Test
    void testSuccessfullyMapFilingProcessedWithRejectReasons() {
        // given
        ProcessedFiling processedFiling = TestUtils.getProcessedFilingWithReject();

        // when
        FilingProcessed result = filingProcessStrategy.map(processedFiling);

        // then
        assertNotNull(result.getResponse().getDateOfCreation());

        // remove dynamic creation date to do direct comparison between actual and result
        ResponseRecord responseRecordWithoutDate = result.getResponse();
        responseRecordWithoutDate.setDateOfCreation(null);
        result.setResponse(responseRecordWithoutDate);
        assertEquals(TestUtils.getFilingProcessedWithReject(), result);
    }

    @Test
    void testSuccessfullyValidateProcessedFiling() {
        // given
        ProcessedFiling processedFiling = TestUtils.getProcessedFiling();

        // when and then
        assertDoesNotThrow(() -> filingProcessStrategy.validate(processedFiling, REQUEST_ID));
    }

    @Test
    void testErrorOneMissingFieldOnValidation() {
        // given
        ProcessedFiling processedFiling = TestUtils.getProcessedFiling();
        processedFiling.setPresenter(null);

        // when
        BadRequestRuntimeException exception = assertThrows(BadRequestRuntimeException.class, () -> {
            filingProcessStrategy.validate(processedFiling, REQUEST_ID);
        });

        // then
        assertEquals("Missing required fields: presenter", exception.getMessage());
    }

    @Test
    void testErrorAllMissingFieldsOnValidation() {
        // given
        ProcessedFiling processedFiling = TestUtils.getProcessedFilingWithReject();
        processedFiling.setPresenter(null);
        processedFiling.setChannelId(null);
        processedFiling.setProcessedAt(null);
        processedFiling.setStatus(null);
        processedFiling.setSubmissionId(null);
        processedFiling.setTransactionId(null);

        // when
        BadRequestRuntimeException exception = assertThrows(BadRequestRuntimeException.class, () -> {
            filingProcessStrategy.validate(processedFiling, REQUEST_ID);
        });

        // then
        assertEquals("Missing required fields: presenter, channelId, processedAt, status, submissionId, transactionId", exception.getMessage());
    }

    @Test
    void testSuccessfullySendMessage() {
        // given
        FilingProcessed filingProcessed = TestUtils.getFilingProcessed();

        // when
        assertDoesNotThrow(() -> {
            filingProcessStrategy.send(filingProcessed);
        });

        // then
        verify(filingProcessedProducer, times(1)).publishMessage(filingProcessed);
    }
}
