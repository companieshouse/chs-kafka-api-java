package uk.gov.companieshouse.chskafka.exceptions;

import static uk.gov.companieshouse.chskafka.Application.LOGGER;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import uk.gov.companieshouse.chskafka.logging.DataMapHolder;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(value = {MethodArgumentNotValidException.class,})
    public ResponseEntity<Void> handleBadRequestExceptions(Exception ex) {
        LOGGER.error("Invalid request body", ex, DataMapHolder.getLogMap());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Void> handleInternalServerError(Exception ex) {
        LOGGER.error(ex.getClass().getName(), ex, DataMapHolder.getLogMap());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
    }

    @ExceptionHandler(value = {BadGatewayException.class})
    public ResponseEntity<Void> handleBadGateway(Exception ex) {
        LOGGER.error("Error calling downstream service", ex, DataMapHolder.getLogMap());
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .build();
    }

    @ExceptionHandler(value = {NoResourceFoundException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Void> handNoResourceFound(Exception ex) {
        LOGGER.error("Endpoint not found", ex, DataMapHolder.getLogMap());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .build();
    }
}
