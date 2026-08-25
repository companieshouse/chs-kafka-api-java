package uk.gov.companieshouse.chskafka.strikeoffpartnerobjectionsprocessed;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.ResultActions;
import uk.gov.companieshouse.chskafka.common.AbstractControllerIT;
import uk.gov.companieshouse.strikeoff.partner.objections.StrikeOffPartnerObjectionsProcessed;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
            "/strikeoffpartnerobjectionsprocessed/accepted-failure-message.json, /strikeoffpartnerobjectionsprocessed/accepted-failure-request.json",
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




    @Test
    void postInvalidRequestReturnsBadRequest() throws Exception {
        String requestBody = readResource("/strikeoffpartnerobjectionsprocessed/request-invalid.json");

        ResultActions response = mockMvcPost(
                requestBody,
                "/private/strike-off-partner-objections-processed");

        response.andExpect(status().isBadRequest());
        assertZeroMessagesPublished();
    }

}