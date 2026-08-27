package uk.gov.companieshouse.chskafka.strikeoffpartnerobjectionsprocessed;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.chskafka.ProcessedStrikeOffPartnerObjection;
import uk.gov.companieshouse.chskafka.common.Mapper;
import uk.gov.companieshouse.strikeoff.partner.objections.ProcessedEventType;
import uk.gov.companieshouse.strikeoff.partner.objections.StrikeOffPartnerObjectionsProcessed;
import uk.gov.companieshouse.strikeoff.partner.objections.SuccessFailureIndicator;

import java.time.LocalDate;

@Component
public class StrikeOffPartnerObjectionsProcessedMapper implements Mapper<ProcessedStrikeOffPartnerObjection, StrikeOffPartnerObjectionsProcessed> {

    public StrikeOffPartnerObjectionsProcessed map(ProcessedStrikeOffPartnerObjection request) {
        StrikeOffPartnerObjectionsProcessed avro = new StrikeOffPartnerObjectionsProcessed();
        avro.setStrikeOffEventId(request.getStrikeOffEventId());
        avro.setEventType(mapEventType(request.getEventType()));
        avro.setSuccessFailureIndicator(mapIndicator(request.getSuccessFailureIndicator()));
        avro.setErrorMessage(request.getErrorMessage());
        avro.setInitialExpirationOn(mapInitialExpirationOn(request));
        avro.setCompanyNumber(request.getCompanyNumber());
        return avro;
    }

    private LocalDate mapInitialExpirationOn(ProcessedStrikeOffPartnerObjection request) {
        return ProcessedStrikeOffPartnerObjection.SuccessFailureIndicatorEnum.SUCCESS == request.getSuccessFailureIndicator() ? request.getInitialExpirationOn() : null;
    }

    private ProcessedEventType mapEventType(ProcessedStrikeOffPartnerObjection.EventTypeEnum eventType) {
        return ProcessedEventType.valueOf(eventType.name());
    }

    private SuccessFailureIndicator mapIndicator(ProcessedStrikeOffPartnerObjection.SuccessFailureIndicatorEnum indicator) {
        return SuccessFailureIndicator.valueOf(indicator.name());
    }
}