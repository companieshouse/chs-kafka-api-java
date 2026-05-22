package uk.gov.companieshouse.chskafka.resourcechanged;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.chskafka.ChangedResource;
import uk.gov.companieshouse.chskafka.common.Mapper;
import uk.gov.companieshouse.chskafka.common.exception.InvalidPayloadException;
import uk.gov.companieshouse.chskafka.common.logging.DataMapHolder;
import uk.gov.companieshouse.stream.EventRecord;
import uk.gov.companieshouse.stream.ResourceChanged;

import static uk.gov.companieshouse.chskafka.Application.LOGGER;

@Component
class ResourceChangedMapper implements Mapper<ChangedResource, ResourceChanged> {

    private static final String EMPTY_STRING = "";

    private final ObjectMapper mapper;

    public ResourceChangedMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public ResourceChanged map(ChangedResource request) {
        ResourceChanged resourceChanged = new ResourceChanged();

        EventRecord eventRecord = new EventRecord();
        eventRecord.setFieldsChanged(request.getEvent().getFieldsChanged());
        eventRecord.setPublishedAt(request.getEvent().getPublishedAt());
        eventRecord.setType(request.getEvent().getType());
        resourceChanged.setEvent(eventRecord);

        resourceChanged.setContextId(request.getContextId());
        resourceChanged.setResourceKind(request.getResourceKind());
        resourceChanged.setResourceUri(request.getResourceUri());

        if ("deleted".equals(request.getEvent().getType()) && request.getDeletedData() != null) {
            try {
                String jsonString = mapper.writeValueAsString(request.getDeletedData());
                resourceChanged.setDeletedData(jsonString);
            } catch ( JsonProcessingException ex) {
                final String msg = "Error serialising deletedData for contextId %s".formatted(request.getContextId());
                LOGGER.error(msg, ex, DataMapHolder.getLogMap());
                throw new InvalidPayloadException(msg, ex);            }
        }

        if (resourceChanged.getDeletedData() == null) {
            resourceChanged.setDeletedData(EMPTY_STRING);
        }
        return resourceChanged;
    }
}
