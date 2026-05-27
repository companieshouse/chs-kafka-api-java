package uk.gov.companieshouse.chskafka.sendemail;

import email.email_send;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.chskafka.SendEmail;
import uk.gov.companieshouse.chskafka.common.Controller;
import uk.gov.companieshouse.chskafka.common.Service;
import uk.gov.companieshouse.chskafka.common.logging.DataMapHolder;

import static uk.gov.companieshouse.chskafka.Application.LOGGER;

@RestController
class SendEmailController implements Controller<SendEmail> {

    private final Service<SendEmail, email_send> service;

    SendEmailController(Service<SendEmail, email_send> service) {
        this.service = service;
    }

    @Override
    @PostMapping("/send-email")
    public ResponseEntity<Void> processRequest(@Valid @RequestBody SendEmail sendEmail) {
        DataMapHolder.get()
                .internalId(sendEmail.getMessageId());
        LOGGER.info("Received SendEmail request", DataMapHolder.getLogMap());
        service.processAndPublish(sendEmail);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}