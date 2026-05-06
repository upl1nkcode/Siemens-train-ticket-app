package com.trainsystem.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String entity, Object identifier) {
        super(entity + " not found with identifier: " + identifier);
    }
}
