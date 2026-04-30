package uk.gov.companieshouse.chskafka.service;

@FunctionalInterface
public interface KafkaEventStrategy<T, A>  {
    A map(T request);

    default void validate(T request, final String xRequestId) {
    }

    default void send(A avroRequest) {
    }
}
