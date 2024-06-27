package com.challenge.manageruser.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class InvalidDepartmentException extends BusinessException {
    public InvalidDepartmentException(final String message) {
        super(message, BAD_REQUEST.value());
    }
}
