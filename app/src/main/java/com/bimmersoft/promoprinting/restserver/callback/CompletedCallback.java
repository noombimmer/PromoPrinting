package com.bimmersoft.promoprinting.restserver.callback;

public interface CompletedCallback {
    class NullCompletedCallback implements CompletedCallback {
        @Override
        public void onCompleted(Exception ex) {

        }
    }

    void onCompleted(Exception ex);
}
