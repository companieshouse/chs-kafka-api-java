package uk.gov.companieshouse.chskafka.auth;

import static uk.gov.companieshouse.chskafka.Application.NAMESPACE;
import static uk.gov.companieshouse.chskafka.auth.AuthConstants.API_KEY_IDENTITY_TYPE;
import static uk.gov.companieshouse.chskafka.auth.AuthConstants.ERIC_AUTHORISED_KEY_PRIVILEGES_HEADER;
import static uk.gov.companieshouse.chskafka.auth.AuthConstants.ERIC_IDENTITY;
import static uk.gov.companieshouse.chskafka.auth.AuthConstants.ERIC_IDENTITY_TYPE;
import static uk.gov.companieshouse.chskafka.auth.AuthConstants.INTERNAL_APP_PRIVILEGE;

import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import uk.gov.companieshouse.chskafka.logging.DataMapHolder;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(NAMESPACE);

    @Override
    public boolean preHandle(HttpServletRequest request, @Nonnull HttpServletResponse response, @Nullable Object handler) {

        String ericIdentity = request.getHeader(ERIC_IDENTITY);
        String ericIdentityType = request.getHeader(ERIC_IDENTITY_TYPE);

        if (StringUtils.isBlank(ericIdentity)) {
            LOGGER.error("ERIC identity is blank", DataMapHolder.getLogMap());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        if (!(API_KEY_IDENTITY_TYPE.equalsIgnoreCase(ericIdentityType))) {
            LOGGER.error("Incorrect ERIC identity type, only API key allowed", DataMapHolder.getLogMap());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        if (!isKeyAuthorised(request)) {
            LOGGER.error("Key is not authorised", DataMapHolder.getLogMap());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        return true;
    }

    private boolean isKeyAuthorised(HttpServletRequest request) {
        String[] privileges = getApiKeyPrivileges(request);

        return ArrayUtils.contains(privileges, INTERNAL_APP_PRIVILEGE);
    }

    private String[] getApiKeyPrivileges(HttpServletRequest request) {
        String commaSeparatedPrivilegeString = request.getHeader(ERIC_AUTHORISED_KEY_PRIVILEGES_HEADER);

        return Optional.ofNullable(commaSeparatedPrivilegeString)
                .map(s -> s.split(","))
                .orElse(new String[]{});
    }
}
