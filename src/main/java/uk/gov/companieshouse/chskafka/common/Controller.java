package uk.gov.companieshouse.chskafka.common;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface Controller<R> {

    ResponseEntity<Void> processRequest(@Valid @RequestBody R requestBody);
}
