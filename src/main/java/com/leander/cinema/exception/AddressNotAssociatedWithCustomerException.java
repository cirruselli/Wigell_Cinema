package com.leander.cinema.exception;

public class AddressNotAssociatedWithCustomerException extends RuntimeException {
    public AddressNotAssociatedWithCustomerException(String message) {
        super(message);
    }
}
