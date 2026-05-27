package uk.gov.companieshouse.chskafka.sendemail;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.chskafka.SendEmail;
import uk.gov.companieshouse.chskafka.common.logging.DataMapHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SendEmailControllerTest {

    @Mock
    private SendEmailService sendEmailService;
    @InjectMocks
    private SendEmailController controller;

    @Test
    void shouldReturn201Created() {
        // given
        SendEmail sendEmail = new SendEmail();
        sendEmail.setMessageType("customer_feedback");
        sendEmail.setMessageId("12345");
        sendEmail.setAppId("98765");
        sendEmail.setJsonData("%7B%22customer_email%22%3A%22joe%40bloggs.com%22%2C%22customer_feedback%22%3A%22Great%20site%20man%22%2C%22customer_name%22%3A%22Joe%20Bloggs%22%7D");


        // when
        ResponseEntity<Void> actual = controller.processRequest(sendEmail);

        // then
        assertEquals(201, actual.getStatusCode().value());
        verify(sendEmailService).processAndPublish(sendEmail);
        assertEquals("12345", DataMapHolder.getLogMap().get("internal_id"));
    }
}
