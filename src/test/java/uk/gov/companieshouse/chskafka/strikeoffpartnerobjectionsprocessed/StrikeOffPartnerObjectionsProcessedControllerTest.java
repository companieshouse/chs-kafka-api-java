package uk.gov.companieshouse.chskafka.strikeoffpartnerobjectionsprocessed;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.companieshouse.api.chskafka.ProcessedStrikeOffPartnerObjection;
import uk.gov.companieshouse.chskafka.common.exception.ControllerExceptionHandler;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class StrikeOffPartnerObjectionsProcessedControllerTest {

    MockMvc mockMvc;

    @Mock
    private StrikeOffPartnerObjectionsProcessedService service;
    @InjectMocks
    private StrikeOffPartnerObjectionsProcessedController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();
    }

    @Test
    void shouldReturn201Created() throws Exception {
        // given
        ProcessedStrikeOffPartnerObjection request = new ProcessedStrikeOffPartnerObjection();
        request.setStrikeOffEventId("CH_UNIQUE_CASE_ID_001");
        request.setCompanyNumber("12345678");
        request.setEventType(ProcessedStrikeOffPartnerObjection.EventTypeEnum.OBJECTION);
        request.setSuccessFailureIndicator(ProcessedStrikeOffPartnerObjection.SuccessFailureIndicatorEnum.SUCCESS);

        mockMvc.perform(post("/private/strike-off-partner-objections-processed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturn400BadRequest() throws Exception {
        String invalidJson = "{}";

        mockMvc.perform(post("/private/strike-off-partner-objections-processed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn500InternalServerError() throws Exception {
        ProcessedStrikeOffPartnerObjection request = new ProcessedStrikeOffPartnerObjection();
        request.setStrikeOffEventId("CH_UNIQUE_CASE_ID_001");
        request.setCompanyNumber("12345678");
        request.setEventType(ProcessedStrikeOffPartnerObjection.EventTypeEnum.OBJECTION);
        request.setSuccessFailureIndicator(ProcessedStrikeOffPartnerObjection.SuccessFailureIndicatorEnum.SUCCESS);

        doThrow(new RuntimeException("unexpected failure"))
                .when(service)
                .processAndPublish(ArgumentMatchers.any(ProcessedStrikeOffPartnerObjection.class));

        mockMvc.perform(post("/private/strike-off-partner-objections-processed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }
}

