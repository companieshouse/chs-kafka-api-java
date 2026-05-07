package uk.gov.companieshouse.chskafka.common;

public interface Service<R, K> {

    K map(R request);

    void produce(K kafkaModel);

    default void processAndPublish(R request) {
        K kafkaModel = map(request);
        produce(kafkaModel);
    }
}

