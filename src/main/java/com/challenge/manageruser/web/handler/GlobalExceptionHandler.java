package com.challenge.manageruser.web.handler;

import com.challenge.manageruser.exception.BusinessException;
import com.google.common.collect.Iterables;
import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(final RuntimeException exception) {
        final var problemDetail = ProblemDetail.forStatusAndDetail(INTERNAL_SERVER_ERROR, ExceptionUtils.getRootCauseMessage(exception));
        problemDetail.setTitle("An unexpected failure occurred");
        return buildResponseProblemDetail(problemDetail);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusinessException(final BusinessException exception) {
        final var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.valueOf(exception.getStatus()), exception.getMessage());
        problemDetail.setTitle("Invalid request");
        return buildResponseProblemDetail(problemDetail);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(final DataIntegrityViolationException exception) {
        final var problemDetail = ProblemDetail.forStatusAndDetail(CONFLICT, ExceptionUtils.getRootCauseMessage(exception));
        problemDetail.setTitle("Constraint error when interacting with the database");
        return buildResponseProblemDetail(problemDetail);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException exception, @NonNull final HttpHeaders headers, @NonNull final HttpStatusCode status, @NonNull final WebRequest request) {
        final var messages = exception.getBindingResult().getFieldErrors().stream().map(error -> {
            final var formattedField = LOWER_CAMEL.to(LOWER_UNDERSCORE, error.getField());
            return "%s: %s".formatted(formattedField, error.getDefaultMessage());
        }).toList();

        final var problemDetail = ProblemDetail.forStatus(BAD_REQUEST);
        problemDetail.setTitle("Invalid request content");
        problemDetail.setProperty("messages", messages);
        return buildResponseProblemDetail(problemDetail);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(final ConstraintViolationException exception) {
        final var messages = exception.getConstraintViolations().stream().map(error -> {
            final var lastField = Iterables.getLast(error.getPropertyPath());
            final var formattedField = LOWER_CAMEL.to(LOWER_UNDERSCORE, lastField.getName());
            return "%s: %s".formatted(formattedField, error.getMessage());
        }).toList();

        final var problemDetail = ProblemDetail.forStatus(BAD_REQUEST);
        problemDetail.setTitle("Invalid request content");
        problemDetail.setProperty("messages", messages);
        return buildResponseProblemDetail(problemDetail);
    }

    private ResponseEntity<Object> buildResponseProblemDetail(final ProblemDetail problemDetail) {
        problemDetail.setProperty("timestamp", Instant.now());

        log.error("m=handle error, status={}, title={}, detail={}", problemDetail.getStatus(), problemDetail.getTitle(), problemDetail.getDetail());
        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }
}
