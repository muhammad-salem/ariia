package org.ariia.mvc.resource;

import java.io.IOException;
import java.util.Properties;

//import java.util.HashMap;
//import java.util.Map;

public final class MimeType {

    private static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
    private static Properties properties;

    static {
        properties = new Properties();
        try {
            properties.load(MimeType.class.getResourceAsStream("/mime-types.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String mime(String type) {
        return properties.getProperty(type, APPLICATION_OCTET_STREAM);
    }

    public static String getType(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        return fileName.substring(lastIndexOf + 1);
    }

    public static String getMimeForFileName(String fileName) {
        return mime(getType(fileName));
    }

}
