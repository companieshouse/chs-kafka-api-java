package uk.gov.companieshouse.chskafka.common.exception;

import static uk.gov.companieshouse.chskafka.Application.LOGGER;

import java.util.Map;
import java.util.stream.Collectors;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.gov.companieshouse.chskafka.common.logging.DataMapHolder;

@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String INTERNAL_SERVER_ERROR = "Internal server error";

    // Custom bad request logging and response body
    @Override
    protected @Nullable ResponseEntity<@NonNull Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        LOGGER.error("Invalid request body", ex, DataMapHolder.getLogMap());
        ProblemDetail problemDetail = ex.getBody();
        Map<String, Object> invalidProperties = ex.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> "[%s] %s".formatted(fieldError.getRejectedValue(), fieldError.getDefaultMessage()),
                        "%s, %s"::formatted));
        problemDetail.setProperties(invalidProperties);
        return handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

    @ExceptionHandler(value = {BadGatewayException.class})
    public ProblemDetail handleBadGateway(BadGatewayException ex) {
        LOGGER.error("Error calling downstream service", ex, DataMapHolder.getLogMap());
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {InvalidPayloadException.class})
    public ProblemDetail handleInvalidPayloadException(InvalidPayloadException ex) {
        LOGGER.error("Invalid Payload exception in controller" , ex, DataMapHolder.getLogMap());
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid payload");
    }

    @ExceptionHandler(value = {Exception.class})
    public ProblemDetail handleInternalServerError(Exception ex) {
        LOGGER.error("Unhandled exception in controller" , ex, DataMapHolder.getLogMap());
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR);
    }
}
