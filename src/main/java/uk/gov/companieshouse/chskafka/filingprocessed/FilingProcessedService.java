package uk.gov.companieshouse.chskafka.filingprocessed;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.chskafka.ProcessedFiling;
import uk.gov.companieshouse.chskafka.common.Mapper;
import uk.gov.companieshouse.chskafka.common.Service;
import uk.gov.companieshouse.chskafka.common.kafka.KafkaProducer;
import uk.gov.companieshouse.filing.processed.FilingProcessed;

@Component
class FilingProcessedService implements Service<ProcessedFiling, FilingProcessed> {

    private final Mapper<ProcessedFiling, FilingProcessed> filingProcessedMapper;
    private final KafkaProducer<FilingProcessed> kafkaProducer;

    FilingProcessedService(Mapper<ProcessedFiling, FilingProcessed> filingProcessedMapper,
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
