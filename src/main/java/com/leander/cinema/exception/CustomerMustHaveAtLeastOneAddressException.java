package com.leander.cinema.exception;

public class CustomerMustHaveAtLeastOneAddressException extends RuntimeException {
    public CustomerMustHaveAtLeastOneAddressException(String message) {
        super(message);
    }
}
