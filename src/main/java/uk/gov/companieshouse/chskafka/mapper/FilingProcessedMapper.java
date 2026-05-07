package uk.gov.companieshouse.chskafka.mapper;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.chskafka.ProcessedFiling;
import uk.gov.companieshouse.api.chskafka.ProcessedFilingRejection;
import uk.gov.companieshouse.filing.processed.FilingProcessed;
import uk.gov.companieshouse.filing.processed.PresenterRecord;
import uk.gov.companieshouse.filing.processed.RejectRecord;
import uk.gov.companieshouse.filing.processed.ResponseRecord;
import uk.gov.companieshouse.filing.processed.SubmissionRecord;

@Component
public class FilingProcessedMapper implements RequestMapper<ProcessedFiling, FilingProcessed> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final LocalDateTimeSupplier localDateTimeSupplier;

    public FilingProcessedMapper(LocalDateTimeSupplier localDateTimeSupplier) {
        this.localDateTimeSupplier = localDateTimeSupplier;
    }

    @Override
    public FilingProcessed map(ProcessedFiling request) {

        FilingProcessed filingProcessed = new FilingProcessed();

        // Top-level fields
        filingProcessed.setApplicationId(request.getApplicationId());
        filingProcessed.setAttempt(1);
        filingProcessed.setChannelId(request.getChannelId());

        // Presenter
        PresenterRecord presenter = new PresenterRecord();
        presenter.setLanguage(request.getPresenter().getLanguage());
        presenter.setUserId(request.getPresenter().getUserId());
        filingProcessed.setPresenter(presenter);

        // Submission
        SubmissionRecord submission = new SubmissionRecord();
        submission.setTransactionId(request.getTransactionId());
        filingProcessed.setSubmission(submission);

        // Response
        ResponseRecord response = new ResponseRecord();
        response.setCompanyName(request.getCompanyName());
        response.setCompanyNumber(request.getCompanyNumber());
        response.setDateOfCreation(DATE_TIME_FORMATTER.format(localDateTimeSupplier.get()));
        response.setProcessedAt(request.getProcessedAt());
        response.setStatus(request.getStatus());
        response.setSubmissionId(request.getSubmissionId());

        // If reject reasons are supplied then populate them, else make the reject fields with empty Lists
        RejectRecord rejectRecord = new RejectRecord();
        ProcessedFilingRejection rejection = request.getRejection();
        if (rejection != null) {
            rejectRecord.setReasonsEnglish(rejection.getEnglishReasons());
            rejectRecord.setReasonsWelsh(rejection.getWelshReasons());
        } else {
            rejectRecord.setReasonsEnglish(Collections.emptyList());
            rejectRecord.setReasonsWelsh(Collections.emptyList());
        }
        response.setReject(rejectRecord);

        filingProcessed.setResponse(response);
        return filingProcessed;
    }
}
