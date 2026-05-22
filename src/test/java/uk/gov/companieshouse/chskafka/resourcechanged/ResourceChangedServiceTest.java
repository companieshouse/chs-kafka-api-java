package uk.gov.companieshouse.chskafka.resourcechanged;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.chskafka.ChangedResource;
import uk.gov.companieshouse.chskafka.common.Mapper;
import uk.gov.companieshouse.chskafka.common.kafka.KafkaProducer;
import uk.gov.companieshouse.stream.ResourceChanged;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ResourceChangedServiceTest {

    @Mock
    private Mapper<ChangedResource, ResourceChanged> resourceChangedMapper;
    @Mock
    private KafkaProducer<ResourceChanged> kafkaProducer;
    @InjectMocks
    private ResourceChangedService resourceChangedService;

    @Test
    void shouldDelegateMapping() {
        ChangedResource request = new ChangedResource();
        resourceChangedService.map(request);
        verify(resourceChangedMapper).map(request);
    }

    @Test
    void shouldDelegatePublishing() {
        ResourceChanged resourceChanged = new ResourceChanged();
        resourceChangedService.produce(resourceChanged);
        verify(kafkaProducer).publishMessage(resourceChanged);
    }
}
