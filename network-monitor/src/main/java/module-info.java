module ariia.network.monitor {
    requires lombok;
    requires network.speed;
    requires network.connectivity;
    requires lawnha;
    requires ariia.utils;
    requires ariia.models;
    exports org.ariia.monitors;
}