package uk.gov.companieshouse.chskafka.sendemail;

import email.email_send;
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
import uk.gov.companieshouse.chskafka.common.LocalDateTimeSupplier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class SendEmailControllerIT extends AbstractControllerIT<email_send> {

    protected SendEmailControllerIT() {
        super("email-send", email_send.class, email_send.getClassSchema());
    }

    @MockitoBean
    private LocalDateTimeSupplier localDateTimeSupplier;

    @BeforeEach
    void setUp() {
        when(localDateTimeSupplier.get()).thenReturn(LocalDateTime.parse("2026-04-16T11:20:26"));
    }

    @ParameterizedTest
    @CsvSource({
            "/sendemail/accepted-request.json, /sendemail/accepted-message.json"
    })
    void shouldPublishEmailSendToKafkaSuccessfully(String request, String expectedMessage) throws Exception {
        // given
        String requestBody = readResource(request);

        email_send expected = readAndDeserialise(expectedMessage);

        // when
        ResultActions response = mockMvcPost(requestBody, "/send-email");
        // then
        response.andExpect(status().isCreated());
        email_send actual = consumeAndDeserialise();
        assertEquals(expected, actual);
    }

    @Test
    void shouldReturn400WhenInvalidRequestBody() throws Exception {
        // given
        String requestBody = readResource("/sendemail/request-invalid.json");

        // when
        ResultActions response = mockMvcPost(requestBody, "/send-email");

        // then
        response.andExpectAll(status().isBadRequest(),
                jsonPath("$.title").value(HttpStatus.BAD_REQUEST.getReasonPhrase()),
                jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()),
                jsonPath("$.detail").value("Invalid request content."),
                jsonPath("$.instance").value("/send-email"));
        assertZeroMessagesPublished();
    }

    @Test
    void shouldReturn401WhenNoEricIdentity() throws Exception {
        // given
        String requestBody = readResource("/sendemail/request-invalid.json");

        // when
        ResultActions response = mockMvc.perform(post("/send-email")
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
        String requestBody = readResource("/sendemail/request-invalid.json");

        // when
        ResultActions response = mockMvc.perform(post("/send-email")
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
        String requestBody = readResource("/sendemail/request-invalid.json");

        // when
        ResultActions response = mockMvc.perform(post("/send-email")
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
