package uk.gov.companieshouse.chskafka.mapper;

public interface RequestMapper<T, K> {

    K map(T request);
}
