package com.bimmersoft.promoprinting.restserver.callback;

import com.bimmersoft.promoprinting.restserver.AsyncSocket;

public interface ConnectCallback {
    void onConnectCompleted(Exception ex, AsyncSocket socket);
}
