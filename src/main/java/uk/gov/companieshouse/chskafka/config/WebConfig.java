package uk.gov.companieshouse.chskafka.config;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.chskafka.auth.AuthInterceptor;

@Configuration
class WebConfig implements WebMvcConfigurer {

    private static final String ERROR_PATH = "/error"; // NOSONAR
    private static final String HEALTHCHECK_PATH = "/healthcheck"; // NOSONAR

    private final AuthInterceptor authInterceptor;

    WebConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .excludePathPatterns(ERROR_PATH)
                .excludePathPatterns(HEALTHCHECK_PATH);
    }

    @Override
    public void addFormatters(@NonNull FormatterRegistry registry) {
        ApplicationConversionService.configure(registry);
    }
}
