package com.challenge.manageruser.exception;

import static org.springframework.http.HttpStatus.NOT_FOUND;

public class NotFoundDepartmentException extends BusinessException {
    public NotFoundDepartmentException(final String message) {
        super(message, NOT_FOUND.value());
    }
}
