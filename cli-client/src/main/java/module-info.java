module ariia.cli.client {
    requires lombok;
    requires lawnha;
    requires ariia.cli.api;
    requires ariia.core.api;
    requires java.net.http;
    requires ariia.utils;
    requires ariia.models;
    exports org.ariia;
    exports org.ariia.internal;
}