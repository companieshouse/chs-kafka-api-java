package uk.gov.companieshouse.chskafka.messagesend;


import email.message_send;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.chskafka.MessageSend;
import uk.gov.companieshouse.chskafka.common.Mapper;
import uk.gov.companieshouse.chskafka.common.Service;
import uk.gov.companieshouse.chskafka.common.kafka.KafkaProducer;

@Component
class MessageSendService implements Service<MessageSend, message_send> {

    private final Mapper<MessageSend, message_send> messageSendMapper;
    private final KafkaProducer<message_send> kafkaProducer;

    MessageSendService(Mapper<MessageSend, message_send> messageSendMapper,
                       KafkaProducer<message_send> kafkaProducer) {
        this.messageSendMapper = messageSendMapper;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public message_send map(MessageSend request) {
        return messageSendMapper.map(request);
    }

    @Override
    public void produce(message_send kafkaModel) {
        kafkaProducer.publishMessage(kafkaModel);
    }
}
