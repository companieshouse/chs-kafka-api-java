package uk.gov.companieshouse.chskafka.strikeoffpartnerobjectionsprocessed;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.chskafka.ProcessedStrikeOffPartnerObjection;
import uk.gov.companieshouse.chskafka.common.Service;
import uk.gov.companieshouse.chskafka.common.logging.DataMapHolder;
import uk.gov.companieshouse.strikeoff.partner.objections.StrikeOffPartnerObjectionsProcessed;

import static uk.gov.companieshouse.chskafka.Application.LOGGER;

@RestController
public class StrikeOffPartnerObjectionsProcessedController {

    private final Service<ProcessedStrikeOffPartnerObjection, StrikeOffPartnerObjectionsProcessed> service;

    public StrikeOffPartnerObjectionsProcessedController(Service<ProcessedStrikeOffPartnerObjection, StrikeOffPartnerObjectionsProcessed> service) {
        this.service = service;
    }

    @PostMapping(value = "/private/strike-off-partner-objections-processed", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> postStrikeOffPartnerObjectionsProcessed(@Valid @RequestBody ProcessedStrikeOffPartnerObjection request) {
        DataMapHolder.get()
                .companyNumber(request.getCompanyNumber()).data(request.toString());
        LOGGER.info("Received Objection processed event", DataMapHolder.getLogMap());
        service.processAndPublish(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}