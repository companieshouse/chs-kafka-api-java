package uk.gov.companieshouse.chskafka.sendemail;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class LocalDateTimeSupplierTest {

    private final Supplier<LocalDateTime> localDateTimeSupplier = new LocalDateTimeSupplier();

    @Test
    void shouldSupplyLocalDateTime() {
        assertInstanceOf(LocalDateTime.class, localDateTimeSupplier.get());
    }
}