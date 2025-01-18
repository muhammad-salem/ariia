package org.ariia.proxy;

import lombok.Data;

import java.net.InetSocketAddress;

@Data
public class Proxy {

    private Type type;
    private String host;
    private int port;
    private String user;
    private char[] password;

    public java.net.Proxy getProxy() {
        java.net.Proxy.Type netType = switch (this.type) {
            case HTTP -> java.net.Proxy.Type.HTTP;
            case SOCKS4, SOCKS5 -> java.net.Proxy.Type.SOCKS;
            default -> java.net.Proxy.Type.DIRECT;
        };
        var sa = new InetSocketAddress(host, port);
        return new java.net.Proxy(netType, sa);
    }

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
         * Represents a proxy for high level protocols such as HTTP or FTP.
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

}
