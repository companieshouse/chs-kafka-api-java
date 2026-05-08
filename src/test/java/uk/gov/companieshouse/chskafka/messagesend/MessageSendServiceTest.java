package uk.gov.companieshouse.chskafka.messagesend;

import email.message_send;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.chskafka.MessageSend;
import uk.gov.companieshouse.chskafka.common.Mapper;
import uk.gov.companieshouse.chskafka.common.kafka.KafkaProducer;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MessageSendServiceTest {

    @Mock
    private Mapper<MessageSend, message_send> messageSendMapper;
    @Mock
    private KafkaProducer<message_send> kafkaProducer;
    @InjectMocks
    private MessageSendService messageSendService;

    @Test
    void shouldDelegateMapping() {
        MessageSend request = new MessageSend();
        messageSendService.map(request);
        verify(messageSendMapper).map(request);
    }

    @Test
    void shouldDelegatePublishing() {
        message_send kafkaModel = new message_send();
        messageSendService.produce(kafkaModel);
        verify(kafkaProducer).publishMessage(kafkaModel);
    }
}
