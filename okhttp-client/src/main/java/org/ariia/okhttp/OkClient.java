package org.ariia.okhttp;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.ariia.core.api.request.ClientRequest;
import org.ariia.core.api.request.Response;

import java.io.IOException;
import java.net.Proxy;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class OkClient implements ClientRequest {

    private OkHttpClient httpClient;
    private boolean trustAll;

    public OkClient(Proxy proxy, boolean trustAll) throws NoSuchAlgorithmException, KeyManagementException {
        this.trustAll = trustAll;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cookieJar(OkCookieJar.CookieJarMap)
                .proxy(proxy)
                .retryOnConnectionFailure(false);
        if (trustAll){
            TrustManager[] trustManagers = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                    }
            };
            X509TrustManager trustManager = (X509TrustManager) trustManagers[0];
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, null);
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            builder.sslSocketFactory(sslSocketFactory, trustManager);
        }
        this.httpClient = builder.build();
    }

    public OkClient(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public Response executeRequest(String method, String url, Map<String, List<String>> headers) throws IOException {

        Request.Builder builder = new Request.Builder();
        if (Objects.nonNull(headers) && !headers.isEmpty()) {
            headers.forEach((headerName, valueList) -> {
                valueList.forEach(headerValue -> {
                    builder.addHeader(headerName, headerValue);
                });
            });
        }

        builder.method(method, null)
                .url(url);

        Call call = httpClient.newCall(builder.build());
        okhttp3.Response response = call.execute();

        Response.Builder responseBuilder = new Response.Builder();
        responseBuilder.code(response.code());
        responseBuilder.requestUrl(response.networkResponse().request().url().toString());
        responseBuilder.requestMethod(response.request().method());
        responseBuilder.protocol(response.protocol().toString());
        responseBuilder.responseMessage(response.message());
        responseBuilder.headers(response.headers().toMultimap());
        responseBuilder.setBodyBytes(response.body().byteStream());
        return responseBuilder.build();
    }

    @Override
    public java.net.Proxy proxy() {
        return httpClient.proxy();
    }

}
