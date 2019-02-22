package com.bimmersoft.promoprinting.restserver.http.server;

import com.bimmersoft.promoprinting.restserver.AsyncSocket;
import com.bimmersoft.promoprinting.restserver.DataEmitter;
import com.bimmersoft.promoprinting.restserver.http.Headers;
import com.bimmersoft.promoprinting.restserver.http.Multimap;
import com.bimmersoft.promoprinting.restserver.http.body.AsyncHttpRequestBody;

import java.util.Map;
import java.util.regex.Matcher;

public interface AsyncHttpServerRequest extends DataEmitter {
    Headers getHeaders();
    Matcher getMatcher();
    void setMatcher(Matcher matcher);
    <T extends AsyncHttpRequestBody> T getBody();
    AsyncSocket getSocket();
    String getPath();
    Multimap getQuery();
    String getMethod();

    String get(String name);
    Map<String, Object> getState();
}
