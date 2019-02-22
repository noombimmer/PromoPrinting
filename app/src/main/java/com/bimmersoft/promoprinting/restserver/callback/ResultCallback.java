package com.bimmersoft.promoprinting.restserver.callback;

public interface ResultCallback<S, T> {
    void onCompleted(Exception e, S source, T result);
}
