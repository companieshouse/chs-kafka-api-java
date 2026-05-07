package uk.gov.companieshouse.chskafka.filingprocessed;

import static uk.gov.companieshouse.chskafka.Application.LOGGER;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.chskafka.ProcessedFiling;
import uk.gov.companieshouse.chskafka.common.Controller;
import uk.gov.companieshouse.chskafka.common.Service;
import uk.gov.companieshouse.chskafka.common.logging.DataMapHolder;
import uk.gov.companieshouse.filing.processed.FilingProcessed;

@RestController
class FilingProcessedController implements Controller<ProcessedFiling> {

    private final Service<ProcessedFiling, FilingProcessed> service;

    FilingProcessedController(Service<ProcessedFiling, FilingProcessed> service) {
        this.service = service;
    }

    @PostMapping("/private/filing-processed")
    @Override
    public ResponseEntity<Void> processRequest(@Valid @RequestBody ProcessedFiling processedFiling) {
        DataMapHolder.get()
                .chsUserId(processedFiling.getPresenter().getUserId())
                .transactionId(processedFiling.getTransactionId());
        LOGGER.info("Received filing processed request", DataMapHolder.getLogMap());
        service.processAndPublish(processedFiling);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
