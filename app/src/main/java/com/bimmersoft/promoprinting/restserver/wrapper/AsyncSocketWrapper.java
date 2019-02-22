package com.bimmersoft.promoprinting.restserver.wrapper;

import com.bimmersoft.promoprinting.restserver.AsyncSocket;

public interface AsyncSocketWrapper extends AsyncSocket, DataEmitterWrapper {
    public AsyncSocket getSocket();
}
