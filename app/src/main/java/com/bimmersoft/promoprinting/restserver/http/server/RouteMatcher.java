package com.bimmersoft.promoprinting.restserver.http.server;

public interface RouteMatcher {
    AsyncHttpServerRouter.RouteMatch route(String method, String path);
}