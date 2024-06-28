package com.challenge.manageruser.exception;

import static org.springframework.http.HttpStatus.NOT_FOUND;

public class NotFoundUserException extends BusinessException {
    public NotFoundUserException(final String message) {
        super(message, NOT_FOUND.value());
    }
}
