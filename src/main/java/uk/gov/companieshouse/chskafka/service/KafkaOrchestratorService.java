package uk.gov.companieshouse.chskafka.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaOrchestratorService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaOrchestratorService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public <T, A> void processAndPublish(T request, String topic, KafkaEventStrategy<T, A> strategy,final String xRequestId) {
        strategy.validate(request,xRequestId);
        A avroMessage = strategy.map(request);
        kafkaTemplate.send(topic, avroMessage);
    }
}
