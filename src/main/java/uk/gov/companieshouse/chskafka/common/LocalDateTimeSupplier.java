package uk.gov.companieshouse.chskafka.common;

import java.time.LocalDateTime;
import java.util.function.Supplier;
import org.springframework.stereotype.Component;

@Component
public class LocalDateTimeSupplier implements Supplier<LocalDateTime> {

    @Override
    public LocalDateTime get() {
        return LocalDateTime.now();
    }
}
