package uk.gov.companieshouse.chskafka.service.strategy;

import uk.gov.companieshouse.api.chskafka.ProcessedFiling;
import uk.gov.companieshouse.chskafka.exceptions.BadRequestRuntimeException;
import uk.gov.companieshouse.chskafka.kafka.filingprocessed.FilingProcessedProducer;
import uk.gov.companieshouse.chskafka.service.KafkaEventStrategy;
import uk.gov.companieshouse.filing.processed.FilingProcessed;
import uk.gov.companieshouse.filing.processed.PresenterRecord;
import uk.gov.companieshouse.filing.processed.RejectRecord;
import uk.gov.companieshouse.filing.processed.ResponseRecord;
import uk.gov.companieshouse.filing.processed.SubmissionRecord;
import uk.gov.companieshouse.logging.util.DataMap;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;

public class FilingProcessStrategy implements KafkaEventStrategy<ProcessedFiling, FilingProcessed> {

    private final FilingProcessedProducer filingProcessedProducer;

    public FilingProcessStrategy(FilingProcessedProducer filingProcessedProducer) {
        this.filingProcessedProducer = filingProcessedProducer;
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
        response.setDateOfCreation(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        response.setProcessedAt(request.getProcessedAt());
        response.setStatus(request.getStatus());
        response.setSubmissionId(request.getSubmissionId());

        // If reject reasons are supplied then populate them, else make the reject fields with empty Lists
        RejectRecord rejectRecord = new RejectRecord();
        if (request.getRejection() != null) {
            rejectRecord.setReasonsEnglish(request.getRejection().getEnglishReasons());
            rejectRecord.setReasonsWelsh(request.getRejection().getWelshReasons());
        } else {
            rejectRecord.setReasonsEnglish(Collections.emptyList());
            rejectRecord.setReasonsWelsh(Collections.emptyList());
        }
        response.setReject(rejectRecord);

        filingProcessed.setResponse(response);
        return filingProcessed;
    }

    @Override
    public void validate(ProcessedFiling request, String xRequestId) {
        DataMap dataMap = new DataMap.Builder().transactionId(request.getTransactionId()).build();
        List<String> missingFields = new ArrayList<>();
        if (request.getPresenter() == null) missingFields.add("presenter");
        if (!hasText(request.getChannelId())) missingFields.add("channelId");
        if (!hasText(request.getProcessedAt())) missingFields.add("processedAt");
        if (!hasText(request.getStatus())) missingFields.add("status");
        if (!hasText(request.getSubmissionId())) missingFields.add("submissionId");
        if (!hasText(request.getTransactionId())) missingFields.add("transactionId");

        if (!missingFields.isEmpty()) {
            String msg = "Missing required fields: " + String.join(", ", missingFields);
            throw new BadRequestRuntimeException(xRequestId, msg, new Exception(msg), dataMap);
        }
    }

    @Override
    public void send(FilingProcessed avroRequest) {
        filingProcessedProducer.publishMessage(avroRequest);
    }
}
