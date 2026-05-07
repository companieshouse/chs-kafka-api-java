package uk.gov.companieshouse.chskafka.controller;

import static uk.gov.companieshouse.chskafka.Application.LOGGER;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.chskafka.ProcessedFiling;
import uk.gov.companieshouse.chskafka.logging.DataMapHolder;
import uk.gov.companieshouse.chskafka.service.KafkaService;
import uk.gov.companieshouse.filing.processed.FilingProcessed;

@RestController
public class FilingProcessedController {

    private final KafkaService<ProcessedFiling, FilingProcessed> kafkaService;

    public FilingProcessedController(KafkaService<ProcessedFiling, FilingProcessed> kafkaService) {
        this.kafkaService = kafkaService;
    }

    @PostMapping("/private/filing-processed")
    public ResponseEntity<Void> processRequest(@Valid @RequestBody ProcessedFiling processedFiling) {
        DataMapHolder.get()
                .chsUserId(processedFiling.getPresenter().getUserId())
                .transactionId(processedFiling.getTransactionId());
        LOGGER.info("Received filing processed request", DataMapHolder.getLogMap());
        kafkaService.processAndPublish(processedFiling);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
