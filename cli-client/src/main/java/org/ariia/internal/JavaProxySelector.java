package org.ariia.internal;

import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.Objects;

public class JavaProxySelector extends ProxySelector {
    private static final List<Proxy> NO_PROXY_LIST = List.of(Proxy.NO_PROXY);
    final List<Proxy> list;

    JavaProxySelector(InetSocketAddress address) {
        Proxy p;
        if (address == null) {
            p = Proxy.NO_PROXY;
        } else {
            p = new Proxy(Proxy.Type.HTTP, address);
        }
        list = List.of(p);
    }

    JavaProxySelector(Proxy proxy) {
        Objects.requireNonNull(proxy);
        list = List.of(proxy);
    }

    @Override
    public void connectFailed(URI uri, SocketAddress sa, IOException e) {
        /* ignore */
    }

    @Override
    public synchronized List<Proxy> select(URI uri) {
        var scheme = uri.getScheme().toLowerCase();
        if (scheme.equals("http") || scheme.equals("https")) {
            return list;
        } else {
            return NO_PROXY_LIST;
        }
    }
}