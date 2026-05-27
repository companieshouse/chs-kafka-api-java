package uk.gov.companieshouse.chskafka.filingprocessed;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.chskafka.ProcessedFiling;
import uk.gov.companieshouse.api.chskafka.ProcessedFilingPresenter;
import uk.gov.companieshouse.api.chskafka.ProcessedFilingRejection;
import uk.gov.companieshouse.chskafka.common.LocalDateTimeSupplier;
import uk.gov.companieshouse.filing.processed.FilingProcessed;
import uk.gov.companieshouse.filing.processed.PresenterRecord;
import uk.gov.companieshouse.filing.processed.RejectRecord;
import uk.gov.companieshouse.filing.processed.ResponseRecord;
import uk.gov.companieshouse.filing.processed.SubmissionRecord;

@ExtendWith(MockitoExtension.class)
class FilingProcessedMapperTest {

    private static final String APPLICATION_ID = "123";
    private static final String CHANNEL_ID = "321";

    private static final String PRESENTER_LANGUAGE = "English";
    private static final String PRESENTER_USER_ID = "123-456-789";

    private static final String SUBMISSION_TRANSACTION_ID = "987-654-321";

    private static final String RESPONSE_COMPANY_NAME = "test-company";
    private static final String RESPONSE_COMPANY_NUMBER = "00002121";
    private static final String RESPONSE_PROCESSED_AT = "2026-04-16T10:19:56Z";
    private static final String RESPONSE_STATUS = "accepted";
    private static final String RESPONSE_SUBMISSION_ID = "2020-2021";

    private static final String REJECT_REASONS_ENGLISH = "reasons";
    private static final String REJECT_REASONS_WELSH = "rhesymau";
    private static final String LOCAL_DATE_TIME = "2026-04-16T11:19:56";

    @Mock
    private LocalDateTimeSupplier localDateTimeSupplier;
    @InjectMocks
    private FilingProcessedMapper filingProcessedMapper;

    @Test
    void testSuccessfullyMapFilingProcessed() {
        // given
        ProcessedFiling processedFiling = getProcessedFiling();

        when(localDateTimeSupplier.get()).thenReturn(LocalDateTime.parse(LOCAL_DATE_TIME));

        // when
        FilingProcessed result = filingProcessedMapper.map(processedFiling);

        // then
        assertEquals(getFilingProcessed(), result);
        verify(localDateTimeSupplier).get();
    }

    @Test
    void testSuccessfullyMapFilingProcessedWithRejectReasons() {
        // given
        ProcessedFiling processedFiling = getProcessedFilingWithReject();

        when(localDateTimeSupplier.get()).thenReturn(LocalDateTime.parse(LOCAL_DATE_TIME));

        // when
        FilingProcessed result = filingProcessedMapper.map(processedFiling);

        // then
        assertEquals(getFilingProcessedWithReject(), result);
        verify(localDateTimeSupplier).get();
    }

    @NotNull
    private static FilingProcessed getFilingProcessed() {
        FilingProcessed filingProcessed = new FilingProcessed();
        filingProcessed.setApplicationId(APPLICATION_ID);
        filingProcessed.setAttempt(1);
        filingProcessed.setChannelId(CHANNEL_ID);

        PresenterRecord presenterRecord = new PresenterRecord();
        presenterRecord.setLanguage(PRESENTER_LANGUAGE);
        presenterRecord.setUserId(PRESENTER_USER_ID);
        filingProcessed.setPresenter(presenterRecord);

        SubmissionRecord submissionRecord = new SubmissionRecord();
        submissionRecord.setTransactionId(SUBMISSION_TRANSACTION_ID);
        filingProcessed.setSubmission(submissionRecord);

        ResponseRecord responseRecord = new ResponseRecord();
        RejectRecord rejectRecord = new RejectRecord();
        rejectRecord.setReasonsEnglish(Collections.emptyList());
        rejectRecord.setReasonsWelsh(Collections.emptyList());
        responseRecord.setReject(rejectRecord);
        responseRecord.setCompanyName(RESPONSE_COMPANY_NAME);
        responseRecord.setCompanyNumber(RESPONSE_COMPANY_NUMBER);
        responseRecord.setProcessedAt(RESPONSE_PROCESSED_AT);
        responseRecord.setStatus(RESPONSE_STATUS);
        responseRecord.setSubmissionId(RESPONSE_SUBMISSION_ID);
        responseRecord.setDateOfCreation(LOCAL_DATE_TIME);
        filingProcessed.setResponse(responseRecord);

        return filingProcessed;
    }

    private static FilingProcessed getFilingProcessedWithReject() {
        FilingProcessed filingProcessed = getFilingProcessed();
        ResponseRecord responseRecord = filingProcessed.getResponse();
        RejectRecord rejectRecord = new RejectRecord();
        rejectRecord.setReasonsEnglish(Collections.singletonList(REJECT_REASONS_ENGLISH));
        rejectRecord.setReasonsWelsh(Collections.singletonList(REJECT_REASONS_WELSH));
        responseRecord.setReject(rejectRecord);
        filingProcessed.setResponse(responseRecord);

        return filingProcessed;
    }

    private static ProcessedFiling getProcessedFiling() {
        ProcessedFiling processedFiling = new ProcessedFiling();
        processedFiling.setApplicationId(APPLICATION_ID);
        processedFiling.setChannelId(CHANNEL_ID);

        ProcessedFilingPresenter processedFilingPresenter = new ProcessedFilingPresenter();
        processedFilingPresenter.setLanguage(PRESENTER_LANGUAGE);
        processedFilingPresenter.setUserId(PRESENTER_USER_ID);
        processedFiling.setPresenter(processedFilingPresenter);

        processedFiling.setTransactionId(SUBMISSION_TRANSACTION_ID);
        processedFiling.setCompanyName(RESPONSE_COMPANY_NAME);
        processedFiling.setCompanyNumber(RESPONSE_COMPANY_NUMBER);
        processedFiling.setProcessedAt(RESPONSE_PROCESSED_AT);
        processedFiling.setStatus(RESPONSE_STATUS);
        processedFiling.setSubmissionId(RESPONSE_SUBMISSION_ID);

        return processedFiling;
    }

    private static ProcessedFiling getProcessedFilingWithReject() {
        ProcessedFiling processedFiling = getProcessedFiling();
        ProcessedFilingRejection rejectRecord = new ProcessedFilingRejection();
        rejectRecord.setEnglishReasons(Collections.singletonList(REJECT_REASONS_ENGLISH));
        rejectRecord.setWelshReasons(Collections.singletonList(REJECT_REASONS_WELSH));
        processedFiling.setRejection(rejectRecord);

        return processedFiling;
    }
}
