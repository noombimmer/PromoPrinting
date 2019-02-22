package com.bimmersoft.promoprinting.restserver.callback;

import com.bimmersoft.promoprinting.restserver.future.Continuation;

public interface ContinuationCallback {
    public void onContinue(Continuation continuation, CompletedCallback next) throws Exception;
}
