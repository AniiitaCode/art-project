package com.example.art.exception;

public class DateMustBeInFutureException extends RuntimeException {

    public DateMustBeInFutureException(String message) {
        super(message);
    }
}
