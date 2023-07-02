package org.example.exception;

import jakarta.validation.ConstraintViolation;

import java.util.Set;

public class ValidationException extends RuntimeException {

    private final Set<ConstraintViolation<?>> violations;

    public ValidationException(String message, Set<ConstraintViolation<?>> violations) {
        super(message);
        this.violations = violations;
    }

    public Set<ConstraintViolation<?>> getViolations() {
        return violations;
    }
}
