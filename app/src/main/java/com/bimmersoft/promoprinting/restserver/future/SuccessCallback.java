package com.bimmersoft.promoprinting.restserver.future;

public interface SuccessCallback<T> {
    void success(T value) throws Exception;
}
