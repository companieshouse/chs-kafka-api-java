package uk.gov.companieshouse.chskafka.common;

import org.testcontainers.kafka.ConfluentKafkaContainer;

public final class SharedKafkaContainer {

    private static final ConfluentKafkaContainer KAFKA = new ConfluentKafkaContainer("confluentinc/cp-kafka:7.6.1");

    static {
        KAFKA.start();
    }

    private SharedKafkaContainer() {
    }

    public static ConfluentKafkaContainer getInstance() {
        return KAFKA;
    }
}

