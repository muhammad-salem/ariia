package org.ariia.internal;

import org.ariia.core.api.request.ClientRequest;
import org.ariia.core.api.request.Response;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class JavaHttpClient implements ClientRequest {

    private Proxy proxy;
    private HttpClient client;
    private boolean trustAll;

    public JavaHttpClient(Proxy proxy, boolean trustAll) throws NoSuchAlgorithmException, KeyManagementException {
        this.proxy = proxy;
        this.trustAll = trustAll;
        init();
    }

    private void init() throws NoSuchAlgorithmException, KeyManagementException {
        HttpClient.Builder builder = HttpClient.newBuilder()
                .followRedirects(Redirect.ALWAYS)
                .connectTimeout(Duration.ofSeconds(20))
                .proxy(new JavaProxySelector(proxy))
//			        .proxy(ProxySelector.of( (InetSocketAddress) proxy.address() ))
//			        .authenticator(Authenticator.getDefault())
//			        .executor(Executors.newCachedThreadPool())
                ;
        if (trustAll) {
            TrustManager[] trustManagers = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return null; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                    }
            };
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, null);
            builder.sslContext(sslContext);
        }
        this.client = builder.build();
    }


    @Override
    public Response executeRequest(String method, String url, Map<String, List<String>> headers) throws IOException {
        try {

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(new URI(url));
            if ("GET".equalsIgnoreCase(method)) {
                requestBuilder.GET();
            } else if ("HEAD".equalsIgnoreCase(method)) {
                requestBuilder.method("HEAD", HttpRequest.BodyPublishers.noBody());
            }

            if (Objects.nonNull(headers) && !headers.isEmpty()) {
                headers.forEach((headerName, valueList) -> {
                    valueList.forEach(headerValue -> {
                        requestBuilder.header(headerName, headerValue);
                    });
                });
            }

            HttpResponse<InputStream> response = client.send(requestBuilder.build(), BodyHandlers.ofInputStream());

            Response.Builder responseBuilder = new Response.Builder();
            responseBuilder.code(response.statusCode());
            responseBuilder.requestUrl(response.uri().toString());
            responseBuilder.requestMethod(response.request().method());
            responseBuilder.protocol(response.version().toString());
            responseBuilder.responseMessage(Code.msg(response.statusCode()));
            responseBuilder.headers(response.headers().map());
            responseBuilder.setBodyBytes(response.body());
            return responseBuilder.build();

        } catch (InterruptedException | URISyntaxException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public Proxy proxy() {
        return proxy;
    }

}
