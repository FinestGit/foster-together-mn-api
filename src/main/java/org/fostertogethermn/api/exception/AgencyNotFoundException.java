package org.fostertogethermn.api.exception;

public class AgencyNotFoundException extends RuntimeException {
    public AgencyNotFoundException(String message) {
        super(message);
    }

    public AgencyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
