package com.bimmersoft.promoprinting.restserver.callback;

import com.bimmersoft.promoprinting.restserver.AsyncServerSocket;
import com.bimmersoft.promoprinting.restserver.AsyncSocket;


public interface ListenCallback extends CompletedCallback {
    public void onAccepted(AsyncSocket socket);
    public void onListening(AsyncServerSocket socket);
}
