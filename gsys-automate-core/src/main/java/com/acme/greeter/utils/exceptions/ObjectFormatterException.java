package com.acme.greeter.utils.exceptions;

import org.apache.commons.lang3.StringUtils;
import org.springframework.orm.ObjectRetrievalFailureException;

/**
 * Exception conditions that occur when formatting objects
 */
public class ObjectFormatterException extends ObjectRetrievalFailureException {

    private static final long serialVersionUID = -3698446347548650693L;

    /**
     * Create a new ObjectFormatterException for the given object, with the given
     * explicit message and exception.
     */
    public ObjectFormatterException(Object identifier, String msg, Throwable cause) {
        super(StringUtils.EMPTY, identifier, msg, cause);
    }
}
