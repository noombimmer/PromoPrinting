package com.bimmersoft.promoprinting.restserver.http;

public class ConnectionFailedException extends Exception {
    public ConnectionFailedException(String message) {
        super(message);
    }
}
