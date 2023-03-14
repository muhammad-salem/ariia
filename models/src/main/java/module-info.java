module ariia.models {
    requires lombok;
    requires ariia.utils;
    requires java.xml;
    exports org.ariia.config;
    exports org.ariia.items;
    exports org.ariia.range;
    exports org.ariia.segment;

    opens org.ariia.items to com.google.gson;
    opens org.ariia.config to com.google.gson;
    opens org.ariia.range to com.google.gson;
    opens org.ariia.segment to com.google.gson;
}
