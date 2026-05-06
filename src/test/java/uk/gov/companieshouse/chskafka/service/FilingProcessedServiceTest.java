package uk.gov.companieshouse.chskafka.service;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.chskafka.ProcessedFiling;
import uk.gov.companieshouse.chskafka.kafka.KafkaProducer;
import uk.gov.companieshouse.chskafka.mapper.FilingProcessedMapper;
import uk.gov.companieshouse.filing.processed.FilingProcessed;

@ExtendWith(MockitoExtension.class)
class FilingProcessedServiceTest {

    @Mock
    private FilingProcessedMapper filingProcessedMapper;
    @Mock
    private KafkaProducer<FilingProcessed> kafkaProducer;
    @InjectMocks
    private FilingProcessedService filingProcessedService;

    @Test
    void shouldDelegateMapping() {
        filingProcessedService.map(new ProcessedFiling());
        verify(filingProcessedMapper).map(new ProcessedFiling());
    }

    @Test
    void shouldDelegatePublishing() {
        filingProcessedService.produce(new FilingProcessed());
        verify(kafkaProducer).publishMessage(new FilingProcessed());
    }
}