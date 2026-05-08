package uk.gov.companieshouse.chskafka.messagesend;

import com.fasterxml.jackson.databind.ObjectMapper;
import email.message_send;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.chskafka.MessageSend;
import uk.gov.companieshouse.chskafka.common.Mapper;
import uk.gov.companieshouse.chskafka.common.exception.InvalidPayloadException;

@Component
class MessageSendMapper implements Mapper<MessageSend, message_send> {

    private final ObjectMapper objectMapper;

    MessageSendMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public message_send map(MessageSend request) {
        message_send message_send = new message_send();
        message_send.setMessageId(request.getMessageId());
        message_send.setUserId(request.getUserId());
        message_send.setAppId(request.getAppId());
        try {
            message_send.setData(objectMapper.writeValueAsString(request.getData()));
        } catch (Exception e) {
            throw new InvalidPayloadException("Failed to serialize data for messageId %s".formatted(request.getMessageId()), e);
        }
        message_send.setCreatedAt(request.getCreatedAt());
        message_send.setMessageType(request.getMessageType());
        return message_send;
    }
}
