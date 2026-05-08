package uk.gov.companieshouse.chskafka.messagesend;

import email.message_send;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.chskafka.MessageSend;
import uk.gov.companieshouse.chskafka.common.Controller;
import uk.gov.companieshouse.chskafka.common.Service;
import uk.gov.companieshouse.chskafka.common.logging.DataMapHolder;

import static uk.gov.companieshouse.chskafka.Application.LOGGER;

@RestController
class MessageSendController implements Controller<MessageSend> {

    private final Service<MessageSend, message_send> service;

    MessageSendController(Service<MessageSend, message_send> service) {
        this.service = service;
    }

    @Override
    @PostMapping("/message-send")
    public ResponseEntity<Void> processRequest(@Valid @RequestBody MessageSend messageSend) {
        DataMapHolder.get()
                .chsUserId(messageSend.getUserId())
                .transactionId(messageSend.getData().getTransactionId());
        LOGGER.info("Received MessageSend request", DataMapHolder.getLogMap());
        service.processAndPublish(messageSend);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
