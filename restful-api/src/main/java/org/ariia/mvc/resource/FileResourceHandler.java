package org.ariia.mvc.resource;

import org.ariia.mvc.router.Routes;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class FileResourceHandler extends RouterResourceHandler {

    private String resourceLocation;

    public FileResourceHandler() {
        this("/index.html", new Routes("/"));
    }

    public FileResourceHandler(String resourceLocation, Routes routes) {
        this(resourceLocation, "/index.html", routes);
    }

    public FileResourceHandler(String resourceLocation, String indexFile, Routes routes) {
        super(indexFile, routes);
        this.resourceLocation = Objects.requireNonNull(resourceLocation);
    }

    public String getResourceLocation() {
        return resourceLocation;
    }

    @Override
    protected InputStream getResourceAsStream(String filename) throws IOException {
        return new FileInputStream(resourceLocation + filename);
    }

}