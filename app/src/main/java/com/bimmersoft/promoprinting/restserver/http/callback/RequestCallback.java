package com.bimmersoft.promoprinting.restserver.http.callback;

import com.bimmersoft.promoprinting.restserver.callback.ResultCallback;
import com.bimmersoft.promoprinting.restserver.http.AsyncHttpResponse;

public interface RequestCallback<T> extends ResultCallback<AsyncHttpResponse, T> {
    void onConnect(AsyncHttpResponse response);
    void onProgress(AsyncHttpResponse response, long downloaded, long total);
}
