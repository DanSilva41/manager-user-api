package com.challenge.manageruser.exception;

public class BusinessException extends RuntimeException {
    protected final int status;

    public BusinessException(final String message, final int status) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
