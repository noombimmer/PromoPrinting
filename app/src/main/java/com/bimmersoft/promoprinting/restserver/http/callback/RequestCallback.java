package com.bimmersoft.promoprinting.restserver.http.callback;

import com.bimmersoft.promoprinting.restserver.callback.ResultCallback;
import com.bimmersoft.promoprinting.restserver.http.AsyncHttpResponse;

public interface RequestCallback<T> extends ResultCallback<AsyncHttpResponse, T> {
    public void onConnect(AsyncHttpResponse response);
    public void onProgress(AsyncHttpResponse response, long downloaded, long total);
}
