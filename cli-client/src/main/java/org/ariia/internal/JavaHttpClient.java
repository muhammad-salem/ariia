package org.ariia.internal;

import org.ariia.core.api.request.ClientRequest;
import org.ariia.core.api.request.Response;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;

import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class JavaHttpClient implements ClientRequest {

    private final Proxy proxy;
    private final HttpClient client;

    public JavaHttpClient(Proxy proxy, String username, char[] password, boolean trustAll) throws NoSuchAlgorithmException, KeyManagementException {
        this.proxy = proxy;
        var builder = HttpClient.newBuilder()
                .followRedirects(Redirect.ALWAYS)
                .connectTimeout(Duration.ofSeconds(20))
                .proxy(new JavaProxySelector(proxy))
//			        .proxy(ProxySelector.of( (InetSocketAddress) proxy.address() ))
//			        .authenticator(Authenticator.getDefault())
//			        .executor(Executors.newCachedThreadPool())
                ;
        if (!Proxy.NO_PROXY.equals(proxy) && proxy != null && username != null && password != null) {
            var authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    if (RequestorType.PROXY.equals(this.getRequestorType())) {
                        return new PasswordAuthentication(username, password);
                    }
                    return null;
                }
            };
            builder.authenticator(authenticator);
            System.setProperty("jdk.http.auth.proxying.disabledSchemes", "");
            System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
        }
        if (trustAll) {
            var trustManagers = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return null; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                    }
            };
            var sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, null);
            builder.sslContext(sslContext);
        }
        this.client = builder.build();
    }

    @Override
    public Response executeRequest(String method, String url, Map<String, List<String>> headers) throws IOException {
        try {
            var requestBuilder = HttpRequest.newBuilder(new URI(url));
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

            var response = client.send(requestBuilder.build(), BodyHandlers.ofInputStream());

            return new Response.Builder()
                    .code(response.statusCode())
                    .requestUrl(response.uri().toString())
                    .requestMethod(response.request().method())
                    .protocol(response.version().toString())
                    .responseMessage(Code.msg(response.statusCode()))
                    .headers(response.headers().map())
                    .setBodyBytes(response.body())
                    .build();

        } catch (InterruptedException | URISyntaxException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public Proxy proxy() {
        return proxy;
    }

}
