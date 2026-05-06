package uk.gov.companieshouse.chskafka.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;
import uk.gov.companieshouse.chskafka.mapper.LocalDateTimeSupplier;
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

    @Test
    void shouldPublishFilingProcessedAcceptedToKafkaSuccessfully() throws Exception {
        // given
        String requestBody = readResource("/filing-processed-accepted-request.json");

        FilingProcessed expected = readAndDeserialise("/filing-processed-accepted-message.json");

        // when
        ResultActions response = mockMvcPost(requestBody, "/private/filing-processed");

        // then
        response.andExpect(status().isCreated());
        FilingProcessed actual = consumeAndDeserialise();
        assertEquals(expected, actual);
    }

    @Test
    void shouldPublishFilingProcessedRejectedToKafkaSuccessfully() throws Exception {
        // given
        String requestBody = readResource("/filing-processed-rejected-request.json");

        FilingProcessed expected = readAndDeserialise("/filing-processed-rejected-message.json");

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
        String requestBody = readResource("/filing-processed-request-invalid.json");

        // when
        ResultActions response = mockMvcPost(requestBody, "/private/filing-processed");

        // then
        response.andExpect(status().isBadRequest());
        assertZeroMessagesPublished();
    }

    @Test
    void shouldReturn401WhenNoEricIdentity() throws Exception {
        // given
        String requestBody = readResource("/filing-processed-request-invalid.json");

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
        String requestBody = readResource("/filing-processed-request-invalid.json");

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
        String requestBody = readResource("/filing-processed-request-invalid.json");

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
}
