package uk.gov.companieshouse.chskafka.common.auth;

final class AuthConstants {

    private AuthConstants() {
    }

    static final String OAUTH2_IDENTITY_TYPE = "oauth2";
    static final String API_KEY_IDENTITY_TYPE = "key";
    static final String ERIC_AUTHORISED_KEY_PRIVILEGES_HEADER = "ERIC-Authorised-Key-Privileges";
    static final String ERIC_IDENTITY = "ERIC-Identity";
    static final String ERIC_IDENTITY_TYPE = "ERIC-Identity-Type";
    static final String INTERNAL_APP_PRIVILEGE = "internal-app";
}
