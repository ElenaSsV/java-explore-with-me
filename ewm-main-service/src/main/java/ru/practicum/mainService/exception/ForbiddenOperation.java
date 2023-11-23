package ru.practicum.mainService.exception;

public class ForbiddenOperation extends RuntimeException {

    public ForbiddenOperation(String message) {
        super(message);
    }
}

