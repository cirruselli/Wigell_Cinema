package com.leander.cinema.exception;

public class BookingCapacityExceededException extends RuntimeException {
    public BookingCapacityExceededException(String message) {
        super(message);
    }
}
