module ariia.web.server {
    requires jdk.httpserver;
    requires lawnha;
    requires ariia.logger;
    requires restful.api;
    requires ariia.cli.api;
    requires ariia.utils;
    requires ariia.core.api;
    requires ariia.models;
    requires ariia.network.monitor;
    exports org.ariia.web;
    exports org.ariia.web.app;
    exports org.ariia.web.app.model;
    exports org.ariia.web.controller;
    exports org.ariia.web.services;
}
