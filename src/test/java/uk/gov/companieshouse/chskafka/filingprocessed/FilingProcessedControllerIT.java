package uk.gov.companieshouse.chskafka.filingprocessed;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;
import uk.gov.companieshouse.chskafka.common.AbstractControllerIT;
import uk.gov.companieshouse.chskafka.common.exception.BadGatewayException;
import uk.gov.companieshouse.filing.processed.FilingProcessed;

@SpringBootTest
class FilingProcessedControllerIT extends AbstractControllerIT<FilingProcessed> {

    protected FilingProcessedControllerIT() {
        super("filing-processed", FilingProcessed.class, FilingProcessed.getClassSchema());
    }

    @MockitoBean
    private LocalDateTimeSupplier localDateTimeSupplier;

    @BeforeEach
    void setUp() {
        when(localDateTimeSupplier.get()).thenReturn(LocalDateTime.parse("2026-04-16T11:20:26"));
    }

    @ParameterizedTest
    @CsvSource({
            "/filingprocessed/accepted-request.json, /filingprocessed/accepted-message.json",
            "/filingprocessed/accepted-min-fields-request.json, /filingprocessed/accepted-min-fields-message.json",
            "/filingprocessed/rejected-request.json, /filingprocessed/rejected-message.json"
    })
    void shouldPublishFilingProcessedToKafkaSuccessfully(String request, String expectedMessage) throws Exception {
        // given
        String requestBody = readResource(request);

        FilingProcessed expected = readAndDeserialise(expectedMessage);

        // when
        ResultActions response = mockMvcPost(requestBody, "/private/filing-processed");

        // then
        response.andExpect(status().isCreated());
        FilingProcessed actual = consumeAndDeserialise();
        assertEquals(expected, actual);
    }

    @Test
    void shouldReturn400WhenInvalidRequestBody() throws Exception {
        // given
        String requestBody = readResource("/filingprocessed/request-invalid.json");

        // when
        ResultActions response = mockMvcPost(requestBody, "/private/filing-processed");

        // then
        response.andExpectAll(status().isBadRequest(),
                jsonPath("$.title").value(HttpStatus.BAD_REQUEST.getReasonPhrase()),
                jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()),
                jsonPath("$.detail").value("Invalid request content."),
                jsonPath("$.instance").value("/private/filing-processed"),
                jsonPath("$.transactionId").value("[null] must not be null"));
        assertZeroMessagesPublished();
    }

    @Test
    void shouldReturn401WhenNoEricIdentity() throws Exception {
        // given
        String requestBody = readResource("/filingprocessed/request-invalid.json");

        // when
        ResultActions response = mockMvc.perform(post("/private/filing-processed")
                .header("X-Request-Id", "test-request-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        response.andExpect(status().isUnauthorized());
        assertZeroMessagesPublished();
    }

    @Test
    void shouldReturn401WhenNoEricIdentityType() throws Exception {
        // given
        String requestBody = readResource("/filingprocessed/request-invalid.json");

        // when
        ResultActions response = mockMvc.perform(post("/private/filing-processed")
                .header("ERIC-Identity", "123")
                .header("X-Request-Id", "test-request-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        response.andExpect(status().isUnauthorized());
        assertZeroMessagesPublished();
    }

    @Test
    void shouldReturn403WhenNoInternalPrivileges() throws Exception {
        // given
        String requestBody = readResource("/filingprocessed/request-invalid.json");

        // when
        ResultActions response = mockMvc.perform(post("/private/filing-processed")
                .header("ERIC-Identity", "123")
                .header("ERIC-Identity-Type", "key")
                .header("X-Request-Id", "test-request-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        response.andExpect(status().isForbidden());
        assertZeroMessagesPublished();
    }

    @Test
    void shouldReturn500WhenUnknownExceptionThrown() throws Exception {
        // given
        String requestBody = readResource("/filingprocessed/accepted-request.json");

        when(localDateTimeSupplier.get()).thenThrow(NullPointerException.class);

        // when
        ResultActions response = mockMvcPost(requestBody, "/private/filing-processed");

        // then
        response.andExpectAll(status().isInternalServerError(),
                jsonPath("$.title").value(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()),
                jsonPath("$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                jsonPath("$.instance").value("/private/filing-processed"));
        assertZeroMessagesPublished();
    }

    @Test
    void shouldReturn502WhenBadGatewayExceptionThrown() throws Exception {
        // given
        String requestBody = readResource("/filingprocessed/accepted-request.json");

        when(localDateTimeSupplier.get()).thenThrow(BadGatewayException.class);

        // when
        ResultActions response = mockMvcPost(requestBody, "/private/filing-processed");

        // then
        response.andExpectAll(status().isBadGateway(),
                jsonPath("$.title").value(HttpStatus.BAD_GATEWAY.getReasonPhrase()),
                jsonPath("$.status").value(HttpStatus.BAD_GATEWAY.value()),
                jsonPath("$.instance").value("/private/filing-processed"));
        assertZeroMessagesPublished();
    }
}
