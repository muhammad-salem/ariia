package org.ariia.config;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class Proxy {

	public enum Type {
		/**
		 * Obtain System Proxy Setting.
		 */
		SYSTEM,
		/**
		 * Represents a direct connection, or the absence of a proxy.
		 */
		DIRECT,
		/**
		 * Represents proxy for high level protocols such as HTTP or FTP.
		 */
		HTTP,
		/**
		 * Represents a SOCKS (V4) proxy.
		 */
		SOCKS4,

		/**
		 * Represents a SOCKS (V5) proxy.
		 */
		SOCKS5;
	}

	Type type;

	String host;
	int port;

	char[] user, pass;

	boolean hasAuthorization;

	public void setType(Type type) {
		this.type = type;
	}

	public void setProxy(String host, int port) {
		this.host = host;
		this.port = port;
		hasAuthorization = false;
	}

	public static Proxy getProxy(String host, int port, Type type) {
		Proxy proxy = new Proxy();
		switch (type) {
		case DIRECT:
			proxy.setType(Type.DIRECT);
			break;
		case HTTP:
			proxy.setType(Type.HTTP);
			break;
		case SOCKS4:
			proxy.setType(Type.SOCKS4);
			break;
		case SOCKS5:
			proxy.setType(Type.SOCKS5);
			break;
		case SYSTEM:
			proxy.setType(Type.SOCKS5);
			break;
		default:
			proxy.setType(Type.DIRECT);
		}
		proxy.host = host;
		proxy.port = port;
		return proxy;
	}

	public java.net.Proxy getProxy() {
		java.net.Proxy.Type netType;
		switch (this.type) {
		case DIRECT:
			netType = java.net.Proxy.Type.DIRECT;
			break;
		case HTTP:
			netType = java.net.Proxy.Type.HTTP;
			break;
		case SOCKS4:
			netType = java.net.Proxy.Type.SOCKS;
			break;
		case SOCKS5:
			netType = java.net.Proxy.Type.SOCKS;
			break;
		case SYSTEM:
			netType = java.net.Proxy.Type.DIRECT;
			break;
		default:
			netType = java.net.Proxy.Type.DIRECT;
		}
		SocketAddress sa = new InetSocketAddress(host, port);
		return new java.net.Proxy(netType, sa);
	}

	public void setAuthorization(char[] user, char[] pass) {
		this.user = user;
		this.pass = pass;
		hasAuthorization = true;
	}

	public boolean hasAuthorization() {
		return hasAuthorization;
	}

	public Type getType() {
		return type;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public char[] getUser() {
		return user;
	}

	public char[] getPass() {
		return pass;
	}

}
