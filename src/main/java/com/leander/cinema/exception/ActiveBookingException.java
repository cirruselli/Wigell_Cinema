package com.leander.cinema.exception;

public class ActiveBookingException extends RuntimeException {
    public ActiveBookingException(String message) {
        super(message);
    }
}
