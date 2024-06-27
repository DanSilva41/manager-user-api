package com.challenge.manageruser.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class InvalidUserException extends BusinessException {
    public InvalidUserException(final String message) {
        super(message, BAD_REQUEST.value());
    }
}
