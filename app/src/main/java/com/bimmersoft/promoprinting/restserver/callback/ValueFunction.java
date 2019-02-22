package com.bimmersoft.promoprinting.restserver.callback;

public interface ValueFunction<T> {
    T getValue() throws Exception;
}
