package com.bimmersoft.promoprinting.restserver.future;

/**
 * Created by koush on 12/22/13.
 */
public interface FutureRunnable<T> {
    T run() throws Exception;
}
