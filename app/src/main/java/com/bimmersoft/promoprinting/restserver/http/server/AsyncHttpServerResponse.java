package com.bimmersoft.promoprinting.restserver.http.server;

import com.bimmersoft.promoprinting.restserver.AsyncSocket;
import com.bimmersoft.promoprinting.restserver.ByteBufferList;
import com.bimmersoft.promoprinting.restserver.DataSink;
import com.bimmersoft.promoprinting.restserver.callback.CompletedCallback;
import com.bimmersoft.promoprinting.restserver.http.AsyncHttpResponse;
import com.bimmersoft.promoprinting.restserver.http.Headers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;

public interface AsyncHttpServerResponse extends DataSink, CompletedCallback {
    void end();
    void send(String contentType, byte[] bytes);
    void send(String contentType, ByteBufferList bb);
    void send(String contentType, String string);
    void send(String string);
    void send(JSONObject json);
    void send(JSONArray jsonArray);
    void sendFile(File file);
    void sendStream(InputStream inputStream, long totalLength);
    AsyncHttpServerResponse code(int code);
    int code();
    Headers getHeaders();
    void writeHead();
    void setContentType(String contentType);
    void redirect(String location);
    AsyncHttpServerRequest getRequest();
    String getHttpVersion();
    void setHttpVersion(String httpVersion);

    // NOT FINAL
    void proxy(AsyncHttpResponse response);

    /**
     * Alias for end. Used with CompletedEmitters
     */
    void onCompleted(Exception ex);
    AsyncSocket getSocket();
    void setSocket(AsyncSocket socket);
}
