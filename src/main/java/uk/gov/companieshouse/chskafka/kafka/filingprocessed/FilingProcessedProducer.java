package uk.gov.companieshouse.chskafka.kafka.filingprocessed;

import uk.gov.companieshouse.filing.processed.FilingProcessed;

public interface FilingProcessedProducer {

    void publishMessage(FilingProcessed filingProcessed);
}
