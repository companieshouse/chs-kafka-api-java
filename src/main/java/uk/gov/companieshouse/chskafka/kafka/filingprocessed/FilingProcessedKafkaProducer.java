package uk.gov.companieshouse.chskafka.kafka.filingprocessed;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.chskafka.exceptions.BadGatewayException;
import uk.gov.companieshouse.chskafka.logging.DataMapHolder;
import uk.gov.companieshouse.filing.processed.FilingProcessed;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.util.concurrent.CompletionException;

import static uk.gov.companieshouse.chskafka.Application.NAMESPACE;

@Component
public class FilingProcessedKafkaProducer implements FilingProcessedProducer{

    private static final Logger LOGGER = LoggerFactory.getLogger(NAMESPACE);
    private final KafkaTemplate<String, FilingProcessed> kafkaTemplate;
    private final String filingProcessedTopic;

    public FilingProcessedKafkaProducer(KafkaTemplate<String, FilingProcessed> kafkaTemplate,
                                        @Value("${kafka.filing.processed.topic}") String filingProcessedTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.filingProcessedTopic = filingProcessedTopic;
    }

    public void publishMessage(FilingProcessed filingProcessed) {
        try {
            kafkaTemplate.send(filingProcessedTopic, filingProcessed).join();
        } catch (CompletionException ex) {
            final String msg = "Completion error during Kafka send Future";
            LOGGER.error(msg, DataMapHolder.getLogMap());
            throw new BadGatewayException(msg, ex);
        } catch (KafkaException ex) {
            final String msg = "Error publishing to filing-processed topic";
            LOGGER.info(msg, DataMapHolder.getLogMap());
            throw new BadGatewayException(msg, ex);
        }
        LOGGER.info("Successfully published message to filing-processed topic", DataMapHolder.getLogMap());
    }
}
