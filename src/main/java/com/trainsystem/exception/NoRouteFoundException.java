package com.trainsystem.exception;

public class NoRouteFoundException extends RuntimeException {

    public NoRouteFoundException(String from, String to) {
        super("No route found between '" + from + "' and '" + to + "'.");
    }
}
