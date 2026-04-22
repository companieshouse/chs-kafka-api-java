package uk.gov.companieshouse.chskafka.exceptions;

public class MessageSendException extends RuntimeException {
    public MessageSendException(String message) {
        super(message);
    }
}
