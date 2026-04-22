package uk.gov.companieshouse.chskafka.logging;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;
import java.util.UUID;

@Aspect
@Component
public class ControllerLoggingAdvice {

    @Before("@within(org.springframework.web.bind.annotation.RestController)")
    public void captureFromHeader() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            String requestId = attrs.getRequest().getHeader("X-Request-Id");
            DataMapHolder.initialise(Optional.ofNullable(requestId)
                    .orElse(UUID.randomUUID().toString()));
        }
    }
}
