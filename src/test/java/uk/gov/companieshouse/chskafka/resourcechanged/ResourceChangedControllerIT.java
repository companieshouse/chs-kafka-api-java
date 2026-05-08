package uk.gov.companieshouse.chskafka.resourcechanged;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import uk.gov.companieshouse.chskafka.common.AbstractControllerIT;
import uk.gov.companieshouse.stream.ResourceChanged;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ResourceChangedControllerIT extends AbstractControllerIT<ResourceChanged> {

    protected ResourceChangedControllerIT() {
        super("resource-changed", ResourceChanged.class, ResourceChanged.getClassSchema());
    }

    @ParameterizedTest
    @CsvSource({
            "/resourceChanged/minimal-valid-request.json, /resourceChanged/minimal-valid-message.json",
            "/resourceChanged/full-valid-request.json, /resourceChanged/full-valid-message.json",
            "/resourceChanged/deleted-event-request.json, /resourceChanged/deleted-event-message.json",
            "/resourceChanged/accepted-request.json, /resourceChanged/accepted-message.json",
            "/resourceChanged/unknown-fields-request.json, /resourceChanged/unknown-fields-message.json"
    })
    void shouldPublishResourceChangedToKafkaSuccessfully(String request, String expectedMessage) throws Exception {
        // given
        String requestBody = readResource(request);
        ResourceChanged expected = readAndDeserialise(expectedMessage);

        // when
        ResultActions response = mockMvcPost(requestBody, "/private/resource-changed");

        // then
        response.andExpect(status().isCreated());
        ResourceChanged actual = consumeAndDeserialise();
        assertEquals(expected, actual);
    }

    @Test
    void shouldReturn400WhenInvalidRequestBody() throws Exception {
        // given
        String requestBody = readResource("/resourceChanged/request-invalid.json");

        // when
        ResultActions response = mockMvcPost(requestBody, "/private/resource-changed");

        // then
        response.andExpectAll(status().isBadRequest(),
                jsonPath("$.title").value(HttpStatus.BAD_REQUEST.getReasonPhrase()),
                jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()),
                jsonPath("$.detail").value("Failed to read request"),
                jsonPath("$.instance").value("/private/resource-changed"));
        assertZeroMessagesPublished();
    }

    @Test
    void shouldReturn401WhenNoEricIdentity() throws Exception {
        // given
        String requestBody = readResource("/resourceChanged/request-invalid.json");

        // when
        ResultActions response = mockMvc.perform(post("/private/resource-changed")
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
        String requestBody = readResource("/resourceChanged/request-invalid.json");

        // when
        ResultActions response = mockMvc.perform(post("/private/resource-changed")
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
        String requestBody = readResource("/resourceChanged/request-invalid.json");

        // when
        ResultActions response = mockMvc.perform(post("/private/resource-changed")
                .header("ERIC-Identity", "123")
                .header("ERIC-Identity-Type", "key")
                .header("X-Request-Id", "test-request-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        response.andExpect(status().isForbidden());
        assertZeroMessagesPublished();
    }

    @ParameterizedTest
    @CsvSource({
            "/resourceChanged/missing-event.json",
            "/resourceChanged/invalid-type.json",
            "/resourceChanged/null-fields.json"
    })
    void shouldReturn400ForAdditionalInvalidPayloads(String requestFile) throws Exception {
        // given
        String requestBody = readResource(requestFile);

        // when
        ResultActions response = mockMvcPost(requestBody, "/private/resource-changed");

        // then
        response.andExpectAll(status().isBadRequest(),
                jsonPath("$.title").value(HttpStatus.BAD_REQUEST.getReasonPhrase()),
                jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()),
                jsonPath("$.instance").value("/private/resource-changed"));
        assertZeroMessagesPublished();
    }
}
