package org.ariia.okhttp;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OkCookieJar implements CookieJar {

    public static CookieJar CookieJarMap = new OkCookieJar();

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
