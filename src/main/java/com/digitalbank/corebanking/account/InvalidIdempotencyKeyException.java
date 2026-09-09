package com.digitalbank.corebanking.account;

public class InvalidIdempotencyKeyException extends RuntimeException {
    public InvalidIdempotencyKeyException() { super("Idempotency-Key must not be blank and must not exceed 200 characters"); }
}
