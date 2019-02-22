package com.bimmersoft.promoprinting.restserver.callback;

import com.bimmersoft.promoprinting.restserver.ByteBufferList;
import com.bimmersoft.promoprinting.restserver.DataEmitter;


public interface DataCallback {
    public class NullDataCallback implements DataCallback {
        @Override
        public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
            bb.recycle();
        }
    }

    public void onDataAvailable(DataEmitter emitter, ByteBufferList bb);
}
