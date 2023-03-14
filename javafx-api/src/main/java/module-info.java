module ariia.javafx.api {
    requires lombok;
    requires lawnha;
    requires restful.api;
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;
    requires ariia.cli.api;
    requires ariia.web.server;
    requires ariia.utils;
    requires ariia.models;
    requires ariia.core.api;
    requires ariia.cli.client;
    requires ariia.network.monitor;
    requires ariia.logger;

    exports org.ariia.javafx;
    exports org.ariia.javafx.controllers;
    opens org.ariia.javafx.controllers to javafx.fxml;
}