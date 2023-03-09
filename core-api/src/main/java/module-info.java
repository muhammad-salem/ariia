module ariia.core.api {
    requires lombok;
    requires network.speed;
    requires network.connectivity;
    requires ariia.logger;
    requires ariia.models;
    requires ariia.network.monitor;
    requires ariia.utils;
    exports org.ariia.core.api.client;
    exports org.ariia.core.api.queue;
    exports org.ariia.core.api.request;
    exports org.ariia.core.api.response;
    exports org.ariia.core.api.service;
    exports org.ariia.core.api.writer;
    exports org.ariia.proxy;
}
