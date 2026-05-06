package uk.gov.companieshouse.chskafka.exceptions;

public class BadGatewayException extends RuntimeException {

    public BadGatewayException(String message, Throwable ex) {
        super(message, ex);
    }
}
