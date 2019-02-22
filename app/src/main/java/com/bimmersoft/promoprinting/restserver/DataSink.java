package com.bimmersoft.promoprinting.restserver;

import com.bimmersoft.promoprinting.restserver.callback.CompletedCallback;
import com.bimmersoft.promoprinting.restserver.callback.WritableCallback;

public interface DataSink {
    public void write(ByteBufferList bb);
    public void setWriteableCallback(WritableCallback handler);
    public WritableCallback getWriteableCallback();
    
    public boolean isOpen();
    public void end();
    public void setClosedCallback(CompletedCallback handler);
    public CompletedCallback getClosedCallback();
    public AsyncServer getServer();
}
