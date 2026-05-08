package uk.gov.companieshouse.chskafka.messagesend;

import email.message_send;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.chskafka.MessageSend;
import uk.gov.companieshouse.api.chskafka.MessageSendData;
import uk.gov.companieshouse.chskafka.common.Service;
import uk.gov.companieshouse.chskafka.common.logging.DataMapHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MessageSendControllerTest {

    private static final String TRANSACTION_ID = "txn-123";
    private static final String USER_ID = "user-123";

    @Mock
    private Service<MessageSend, message_send> messageSendService;
    @InjectMocks
    private MessageSendController controller;

    @Test
    void shouldReturn201Created() {
        // given
        MessageSendData data = new MessageSendData();
        data.setTransactionId(TRANSACTION_ID);
        MessageSend messageSend = new MessageSend();
        messageSend.setUserId(USER_ID);
        messageSend.setData(data);

        // when
        ResponseEntity<Void> actual = controller.processRequest(messageSend);

        // then
        assertEquals(201, actual.getStatusCode().value());
        verify(messageSendService).processAndPublish(messageSend);
        assertEquals(USER_ID, DataMapHolder.getLogMap().get("chs_user_id"));
        assertEquals(TRANSACTION_ID, DataMapHolder.getLogMap().get("transaction_id"));
    }
}
