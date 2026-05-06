package uk.gov.companieshouse.chskafka.service;

public interface KafkaService<R, K> {

    K map(R request);

    void produce(K kafkaModel);

    default void processAndPublish(R request) {
        K kafkaModel = map(request);
        produce(kafkaModel);
    }
}

