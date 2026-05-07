package uk.gov.companieshouse.chskafka.common;

public interface Mapper<T, K> {

    K map(T request);
}
