package com.leander.cinema.exception;

public class InvalidReservationTimeException extends RuntimeException {
    public InvalidReservationTimeException(String message) {
        super(message);
    }
}
