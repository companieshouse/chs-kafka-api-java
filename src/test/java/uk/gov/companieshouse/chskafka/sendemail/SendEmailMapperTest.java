package uk.gov.companieshouse.chskafka.sendemail;

import email.email_send;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.chskafka.SendEmail;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SendEmailMapperTest {

    private static final String APPLICATION_ID = "123";
    private static final String MESSAGE_ID = "987";
    private static final String MESSAGE_TYPE = "customer_feedback";
    private static final String EMAIL_ADDRESS = "joe@bloggs.com";
    private static final String JSON_DATA = "%7B%22customer_email%22%3A%22joe%40bloggs.com%22%2C%22customer_feedback%22%3A%22Great%20site%20man%22%2C%22customer_name%22%3A%22Joe%20Bloggs%22%7D";
    private static final String LOCAL_DATE_TIME = "2026-04-16T11:19:56";

    @Mock
    private LocalDateTimeSupplier localDateTimeSupplier;
    @InjectMocks
    private SendEmailMapper sendEmailMapper;

    @Test
    void testSuccessfullyMapSendEmail() {
        // given
        SendEmail sendEmail = getSendEmail();

        when(localDateTimeSupplier.get()).thenReturn(LocalDateTime.parse(LOCAL_DATE_TIME));

        // when
        email_send result = sendEmailMapper.map(sendEmail);

        // then
        assertEquals(getEmailSend(), result);
        verify(localDateTimeSupplier).get();
    }

    @NotNull
    private static email_send getEmailSend() {
        email_send emailSend = new email_send();
        emailSend.setAppId(APPLICATION_ID);
        emailSend.setMessageId(MESSAGE_ID);
        emailSend.setMessageType(MESSAGE_TYPE);
        emailSend.setData(JSON_DATA);
        emailSend.setEmailAddress(EMAIL_ADDRESS);
        emailSend.setCreatedAt(LOCAL_DATE_TIME);

        return emailSend;
    }

    private static SendEmail getSendEmail() {
        SendEmail sendEmail = new SendEmail();
        sendEmail.setAppId(APPLICATION_ID);
        sendEmail.setMessageId(MESSAGE_ID);
        sendEmail.setMessageType(MESSAGE_TYPE);
        sendEmail.setJsonData(JSON_DATA);
        sendEmail.setEmailAddress(EMAIL_ADDRESS);

        return sendEmail;
    }
}
