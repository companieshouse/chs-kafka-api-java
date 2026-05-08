package uk.gov.companieshouse.chskafka.resourcechanged;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.chskafka.ChangedResource;
import uk.gov.companieshouse.api.chskafka.ChangedResourceEvent;
import uk.gov.companieshouse.chskafka.common.exception.InvalidPayloadException;
import uk.gov.companieshouse.stream.ResourceChanged;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceChangedMapperTest {

    @Mock
    private ObjectMapper objectMapper;

    @Test
    void mapShouldMapAllStandardFields() {
        ResourceChangedMapper mapper = new ResourceChangedMapper(new ObjectMapper());
        ChangedResource request = createRequest("changed", null);

        ResourceChanged result = mapper.map(request);

        assertEquals(request.getResourceUri(), result.getResourceUri());
        assertEquals(request.getResourceKind(), result.getResourceKind());
        assertEquals(request.getContextId(), result.getContextId());
        assertEquals(request.getEvent().getType(), result.getEvent().getType());
        assertEquals(request.getEvent().getPublishedAt(), result.getEvent().getPublishedAt());
        assertEquals(request.getEvent().getFieldsChanged(), result.getEvent().getFieldsChanged());
    }

    @Test
    void mapShouldSerializeDeletedDataWhenEventTypeIsDeleted() throws Exception {
        ResourceChangedMapper mapper = new ResourceChangedMapper(objectMapper);
        Map<String, String> deletedData = Map.of("status", "dissolved");
        ChangedResource request = createRequest("deleted", deletedData);

        when(objectMapper.writeValueAsString(deletedData)).thenReturn("{\"status\":\"dissolved\"}");

        ResourceChanged result = mapper.map(request);

        assertEquals("{\"status\":\"dissolved\"}", result.getDeletedData());
    }

    @Test
    void mapShouldNotSerializeDeletedDataWhenEventTypeIsNotDeleted() {
        ResourceChangedMapper mapper = new ResourceChangedMapper(objectMapper);
        ChangedResource request = createRequest("changed", Map.of("status", "dissolved"));

        ResourceChanged result = mapper.map(request);

        verifyNoInteractions(objectMapper);
        assertEquals("", result.getDeletedData());
    }

    @Test
    void mapShouldNotSerializeDeletedDataWhenDeletedDataIsNull() {
        ResourceChangedMapper mapper = new ResourceChangedMapper(objectMapper);
        ChangedResource request = createRequest("deleted", null);

        ResourceChanged result = mapper.map(request);

        verifyNoInteractions(objectMapper);
        assertEquals("", result.getDeletedData());
    }

    @Test
    void mapShouldThrowSerialisationExceptionWhenDeletedDataSerialisationFails() throws Exception {
        ResourceChangedMapper mapper = new ResourceChangedMapper(objectMapper);
        Map<String, String> deletedData = Map.of("status", "dissolved");
        ChangedResource request = createRequest("deleted", deletedData);

        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("boom") {});

        assertThrows(InvalidPayloadException.class, () -> mapper.map(request));
    }

    private ChangedResource createRequest(String eventType, Object deletedData) {
        ChangedResourceEvent event = new ChangedResourceEvent();
        event.setType(eventType);
        event.setPublishedAt("2026-05-18T10:00:00Z");
        event.setFieldsChanged(List.of("links.document_metadata"));

        ChangedResource request = new ChangedResource();
        request.setResourceUri("/company/00006400/filing-history/abc");
        request.setResourceKind("filing-history");
        request.setContextId("context-123");
        request.setDeletedData(deletedData);
        request.setEvent(event);
        return request;
    }
}
