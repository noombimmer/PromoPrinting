package com.bimmersoft.promoprinting.restserver.callback;

import com.bimmersoft.promoprinting.restserver.future.Continuation;

public interface ContinuationCallback {
    void onContinue(Continuation continuation, CompletedCallback next) throws Exception;
}
