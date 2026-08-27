package uk.gov.companieshouse.chskafka.strikeoffpartnerobjectionsprocessed;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.chskafka.ProcessedStrikeOffPartnerObjection;
import uk.gov.companieshouse.chskafka.common.Service;
import uk.gov.companieshouse.chskafka.common.exception.BadGatewayException;
import uk.gov.companieshouse.chskafka.common.kafka.KafkaProducer;
import uk.gov.companieshouse.strikeoff.partner.objections.StrikeOffPartnerObjectionsProcessed;

@Component
public class StrikeOffPartnerObjectionsProcessedService implements Service<ProcessedStrikeOffPartnerObjection, StrikeOffPartnerObjectionsProcessed> {

    private final KafkaProducer<StrikeOffPartnerObjectionsProcessed> kafkaProducer;
    private final StrikeOffPartnerObjectionsProcessedMapper mapper;

    public StrikeOffPartnerObjectionsProcessedService(
            KafkaProducer<StrikeOffPartnerObjectionsProcessed> kafkaProducer,
            StrikeOffPartnerObjectionsProcessedMapper mapper) {
        this.kafkaProducer = kafkaProducer;
        this.mapper = mapper;
    }


    @Override
    public StrikeOffPartnerObjectionsProcessed map(ProcessedStrikeOffPartnerObjection request) {
        return mapper.map(request);
    }

    @Override
    public void produce(StrikeOffPartnerObjectionsProcessed kafkaModel) {
        try {
            kafkaProducer.publishMessage(kafkaModel);
        } catch (Exception e) {
            throw new BadGatewayException("Failed to publish message to Kafka", e);
        }
    }
}