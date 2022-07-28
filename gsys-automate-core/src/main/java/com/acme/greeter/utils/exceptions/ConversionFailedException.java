package com.acme.greeter.utils.exceptions;

import lombok.Generated;
import org.springframework.core.convert.ConversionException;

/**
 * Error stating that creating or converting objects to and from a specific
 * class is not possible
 *
 * @see ConversionException
 */
public class ConversionFailedException extends ConversionException {

    private static final @Generated long serialVersionUID = 4217766122055678806L;

    /**
     * Constructor of {@link ConversionFailedException}
     *
     * @param message -> Message to be displayed
     * @param cause   -> Throwable cause
     */
    public ConversionFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
