package com.digitalbank.corebanking.common;

import com.digitalbank.corebanking.account.*;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.OffsetDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AccountAlreadyExistsException.class)
    ResponseEntity<ErrorResponse> duplicate(AccountAlreadyExistsException e) { return error(HttpStatus.CONFLICT, e, "ACCOUNT_ALREADY_EXISTS_FOR_APPLICATION"); }
    @ExceptionHandler(AccountNotFoundException.class)
    ResponseEntity<ErrorResponse> missing(AccountNotFoundException e) { return error(HttpStatus.NOT_FOUND, e, "ACCOUNT_NOT_FOUND"); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException e) { return error(HttpStatus.BAD_REQUEST, e, "VALIDATION_ERROR"); }
    private ResponseEntity<ErrorResponse> error(HttpStatus status, Exception e, String code) {
        return ResponseEntity.status(status).body(new ErrorResponse(false, e.getMessage(), code, OffsetDateTime.now()));
    }
}
