package uk.gov.companieshouse.chskafka.sendemail;

import email.email_send;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.chskafka.SendEmail;
import uk.gov.companieshouse.chskafka.common.kafka.KafkaProducer;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SendEmailServiceTest {

    @Mock
    private SendEmailMapper sendEmailMapper;
    @Mock
    private KafkaProducer<email_send> kafkaProducer;
    @InjectMocks
    private SendEmailService sendEmailService;

    @Test
    void shouldDelegateMapping() {
        sendEmailService.map(new SendEmail());
        verify(sendEmailMapper).map(new SendEmail());
    }

    @Test
    void shouldDelegatePublishing() {
        sendEmailService.produce(new email_send());
        verify(kafkaProducer).publishMessage(new email_send());
    }
}