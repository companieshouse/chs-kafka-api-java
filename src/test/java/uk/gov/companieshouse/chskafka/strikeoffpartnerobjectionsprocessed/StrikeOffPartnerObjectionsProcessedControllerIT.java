package uk.gov.companieshouse.chskafka.strikeoffpartnerobjectionsprocessed;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.ResultActions;
import uk.gov.companieshouse.chskafka.common.AbstractControllerIT;
import uk.gov.companieshouse.strikeoff.partner.objections.StrikeOffPartnerObjectionsProcessed;

import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class StrikeOffPartnerObjectionsProcessedControllerIT extends AbstractControllerIT<StrikeOffPartnerObjectionsProcessed> {

    StrikeOffPartnerObjectionsProcessedControllerIT() {
        super(
                "strike-off-partner-objections-processed",
                StrikeOffPartnerObjectionsProcessed.class,
                StrikeOffPartnerObjectionsProcessed.getClassSchema());
    }

    @ParameterizedTest
    @CsvSource({
            "/strikeoffpartnerobjectionsprocessed/accepted-message.json, /strikeoffpartnerobjectionsprocessed/accepted-request.json",
            "/strikeoffpartnerobjectionsprocessed/failure-with-date-message.json, /strikeoffpartnerobjectionsprocessed/failure-with-date-request.json",
            "/strikeoffpartnerobjectionsprocessed/failure-without-date-message.json, /strikeoffpartnerobjectionsprocessed/failure-without-date-request.json"
    })
    void shouldPublishStrikeOffPartnerObjectionsProcessedToKafka(
            String expectedMessageFile,
            String requestFile) throws Exception {
        // given
        String requestBody = readResource(requestFile);
        StrikeOffPartnerObjectionsProcessed expected = readAndDeserialise(expectedMessageFile);

        // when
        ResultActions response = mockMvcPost(
                requestBody,
                "/private/strike-off-partner-objections-processed");

        response.andExpect(status().isCreated());

        // then
        StrikeOffPartnerObjectionsProcessed actual = consumeAndDeserialise();
        assertEquals(expected, actual);
    }


    @ParameterizedTest
    @CsvSource({
            "/strikeoffpartnerobjectionsprocessed/request-missing-strike-off-event-id.json, strikeOffEventId",
            "/strikeoffpartnerobjectionsprocessed/request-missing-company-number.json, companyNumber",
            "/strikeoffpartnerobjectionsprocessed/request-missing-event-type.json, eventType",
            "/strikeoffpartnerobjectionsprocessed/request-missing-success-failure-indicator.json, successFailureIndicator"
    })
    void shouldReturn400ForAdditionalInvalidPayloads(String requestFile, String expectedField) throws Exception {
        // given
        String requestBody = readResource(requestFile);

        // when
        ResultActions response = mockMvcPost(requestBody, "/private/strike-off-partner-objections-processed");

        // then
        response.andExpectAll(status().isBadRequest(),
                jsonPath("$.title").value(HttpStatus.BAD_REQUEST.getReasonPhrase()),
                jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()),
                jsonPath("$.detail").value("Invalid request content."),
                jsonPath("$.instance").value("/private/strike-off-partner-objections-processed"),
                jsonPath("$." + expectedField).value("[null] must not be null"));
        assertZeroMessagesPublished();
    }

}