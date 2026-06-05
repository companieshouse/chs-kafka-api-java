package uk.gov.companieshouse.chskafka.messagesend;

import com.fasterxml.jackson.databind.ObjectMapper;
import email.message_send;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.chskafka.MessageSend;
import uk.gov.companieshouse.api.chskafka.MessageSendData;
import uk.gov.companieshouse.chskafka.common.Mapper;
import uk.gov.companieshouse.chskafka.common.exception.InvalidPayloadException;
import uk.gov.companieshouse.chskafka.common.logging.DataMapHolder;

import static uk.gov.companieshouse.chskafka.Application.LOGGER;

@Component
class MessageSendMapper implements Mapper<MessageSend, message_send> {

    private final ObjectMapper objectMapper;

    MessageSendMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public message_send map(MessageSend request) {
        message_send messageSend = new message_send();
        messageSend.setMessageId(request.getMessageId());
        messageSend.setUserId(request.getUserId());
        messageSend.setAppId(request.getAppId());
        MessageSendData messageSendData = request.getData();
        try {
            messageSend.setData(objectMapper.writeValueAsString(messageSendData));
        } catch (Exception ex) {
            final String msg = "Error serialising messageSendData payload for messageId %s".formatted(request.getMessageId());
            LOGGER.error(msg, ex, DataMapHolder.getLogMap());
            throw new InvalidPayloadException(msg, ex);
        }
        messageSend.setCreatedAt(request.getCreatedAt());
        messageSend.setMessageType(request.getMessageType());
        return messageSend;
    }
}
