package com.leander.cinema.exception;

public class ForbiddenTicketAccessException extends RuntimeException {
    public ForbiddenTicketAccessException(String message) {
        super(message);
    }
}
