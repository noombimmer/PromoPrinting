package com.bimmersoft.promoprinting.restserver.future;

public interface TypeConverter<T, F> {
    Future<T> convert(F from, String fromMime) throws Exception;
}
