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

        // when
        ResponseEntity<Void> actual = controller.processRequest(new ProcessedFiling());

        // then
        assertEquals(201, actual.getStatusCode().value());
        verify(filingProcessedService).processAndPublish(new ProcessedFiling());
    }
}
