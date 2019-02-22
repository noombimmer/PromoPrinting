package com.bimmersoft.promoprinting.restserver;


public interface AsyncSocket extends DataEmitter, DataSink {
    public AsyncServer getServer();
}
