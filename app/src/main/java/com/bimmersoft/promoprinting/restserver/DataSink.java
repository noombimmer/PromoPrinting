package com.bimmersoft.promoprinting.restserver;

import com.bimmersoft.promoprinting.restserver.callback.CompletedCallback;
import com.bimmersoft.promoprinting.restserver.callback.WritableCallback;

public interface DataSink {
    void write(ByteBufferList bb);
    void setWriteableCallback(WritableCallback handler);
    WritableCallback getWriteableCallback();
    
    boolean isOpen();
    void end();
    void setClosedCallback(CompletedCallback handler);
    CompletedCallback getClosedCallback();
    AsyncServer getServer();
}
