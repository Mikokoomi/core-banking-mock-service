package com.digitalbank.corebanking.account;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String accountNumber) { super("Account not found: " + accountNumber); }
}
