package uk.gov.companieshouse.chskafka.resourcechanged;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.chskafka.ChangedResource;
import uk.gov.companieshouse.api.chskafka.ChangedResourceEvent;
import uk.gov.companieshouse.chskafka.common.Service;
import uk.gov.companieshouse.chskafka.common.logging.DataMapHolder;
import uk.gov.companieshouse.stream.ResourceChanged;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ResourceChangedControllerTest {

    @Mock
    private Service<ChangedResource, ResourceChanged> messageSendService;
    @InjectMocks
    private ResourceChangedController controller;

    @Test
    void shouldReturn201Created() {
        // given
        ChangedResource changedResource = new ChangedResource();
        changedResource.setContextId("context_123");
        changedResource.setResourceKind("company");
        changedResource.setResourceUri("/company/00006400");
        ChangedResourceEvent eventRecord =  new ChangedResourceEvent();
        eventRecord.setPublishedAt("2025-02-20T17:00:00Z");
        eventRecord.setFieldsChanged(List.of("field1", "field2"));
        eventRecord.setType("resource_changed");
        changedResource.setEvent(eventRecord);
        changedResource.setDeletedData("");
        // when
        ResponseEntity<Void> actual = controller.processRequest(changedResource);

        // then
        assertEquals(201, actual.getStatusCode().value());
        verify(messageSendService).processAndPublish(changedResource);
        assertEquals("context_123", DataMapHolder.getLogMap().get("context_id"));
    }
}
