package uk.gov.companieshouse.chskafka.sendemail;

import email.email_send;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.chskafka.SendEmail;
import uk.gov.companieshouse.chskafka.common.Mapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;


@Component
class SendEmailMapper implements Mapper<SendEmail, email_send> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final Supplier<LocalDateTime> localDateTimeSupplier;

    SendEmailMapper(Supplier<LocalDateTime> localDateTimeSupplier) {
        this.localDateTimeSupplier = localDateTimeSupplier;
    }

    @Override
    public email_send map(SendEmail request) {
        email_send emailSend = new email_send();
        emailSend.setAppId(request.getAppId());
        emailSend.setMessageId(request.getMessageId());
        emailSend.setMessageType(request.getMessageType());
        emailSend.setEmailAddress(request.getEmailAddress());
        emailSend.setCreatedAt(DATE_TIME_FORMATTER.format(localDateTimeSupplier.get()));
        emailSend.setData(request.getJsonData());

        return emailSend;
    }
}