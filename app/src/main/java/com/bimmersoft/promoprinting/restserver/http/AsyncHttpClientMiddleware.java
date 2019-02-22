package com.bimmersoft.promoprinting.restserver.http;

import com.bimmersoft.promoprinting.restserver.AsyncSocket;
import com.bimmersoft.promoprinting.restserver.DataEmitter;
import com.bimmersoft.promoprinting.restserver.DataSink;
import com.bimmersoft.promoprinting.restserver.callback.CompletedCallback;
import com.bimmersoft.promoprinting.restserver.callback.ConnectCallback;
import com.bimmersoft.promoprinting.restserver.future.Cancellable;
import com.bimmersoft.promoprinting.restserver.util.UntypedHashtable;

/**
 * AsyncHttpClientMiddleware is used by AsyncHttpClient to
 * inspect, manipulate, and handle http requests.
 */
public interface AsyncHttpClientMiddleware {
    interface ResponseHead  {
        AsyncSocket socket();
        String protocol();
        String message();
        int code();
        ResponseHead protocol(String protocol);
        ResponseHead message(String message);
        ResponseHead code(int code);
        Headers headers();
        ResponseHead headers(Headers headers);
        DataSink sink();
        ResponseHead sink(DataSink sink);
        DataEmitter emitter();
        ResponseHead emitter(DataEmitter emitter);
    }

    class OnRequestData {
        public UntypedHashtable state = new UntypedHashtable();
        public AsyncHttpRequest request;
    }

    class GetSocketData extends OnRequestData {
        public ConnectCallback connectCallback;
        public Cancellable socketCancellable;
        public String protocol;
    }

    class OnExchangeHeaderData extends GetSocketData {
        public AsyncSocket socket;
        public ResponseHead response;
        public CompletedCallback sendHeadersCallback;
        public CompletedCallback receiveHeadersCallback;
    }

    class OnRequestSentData extends OnExchangeHeaderData {
    }

    class OnHeadersReceivedDataOnRequestSentData extends OnRequestSentData {
    }

    class OnBodyDataOnRequestSentData extends OnHeadersReceivedDataOnRequestSentData {
        public DataEmitter bodyEmitter;
    }

    class OnResponseCompleteDataOnRequestSentData extends OnBodyDataOnRequestSentData {
        public Exception exception;
    }

    /**
     * Called immediately upon request execution
     * @param data
     */
    void onRequest(OnRequestData data);

    /**
     * Called to retrieve the socket that will fulfill this request
     * @param data
     * @return
     */
    Cancellable getSocket(GetSocketData data);

    /**
     * Called before when the headers are sent and received via the socket.
     * Implementers return true to denote they will manage header exchange.
     * @param data
     * @return
     */
    boolean exchangeHeaders(OnExchangeHeaderData data);

    /**
     * Called once the headers and any optional request body has
     * been sent
     * @param data
     */
    void onRequestSent(OnRequestSentData data);

    /**
     * Called once the headers have been received via the socket
     * @param data
     */
    void onHeadersReceived(OnHeadersReceivedDataOnRequestSentData data);

    /**
     * Called before the body is decoded
     * @param data
     */
    void onBodyDecoder(OnBodyDataOnRequestSentData data);

    /**
     * Called once the request is complete and response has been received,
     * or if an error occurred
     * @param data
     */
    void onResponseComplete(OnResponseCompleteDataOnRequestSentData data);
}
