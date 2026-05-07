package uk.gov.companieshouse.chskafka.filingprocessed;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.time.LocalDateTime;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

class LocalDateTimeSupplierTest {

    private final Supplier<LocalDateTime> localDateTimeSupplier = new LocalDateTimeSupplier();

    @Test
    void shouldSupplyLocalDateTime() {
        assertInstanceOf(LocalDateTime.class, localDateTimeSupplier.get());
    }
}