package uk.gov.companieshouse.chskafka.resourcechanged;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.chskafka.ChangedResource;
import uk.gov.companieshouse.chskafka.common.Mapper;
import uk.gov.companieshouse.chskafka.common.Service;
import uk.gov.companieshouse.chskafka.common.kafka.KafkaProducer;
import uk.gov.companieshouse.stream.ResourceChanged;

@Component
class ResourceChangedService implements Service<ChangedResource, ResourceChanged> {

    private final Mapper<ChangedResource, ResourceChanged> resourceChangedMapper;
    private final KafkaProducer<ResourceChanged> kafkaProducer;

    ResourceChangedService(Mapper<ChangedResource, ResourceChanged> resourceChangedMapper,
                           KafkaProducer<ResourceChanged> kafkaProducer) {
        this.resourceChangedMapper = resourceChangedMapper;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public ResourceChanged map(ChangedResource request) {
        return resourceChangedMapper.map(request);
    }

    @Override
    public void produce(ResourceChanged kafkaModel) {
        kafkaProducer.publishMessage(kafkaModel);
    }
}
