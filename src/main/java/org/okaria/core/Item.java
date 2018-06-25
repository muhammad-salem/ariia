package org.okaria.core;

import java.net.Proxy;

public interface Item {

	void setUrl(String url);

	void setFileName(String name);

	void resolveConfiectFileName();

	void SetFileDirectory(String dir);

	void setCacheDir(String cachedir);

	void setFileLength(long length);

	void addCookie(String cookie);

	void addCookies(String... cookies);

	void setCookies(String cookies);

	void setCookies(String... cookies);

	void clearCookies();

	void setReferer(String referer);

	void setIfModifiedSince(long date);

	void setUserAgernt();

	void setProxyType(Proxy.Type type);

	void setProxy(String host, int port);

	void setProxy(String host, int port, Proxy.Type type);

	void setProxy(String host, int port, String user, char[] pass);

	void setProxy(String host, int port, String user, char[] pass, Proxy.Type type);

	// void proxyChangeLisinter();

}
