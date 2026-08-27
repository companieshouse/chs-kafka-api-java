package uk.gov.companieshouse.chskafka.strikeoffpartnerobjectionsprocessed;

import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.chskafka.ProcessedStrikeOffPartnerObjection;
import uk.gov.companieshouse.strikeoff.partner.objections.StrikeOffPartnerObjectionsProcessed;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static uk.gov.companieshouse.strikeoff.partner.objections.ProcessedEventType.WITHDRAWAL;
import static uk.gov.companieshouse.strikeoff.partner.objections.SuccessFailureIndicator.FAILURE;

class StrikeOffPartnerObjectionsProcessedMapperTest {

    private final StrikeOffPartnerObjectionsProcessedMapper mapper =
            new StrikeOffPartnerObjectionsProcessedMapper();

    @Test
    void shouldMapAllFieldsForSuccessObjection() {
        // given
        ProcessedStrikeOffPartnerObjection request = getProcessedStrikeOffPartnerObjection(ProcessedStrikeOffPartnerObjection.EventTypeEnum.OBJECTION, ProcessedStrikeOffPartnerObjection.SuccessFailureIndicatorEnum.SUCCESS);

        // when
        StrikeOffPartnerObjectionsProcessed actual = mapper.map(request);

        // then
        assertEquals("CH_UNIQUE_CASE_ID_001", actual.getStrikeOffEventId());
        assertEquals(uk.gov.companieshouse.strikeoff.partner.objections.ProcessedEventType.OBJECTION, actual.getEventType());
        assertEquals(uk.gov.companieshouse.strikeoff.partner.objections.SuccessFailureIndicator.SUCCESS, actual.getSuccessFailureIndicator());
        assertNull(actual.getErrorMessage());
        assertEquals(LocalDate.of(2026, 10, 31), actual.getInitialExpirationOn());
        assertEquals("12345678", actual.getCompanyNumber());
    }

    private static ProcessedStrikeOffPartnerObjection getProcessedStrikeOffPartnerObjection(ProcessedStrikeOffPartnerObjection.EventTypeEnum type, ProcessedStrikeOffPartnerObjection.SuccessFailureIndicatorEnum indicator) {
        ProcessedStrikeOffPartnerObjection request = new ProcessedStrikeOffPartnerObjection();
        request.setStrikeOffEventId("CH_UNIQUE_CASE_ID_001");
        request.setEventType(type);
        request.setSuccessFailureIndicator(indicator);
        request.setErrorMessage(null);
        request.setInitialExpirationOn(LocalDate.of(2026, 10, 31));
        request.setCompanyNumber("12345678");
        return request;
    }

    @Test
    void shouldSetInitialExpirationOnNullForFailure() {
        // given
        ProcessedStrikeOffPartnerObjection request = getProcessedStrikeOffPartnerObjection(ProcessedStrikeOffPartnerObjection.EventTypeEnum.WITHDRAWAL, ProcessedStrikeOffPartnerObjection.SuccessFailureIndicatorEnum.FAILURE);
        request.setErrorMessage("Failed in CHIPS");
        // when
        StrikeOffPartnerObjectionsProcessed actual = mapper.map(request);

        // then
        assertEquals("CH_UNIQUE_CASE_ID_001", actual.getStrikeOffEventId());
        assertEquals(WITHDRAWAL, actual.getEventType());
        assertEquals(FAILURE, actual.getSuccessFailureIndicator());
        assertEquals("Failed in CHIPS", actual.getErrorMessage());
        assertNull(actual.getInitialExpirationOn());
        assertEquals("12345678", actual.getCompanyNumber());
    }
}