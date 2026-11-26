package uk.gov.companieshouse.chskafka.common.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

class ControllerExceptionHandlerTest {

    private final ControllerExceptionHandler handler = new ControllerExceptionHandler();

    @Test
    void shouldReturnBadGatewayForBadGatewayException() {
        ProblemDetail response = handler.handleBadGateway(new BadGatewayException("downstream failed", new RuntimeException()));

        assertEquals(HttpStatus.BAD_GATEWAY.value(), response.getStatus());
        assertEquals(ControllerExceptionHandler.INTERNAL_SERVER_ERROR, response.getDetail());
    }

    @Test
    void shouldReturnInvalidPayloadDetailForSerDesException() {
        ProblemDetail response = handler.handleSerDesException(
                new SerDesException(ControllerExceptionHandler.INTERNAL_SERVER_ERROR, new RuntimeException()));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
        assertEquals(ControllerExceptionHandler.INTERNAL_SERVER_ERROR, response.getDetail());
    }

    @Test
    void shouldReturnGenericInternalServerErrorForUnexpectedException() {
        ProblemDetail response = handler.handleInternalServerError(new RuntimeException("unexpected"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
        assertEquals(ControllerExceptionHandler.INTERNAL_SERVER_ERROR, response.getDetail());
    }
}
