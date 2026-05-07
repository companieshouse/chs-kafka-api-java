package uk.gov.companieshouse.chskafka.service;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.chskafka.ProcessedFiling;
import uk.gov.companieshouse.chskafka.kafka.KafkaProducer;
import uk.gov.companieshouse.chskafka.mapper.RequestMapper;
import uk.gov.companieshouse.filing.processed.FilingProcessed;

@Component
public class FilingProcessedService implements KafkaService<ProcessedFiling, FilingProcessed> {

    private final RequestMapper<ProcessedFiling, FilingProcessed> filingProcessedMapper;
    private final KafkaProducer<FilingProcessed> kafkaProducer;

    public FilingProcessedService(RequestMapper<ProcessedFiling, FilingProcessed> filingProcessedMapper,
            KafkaProducer<FilingProcessed> kafkaProducer) {
        this.filingProcessedMapper = filingProcessedMapper;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public FilingProcessed map(ProcessedFiling request) {
        return filingProcessedMapper.map(request);
    }

    @Override
    public void produce(FilingProcessed kafkaModel) {
        kafkaProducer.publishMessage(kafkaModel);
    }
}
