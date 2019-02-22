package com.bimmersoft.promoprinting.restserver;

import com.bimmersoft.promoprinting.restserver.callback.CompletedCallback;
import com.bimmersoft.promoprinting.restserver.callback.DataCallback;

public interface DataEmitter {
    void setDataCallback(DataCallback callback);
    DataCallback getDataCallback();
    boolean isChunked();
    void pause();
    void resume();
    void close();
    boolean isPaused();
    void setEndCallback(CompletedCallback callback);
    CompletedCallback getEndCallback();
    AsyncServer getServer();
    String charset();
}
