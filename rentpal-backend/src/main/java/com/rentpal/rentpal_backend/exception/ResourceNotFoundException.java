package com.rentpal.rentpal_backend.exception;

public class ResourceNotFoundException extends RuntimeException {

    // Constructor with only message (used in your Service layer)
    public ResourceNotFoundException(String message) {
        super(message);
    }

    // Optional: constructors to match other cases if needed later
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceNotFoundException(Throwable cause) {
        super(cause);
    }
}
