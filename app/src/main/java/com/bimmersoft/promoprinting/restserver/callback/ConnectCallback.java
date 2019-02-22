package com.bimmersoft.promoprinting.restserver.callback;

import com.bimmersoft.promoprinting.restserver.AsyncSocket;

public interface ConnectCallback {
    public void onConnectCompleted(Exception ex, AsyncSocket socket);
}
