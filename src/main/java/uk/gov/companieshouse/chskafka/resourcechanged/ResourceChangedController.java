package uk.gov.companieshouse.chskafka.resourcechanged;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.chskafka.ChangedResource;
import uk.gov.companieshouse.chskafka.common.Controller;
import uk.gov.companieshouse.chskafka.common.Service;
import uk.gov.companieshouse.chskafka.common.logging.DataMapHolder;
import uk.gov.companieshouse.stream.ResourceChanged;

import static uk.gov.companieshouse.chskafka.Application.LOGGER;

@RestController
class ResourceChangedController implements Controller<ChangedResource> {

    private final Service<ChangedResource, ResourceChanged> service;

    ResourceChangedController(Service<ChangedResource, ResourceChanged> service) {
        this.service = service;
    }

    @PostMapping("/private/resource-changed")
    @Override
    public ResponseEntity<Void> processRequest(@Valid @RequestBody ChangedResource changedResource) {
        DataMapHolder.get().contextId(changedResource.getContextId());
        LOGGER.info("Received change resource request", DataMapHolder.getLogMap());
        service.processAndPublish(changedResource);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
