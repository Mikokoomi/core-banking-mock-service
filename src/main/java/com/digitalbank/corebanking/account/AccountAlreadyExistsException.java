package com.digitalbank.corebanking.account;

public class AccountAlreadyExistsException extends RuntimeException {
    public AccountAlreadyExistsException() { super("An account already exists for this application"); }
}
