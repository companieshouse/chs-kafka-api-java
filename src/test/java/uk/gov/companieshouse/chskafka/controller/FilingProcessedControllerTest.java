package uk.gov.companieshouse.chskafka.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.chskafka.ProcessedFiling;
import uk.gov.companieshouse.api.chskafka.ProcessedFilingPresenter;
import uk.gov.companieshouse.chskafka.logging.DataMapHolder;
import uk.gov.companieshouse.chskafka.service.FilingProcessedService;

@ExtendWith(MockitoExtension.class)
class FilingProcessedControllerTest {

    @Mock
    private FilingProcessedService filingProcessedService;
    @InjectMocks
    private FilingProcessedController controller;

    @Test
    void shouldReturn201Created() {
        // given
        ProcessedFilingPresenter presenter = new ProcessedFilingPresenter();
        presenter.setUserId("userId");
        ProcessedFiling processedFiling = new ProcessedFiling();
        processedFiling.setPresenter(presenter);
        processedFiling.setTransactionId("transactionId");

        // when
        ResponseEntity<Void> actual = controller.processRequest(processedFiling);

        // then
        assertEquals(201, actual.getStatusCode().value());
        verify(filingProcessedService).processAndPublish(processedFiling);
        assertEquals("userId", DataMapHolder.getLogMap().get("chs_user_id"));
        assertEquals("transactionId", DataMapHolder.getLogMap().get("transaction_id"));
    }
}
