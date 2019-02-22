package com.bimmersoft.promoprinting.restserver.http;

import com.bimmersoft.promoprinting.restserver.AsyncSocket;
import com.bimmersoft.promoprinting.restserver.DataEmitter;

public interface AsyncHttpResponse extends DataEmitter {
    String protocol();
    String message();
    int code();
    Headers headers();
    AsyncSocket detachSocket();
    AsyncHttpRequest getRequest();
}
