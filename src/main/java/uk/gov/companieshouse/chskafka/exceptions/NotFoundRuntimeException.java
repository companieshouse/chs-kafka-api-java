package uk.gov.companieshouse.chskafka.exceptions;

import uk.gov.companieshouse.logging.util.DataMap;

import static uk.gov.companieshouse.chskafka.util.LoggingUtil.LOGGER;

public class NotFoundRuntimeException extends RuntimeException {

    public NotFoundRuntimeException(final String xRequestId, final String exceptionMessage, final Exception exception , DataMap dataMap) {
        super( exceptionMessage );
        LOGGER.errorContext( xRequestId, exception,dataMap.getLogMap());
    }

}


