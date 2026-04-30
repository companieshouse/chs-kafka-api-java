package uk.gov.companieshouse.chskafka.service;

import org.springframework.stereotype.Service;

@Service
public class KafkaOrchestratorService {

    public <T, A> void processAndPublish(T request, KafkaEventStrategy<T, A> strategy, final String xRequestId) {
        strategy.validate(request, xRequestId);

        A avroMessage = strategy.map(request);

        strategy.send(avroMessage);
    }
}
