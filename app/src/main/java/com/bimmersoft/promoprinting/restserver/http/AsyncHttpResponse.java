package com.bimmersoft.promoprinting.restserver.http;

import com.bimmersoft.promoprinting.restserver.AsyncSocket;
import com.bimmersoft.promoprinting.restserver.DataEmitter;

public interface AsyncHttpResponse extends DataEmitter {
    public String protocol();
    public String message();
    public int code();
    public Headers headers();
    public AsyncSocket detachSocket();
    public AsyncHttpRequest getRequest();
}
