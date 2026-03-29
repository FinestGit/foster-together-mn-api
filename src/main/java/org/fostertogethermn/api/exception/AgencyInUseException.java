package org.fostertogethermn.api.exception;

public class AgencyInUseException extends RuntimeException {
    public AgencyInUseException(String message) {
        super(message);
    }

    public AgencyInUseException(String message, Throwable cause) {
        super(message, cause);
    }
}
