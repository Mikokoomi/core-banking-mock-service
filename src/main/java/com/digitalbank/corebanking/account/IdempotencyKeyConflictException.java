package com.digitalbank.corebanking.account;

public class IdempotencyKeyConflictException extends RuntimeException {
    public IdempotencyKeyConflictException() {
        super("Idempotency key is already bound to a different account creation request");
    }
}
