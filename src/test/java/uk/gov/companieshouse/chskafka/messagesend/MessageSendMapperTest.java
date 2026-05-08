package uk.gov.companieshouse.chskafka.messagesend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import email.message_send;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.chskafka.MessageSend;
import uk.gov.companieshouse.api.chskafka.MessageSendData;
import uk.gov.companieshouse.chskafka.common.exception.InvalidPayloadException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageSendMapperTest {

    private static final String MESSAGE_ID = "msg-001";
    private static final String USER_ID = "user-123";
    private static final String APP_ID = "app-abc";
    private static final String CREATED_AT = "2025-02-20T17:00:00Z";
    private static final String MESSAGE_TYPE = "monitor_email";
    private static final String COMPANY_NAME = "My New Company";
    private static final String COMPANY_NUMBER = "00006400";

    @Mock
    ObjectMapper objectMapper;

    @Test
    void mapShouldMapAllFieldsAndSerializeData() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        MessageSendMapper messageSendMapper = new MessageSendMapper(mapper);

        MessageSend request = createMessageSend();
        MessageSendData data = request.getData();

        // Act
        message_send result = messageSendMapper.map(request);

        // Assert
        assertEquals(MESSAGE_ID, result.getMessageId());
        assertEquals(USER_ID, result.getUserId());
        assertEquals(APP_ID, result.getAppId());
        assertEquals(CREATED_AT, result.getCreatedAt());
        assertEquals(MESSAGE_TYPE, result.getMessageType());

        // The data field should be a JSON string
        String expectedJson = mapper.writeValueAsString(data);
        assertEquals(expectedJson, result.getData());
    }

    @Test
    void map_shouldThrowInvalidPayloadExceptionOnSerializationError() throws Exception {
        MessageSendMapper mapper = new MessageSendMapper(objectMapper);

        MessageSend request = createMessageSend();

        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("fail") {
        });

        InvalidPayloadException ex = assertThrows(
                InvalidPayloadException.class,
                () -> mapper.map(request)
        );
        assertTrue(ex.getMessage().contains(MESSAGE_ID));
    }

    private MessageSend createMessageSend() {
        MessageSendData data = createMessageSendData();

        MessageSend request = new MessageSend();
        request.setMessageId(MESSAGE_ID);
        request.setUserId(USER_ID);
        request.setAppId(APP_ID);
        request.setData(data);
        request.setCreatedAt(CREATED_AT);
        request.setMessageType(MESSAGE_TYPE);
        return request;
    }

    private MessageSendData createMessageSendData() {
        MessageSendData data = new MessageSendData();
        data.setCompanyName(COMPANY_NAME);
        data.setCompanyNumber(COMPANY_NUMBER);
        return data;
    }
}
