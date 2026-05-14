package uk.gov.companieshouse.chskafka.sendemail;


import email.email_send;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.chskafka.SendEmail;
import uk.gov.companieshouse.chskafka.common.Mapper;
import uk.gov.companieshouse.chskafka.common.Service;
import uk.gov.companieshouse.chskafka.common.kafka.KafkaProducer;

@Component
class SendEmailService implements Service<SendEmail, email_send> {

    private final Mapper<SendEmail, email_send> sendEmailMapper;
    private final KafkaProducer<email_send> kafkaProducer;

    SendEmailService(Mapper<SendEmail, email_send> sendEmailMapper,
                       KafkaProducer<email_send> kafkaProducer) {
        this.sendEmailMapper = sendEmailMapper;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public email_send map(SendEmail request) {
        return sendEmailMapper.map(request);
    }

    @Override
    public void produce(email_send kafkaModel) {
        kafkaProducer.publishMessage(kafkaModel);
    }
}