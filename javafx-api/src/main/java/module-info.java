module ariia.javafx.api {
    requires restful.api;
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;
    requires ariia.cli.api;
    requires ariia.web.server;

    exports org.ariia.javafx;
}