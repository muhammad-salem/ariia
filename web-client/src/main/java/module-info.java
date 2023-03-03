module ariia.web.client {
    requires restful.api;
    requires ariia.cli.api;
    requires ariia.web.server;
    requires ariia.cli.client;
    requires ariia.utils;
    exports org.ariia.web.server;
}
