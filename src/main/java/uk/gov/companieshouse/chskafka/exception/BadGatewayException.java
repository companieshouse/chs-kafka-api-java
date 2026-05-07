package uk.gov.companieshouse.chskafka.exception;

public class BadGatewayException extends RuntimeException {

    public BadGatewayException(String message, Throwable ex) {
        super(message, ex);
    }
}
