package com.bimmersoft.promoprinting.restserver;

import java.security.cert.X509Certificate;

import javax.net.ssl.SSLEngine;

public interface AsyncSSLSocket extends AsyncSocket {
    X509Certificate[] getPeerCertificates();
    SSLEngine getSSLEngine();
}
