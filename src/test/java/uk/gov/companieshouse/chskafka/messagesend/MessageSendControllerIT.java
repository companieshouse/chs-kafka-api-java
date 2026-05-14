package uk.gov.companieshouse.chskafka.messagesend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import email.message_send;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import uk.gov.companieshouse.chskafka.common.AbstractControllerIT;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class MessageSendControllerIT extends AbstractControllerIT<message_send> {
    @Qualifier("testObjectMapper")
    private final ObjectMapper objectMapper;

    @Autowired
    protected MessageSendControllerIT(ObjectMapper objectMapper) {
        super("message-send", message_send.class, message_send.getClassSchema());
        this.objectMapper = objectMapper;
    }

    @ParameterizedTest
    @CsvSource({
            "/messagesend/accepted-request.json, /messagesend/accepted-message.json",
            "/messagesend/accepted-request-no-company-number.json, /messagesend/accepted-message-no-company-number.json"

    })
    void shouldPublishMessageSendToKafkaSuccessfully(String request, String expectedMessage) throws Exception {
        // given
        String requestBody = readResource(request);

        message_send expected = readAndDeserialise(expectedMessage);

        // when
        ResultActions response = mockMvcPost(requestBody, "/message-send");
        // then
        response.andExpect(status().isCreated());
        message_send actual = consumeAndDeserialise();
        JsonNode expectedData = objectMapper.readTree(expected.getData());
        JsonNode actualData = objectMapper.readTree(actual.getData());
        assertThat(actualData).isEqualTo(expectedData);
        assertThat(actual).usingRecursiveComparison().ignoringFields("data").isEqualTo(expected);
    }

    @Test
    void shouldReturn400WhenInvalidRequestBody() throws Exception {
        // given
        String requestBody = readResource("/messagesend/request-invalid.json");

        // when
        ResultActions response = mockMvcPost(requestBody, "/message-send");

        // then
        response.andExpectAll(status().isBadRequest(),
                jsonPath("$.title").value(HttpStatus.BAD_REQUEST.getReasonPhrase()),
                jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()),
                jsonPath("$.detail").value("Failed to read request"),
                jsonPath("$.instance").value("/message-send"));
        assertZeroMessagesPublished();
    }

    @Test
    void shouldReturn401WhenNoEricIdentity() throws Exception {
        // given
        String requestBody = readResource("/messagesend/request-invalid.json");

        // when
        ResultActions response = mockMvc.perform(post("/message-send")
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
        String requestBody = readResource("/messagesend/request-invalid.json");

        // when
        ResultActions response = mockMvc.perform(post("/message-send")
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
        String requestBody = readResource("/messagesend/request-invalid.json");

        // when
        ResultActions response = mockMvc.perform(post("/message-send")
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
