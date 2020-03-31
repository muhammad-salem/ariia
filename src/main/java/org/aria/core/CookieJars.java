package org.aria.core;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class CookieJars implements CookieJar {

	public static CookieJars CookieJarMap = new CookieJars();

	protected Map<String, List<Cookie>> cookieJar = new LinkedHashMap<>();

	@Override
	public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
		cookieJar.put(url.toString(), cookies);
	}

	@Override
	public List<Cookie> loadForRequest(HttpUrl url) {
		List<Cookie> cookies = cookieJar.get(url.toString());
		if (cookies == null)
			cookies = Collections.emptyList();
		return cookies;
	}

	public void remove(HttpUrl url) {
		cookieJar.remove(url.toString());
	}

}
