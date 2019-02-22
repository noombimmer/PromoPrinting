package com.bimmersoft.promoprinting.restserver.http.filter;

public class ChunkedDataException extends Exception {
    public ChunkedDataException(String message) {
        super(message);
    }
}
