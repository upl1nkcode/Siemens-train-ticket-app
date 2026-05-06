package com.trainsystem.exception;

public class OverbookingException extends RuntimeException {

    public OverbookingException(int requested, int available) {
        super("Cannot book " + requested + " seat(s) — only " + available + " available.");
    }
}
