package org.aria.core;

import java.net.InetSocketAddress;
import java.net.Proxy;

import okhttp3.CookieJar;

public class OkConfig {

	public static OkConfig LessDirect = new OkConfig();

	CookieJar cookieJar;
	Proxy proxy;
	// Authenticator proxyAuthenticator;

	public OkConfig() {
		this.cookieJar = CookieJars.NO_COOKIES;
		this.proxy = Proxy.NO_PROXY;
	}

	public OkConfig(CookieJar cookieJar, Proxy proxy) {
		this.cookieJar = cookieJar;
		this.proxy = proxy;
	}

	public OkConfig(CookieJar cookieJar, Proxy.Type type, String hostname, int port) {
		this.cookieJar = cookieJar;
		if (hostname == null)
			this.proxy = Proxy.NO_PROXY;
		else
			this.proxy = new Proxy(type, new InetSocketAddress(hostname, port));
	}

	public CookieJar cookieJar() {
		return cookieJar;
	}

	public Proxy proxy() {
		return proxy;
	}

	public void updaeProxy(Proxy proxy) {
		this.proxy = proxy;
	}

	public void updateCookieJar(CookieJar cookieJar) {
		this.cookieJar = cookieJar;
	}

}
