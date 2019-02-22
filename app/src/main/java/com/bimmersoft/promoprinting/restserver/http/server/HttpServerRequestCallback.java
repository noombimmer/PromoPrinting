package com.bimmersoft.promoprinting.restserver.http.server;


public interface HttpServerRequestCallback {
    void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response);
}
