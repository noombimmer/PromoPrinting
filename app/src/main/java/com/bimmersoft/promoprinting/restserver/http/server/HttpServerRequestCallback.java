package com.bimmersoft.promoprinting.restserver.http.server;


public interface HttpServerRequestCallback {
    public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response);
}
