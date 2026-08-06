package uk.gov.companieshouse.chskafka.messagesend;

import static uk.gov.companieshouse.chskafka.Application.LOGGER;

import email.message_send;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import uk.gov.companieshouse.api.chskafka.MessageSend;
import uk.gov.companieshouse.api.chskafka.MessageSendData;
import uk.gov.companieshouse.chskafka.common.Mapper;
import uk.gov.companieshouse.chskafka.common.exception.InvalidPayloadException;
import uk.gov.companieshouse.chskafka.common.logging.DataMapHolder;

@Component
class MessageSendMapper implements Mapper<MessageSend, message_send> {

    @Qualifier("objectMapper")
    private final ObjectMapper objectMapper;

    MessageSendMapper(@Qualifier("objectMapper") ObjectMapper objectMapper) {
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
