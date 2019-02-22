package com.bimmersoft.promoprinting.restserver.http.filter;

public class PrematureDataEndException extends Exception {
    public PrematureDataEndException(String message) {
        super(message);
    }
}
