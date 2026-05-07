package uk.gov.companieshouse.chskafka.filingprocessed;

import java.time.LocalDateTime;
import java.util.function.Supplier;
import org.springframework.stereotype.Component;

@Component
class LocalDateTimeSupplier implements Supplier<LocalDateTime> {

    @Override
    public LocalDateTime get() {
        return LocalDateTime.now();
    }
}
