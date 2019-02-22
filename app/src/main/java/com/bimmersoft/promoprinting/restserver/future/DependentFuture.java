package com.bimmersoft.promoprinting.restserver.future;

public interface DependentFuture<T> extends Future<T>, DependentCancellable {
}
