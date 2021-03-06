package com.bimmersoft.promoprinting.restserver.http.body;

import com.bimmersoft.promoprinting.restserver.ByteBufferList;
import com.bimmersoft.promoprinting.restserver.DataEmitter;
import com.bimmersoft.promoprinting.restserver.DataSink;
import com.bimmersoft.promoprinting.restserver.Util;
import com.bimmersoft.promoprinting.restserver.callback.CompletedCallback;
import com.bimmersoft.promoprinting.restserver.callback.DataCallback;
import com.bimmersoft.promoprinting.restserver.http.AsyncHttpRequest;
import com.bimmersoft.promoprinting.restserver.http.Multimap;
import com.bimmersoft.promoprinting.restserver.http.NameValuePair;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class UrlEncodedFormBody implements AsyncHttpRequestBody<Multimap> {
    private Multimap mParameters;
    private byte[] mBodyBytes;

    public UrlEncodedFormBody(Multimap parameters) {
        mParameters = parameters;
    }

    public UrlEncodedFormBody(List<NameValuePair> parameters) {
        mParameters = new Multimap(parameters);
    }

    private void buildData() {
        boolean first = true;
        StringBuilder b = new StringBuilder();
        try {
            for (NameValuePair pair: mParameters) {
                if (pair.getValue() == null)
                    continue;
                if (!first)
                    b.append('&');
                first = false;

                b.append(URLEncoder.encode(pair.getName(), "UTF-8"));
                b.append('=');
                b.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
            }
            mBodyBytes = b.toString().getBytes(StandardCharsets.UTF_8);
        }
        catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }
    
    @Override
    public void write(AsyncHttpRequest request, final DataSink response, final CompletedCallback completed) {
        if (mBodyBytes == null)
            buildData();
        Util.writeAll(response, mBodyBytes, completed);
    }

    public static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    @Override
    public String getContentType() {
        return CONTENT_TYPE + "; charset=utf-8";
    }

    @Override
    public void parse(DataEmitter emitter, final CompletedCallback completed) {
        final ByteBufferList data = new ByteBufferList();
        emitter.setDataCallback(new DataCallback() {
            @Override
            public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                bb.get(data);
            }
        });
        emitter.setEndCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                try {
                    if (ex != null)
                        throw ex;
                    mParameters = Multimap.parseUrlEncoded(data.readString());
                }
                catch (Exception e) {
                    completed.onCompleted(e);
                    return;
                }
                completed.onCompleted(null);
            }
        });
    }

    public UrlEncodedFormBody() {
    }

    @Override
    public boolean readFullyOnRequest() {
        return true;
    }

    @Override
    public int length() {
        if (mBodyBytes == null)
            buildData();
        return mBodyBytes.length;
    }

    @Override
    public Multimap get() {
        return mParameters;
    }
}
