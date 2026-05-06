package uk.gov.companieshouse.chskafka.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import uk.gov.companieshouse.filing.processed.FilingProcessed;

@SpringBootTest
class FilingProcessedControllerIT extends AbstractControllerIT {

    private static final String FILING_PROCESSED_TOPIC = "filing-processed";

    protected FilingProcessedControllerIT() {
        super(FILING_PROCESSED_TOPIC);
    }

    @Test
    void shouldPublishFilingProcessedAcceptedToKafkaSuccessfully() throws Exception {
        // given
        String requestBody = readResource("/filing-processed-accepted-request.json");

        FilingProcessed expected = readAndDeserialise("/filing-processed-accepted-message.json", FilingProcessed.class,
                FilingProcessed.getClassSchema());

        // when
        ResultActions result = mockMvcPost(requestBody, "/private/filing-processed");

        // then
        result.andExpect(status().isCreated());
        FilingProcessed actual = consumeAndDeserialise(FILING_PROCESSED_TOPIC, FilingProcessed.class);
        assertEquals(expected, actual);
    }

    @Test
    void shouldPublishFilingProcessedRejectedToKafkaSuccessfully() throws Exception {
        // given
        String requestBody = readResource("/filing-processed-rejected-request.json");

        FilingProcessed expected = readAndDeserialise("/filing-processed-rejected-message.json", FilingProcessed.class,
                FilingProcessed.getClassSchema());

        // when
        ResultActions result = mockMvcPost(requestBody, "/private/filing-processed");

        // then
        result.andExpect(status().isCreated());
        FilingProcessed actual = consumeAndDeserialise(FILING_PROCESSED_TOPIC, FilingProcessed.class);
        assertEquals(expected, actual);
    }

    @Test
    void shouldReturn400WhenInvalidRequestBody() throws Exception {
        // given
        String requestBody = readResource("/filing-processed-request-invalid.json");

        // when
        ResultActions result = mockMvcPost(requestBody, "/private/filing-processed");

        // then
        result.andExpect(status().isBadRequest());
        assertZeroMessagesPublished(FILING_PROCESSED_TOPIC);
    }

    @Test
    void shouldReturn401WhenNoEricIdentity() throws Exception {
        // given
        String requestBody = readResource("/filing-processed-request-invalid.json");

        // when
        ResultActions result = mockMvc.perform(post("/private/filing-processed")
                .header("X-Request-Id", "test-request-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isUnauthorized());
        assertZeroMessagesPublished(FILING_PROCESSED_TOPIC);
    }

    @Test
    void shouldReturn401WhenNoEricIdentityType() throws Exception {
        // given
        String requestBody = readResource("/filing-processed-request-invalid.json");

        // when
        ResultActions result = mockMvc.perform(post("/private/filing-processed")
                .header("ERIC-Identity", "123")
                .header("X-Request-Id", "test-request-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isUnauthorized());
        assertZeroMessagesPublished(FILING_PROCESSED_TOPIC);
    }

    @Test
    void shouldReturn403WhenNoInternalPrivileges() throws Exception {
        // given
        String requestBody = readResource("/filing-processed-request-invalid.json");

        // when
        ResultActions result = mockMvc.perform(post("/private/filing-processed")
                .header("ERIC-Identity", "123")
                .header("ERIC-Identity-Type", "key")
                .header("X-Request-Id", "test-request-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isForbidden());
        assertZeroMessagesPublished(FILING_PROCESSED_TOPIC);
    }
}
