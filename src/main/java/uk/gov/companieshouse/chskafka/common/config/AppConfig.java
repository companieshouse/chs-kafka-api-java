package uk.gov.companieshouse.chskafka.common.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.openapitools.jackson.nullable.JsonNullableJackson3Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class AppConfig {

    @Bean("snakeCaseMapper")
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .addModule(new JsonNullableJackson3Module())
                .changeDefaultPropertyInclusion(incl ->
                        incl.withContentInclusion(JsonInclude.Include.NON_NULL)
                                .withValueInclusion(JsonInclude.Include.NON_NULL))
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .build();
    }
}
