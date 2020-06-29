package org.ariia.mvc.resource;

import org.ariia.mvc.router.Routes;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class StreamResourceHandler extends RouterResourceHandler {

    private String resourceLocation;

    public StreamResourceHandler() {
        this("/static", "/index.html", new Routes("/"));
    }

    public StreamResourceHandler(String resourceLocation, Routes routes) {
        this(resourceLocation, "/index.html", routes);
    }

    public StreamResourceHandler(String resourceLocation, String indexFile, Routes routes) {
        super(indexFile, routes);
        this.resourceLocation = Objects.requireNonNull(resourceLocation);
    }

    public String getResourceLocation() {
        return resourceLocation;
    }

    @Override
    protected InputStream getResourceAsStream(String filename) throws IOException {
        InputStream stream = getClass().getResourceAsStream(resourceLocation + filename);
        if (Objects.isNull(stream)) {
            throw new IOException("no resource with this name is found");
        }
        return stream;
    }


}