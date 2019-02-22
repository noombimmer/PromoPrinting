package com.bimmersoft.promoprinting.restserver.future;

public interface DependentCancellable extends Cancellable {
    boolean setParent(Cancellable parent);
}
