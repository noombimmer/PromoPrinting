package com.bimmersoft.promoprinting.restserver.http.filter;

public class DataRemainingException extends Exception {
    public DataRemainingException(String message, Exception cause) {
        super(message, cause);
    }
}
