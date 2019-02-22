package com.bimmersoft.promoprinting.restserver;

public interface AsyncServerSocket {
    void stop();
    int getLocalPort();
}
