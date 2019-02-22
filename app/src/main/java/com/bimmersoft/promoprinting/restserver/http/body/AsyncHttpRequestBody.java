package com.bimmersoft.promoprinting.restserver.http.body;

import com.bimmersoft.promoprinting.restserver.DataEmitter;
import com.bimmersoft.promoprinting.restserver.DataSink;
import com.bimmersoft.promoprinting.restserver.callback.CompletedCallback;
import com.bimmersoft.promoprinting.restserver.http.AsyncHttpRequest;

public interface AsyncHttpRequestBody<T> {
    void write(AsyncHttpRequest request, DataSink sink, CompletedCallback completed);
    void parse(DataEmitter emitter, CompletedCallback completed);
    String getContentType();
    boolean readFullyOnRequest();
    int length();
    T get();
}
