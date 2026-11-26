package uk.gov.companieshouse.chskafka.common.exception;

public class SerDesException extends RuntimeException {

    public SerDesException(String message, Throwable cause) {
        super(message, cause);
    }
}