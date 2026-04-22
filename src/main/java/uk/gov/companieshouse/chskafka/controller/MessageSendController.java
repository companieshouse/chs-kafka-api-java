package uk.gov.companieshouse.chskafka.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.chskafka.ProcessedFiling;
import uk.gov.companieshouse.chskafka.service.KafkaOrchestratorService;
import uk.gov.companieshouse.chskafka.service.strategy.FilingProcessStrategy;
import uk.gov.companieshouse.chskafka.util.Constant;

import static uk.gov.companieshouse.chskafka.util.Constant.REQUEST_ID_HEADER_NAME;

@RestController
public class MessageSendController {

    private final KafkaOrchestratorService kafkaOrchestratorService;
    private final FilingProcessStrategy filingProcessStrategy;

    public MessageSendController(KafkaOrchestratorService kafkaOrchestratorService, FilingProcessStrategy filingProcessStrategy) {
        this.kafkaOrchestratorService = kafkaOrchestratorService;
        this.filingProcessStrategy = filingProcessStrategy;
    }

    @PostMapping("/private/filing-processed")
    public ResponseEntity<Void> filingProcessedMessage(@RequestBody ProcessedFiling processedFiling,final @RequestHeader(REQUEST_ID_HEADER_NAME) String xRequestId) {
        kafkaOrchestratorService.processAndPublish(processedFiling, Constant.ITEM_GROUP_PROCESSED_SEND,filingProcessStrategy,xRequestId);
        return ResponseEntity.ok().build();
    }

}
