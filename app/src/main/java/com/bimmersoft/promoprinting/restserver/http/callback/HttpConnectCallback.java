package com.bimmersoft.promoprinting.restserver.http.callback;


import com.bimmersoft.promoprinting.restserver.http.AsyncHttpResponse;

public interface HttpConnectCallback {
    public void onConnectCompleted(Exception ex, AsyncHttpResponse response);
}
