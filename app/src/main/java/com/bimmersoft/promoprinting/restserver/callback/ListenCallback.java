package com.bimmersoft.promoprinting.restserver.callback;

import com.bimmersoft.promoprinting.restserver.AsyncServerSocket;
import com.bimmersoft.promoprinting.restserver.AsyncSocket;


public interface ListenCallback extends CompletedCallback {
    void onAccepted(AsyncSocket socket);
    void onListening(AsyncServerSocket socket);
}
