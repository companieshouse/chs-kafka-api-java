package uk.gov.companieshouse.chskafka.strikeoffpartnerobjectionsprocessed;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.chskafka.ProcessedStrikeOffPartnerObjection;
import uk.gov.companieshouse.chskafka.common.exception.BadGatewayException;
import uk.gov.companieshouse.chskafka.common.kafka.KafkaProducer;
import uk.gov.companieshouse.strikeoff.partner.objections.StrikeOffPartnerObjectionsProcessed;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StrikeOffPartnerObjectionsProcessedServiceTest {

    @Mock
    private KafkaProducer<StrikeOffPartnerObjectionsProcessed> kafkaProducer;

    @Mock
    private StrikeOffPartnerObjectionsProcessedMapper mapper;

    @InjectMocks
    private StrikeOffPartnerObjectionsProcessedService service;

    @Test
    void shouldMapRequestToKafkaModel() {
        // given
        ProcessedStrikeOffPartnerObjection request = new ProcessedStrikeOffPartnerObjection();
        StrikeOffPartnerObjectionsProcessed expected = new StrikeOffPartnerObjectionsProcessed();
        when(mapper.map(request)).thenReturn(expected);

        // when
        StrikeOffPartnerObjectionsProcessed actual = service.map(request);

        // then
        assertSame(expected, actual);
        verify(mapper).map(request);
    }

    @Test
    void shouldPublishMessageToKafka() {
        // given
        StrikeOffPartnerObjectionsProcessed kafkaModel = new StrikeOffPartnerObjectionsProcessed();

        // when
        service.produce(kafkaModel);

        // then
        verify(kafkaProducer).publishMessage(kafkaModel);
    }

    @Test
    void shouldThrowBadGatewayExceptionWhenKafkaPublishFails() {
        // given
        StrikeOffPartnerObjectionsProcessed kafkaModel = new StrikeOffPartnerObjectionsProcessed();
        doThrow(new RuntimeException("kafka down"))
                .when(kafkaProducer)
                .publishMessage(kafkaModel);

        // when / then
        assertThrows(BadGatewayException.class, () -> service.produce(kafkaModel));
    }
}