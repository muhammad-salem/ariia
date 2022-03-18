package org.ariia.core.api.client;

import org.ariia.config.Properties;
import org.ariia.core.api.queue.ItemDownloader;
import org.ariia.core.api.request.ClientRequest;
import org.ariia.core.api.request.Response;
import org.ariia.core.api.response.ContentLength;
import org.ariia.core.api.response.Downloader;
import org.ariia.core.api.writer.ItemMetaData;
import org.ariia.items.Item;
import org.ariia.items.ItemState;
import org.ariia.logging.Log;
import org.ariia.range.RangeInfo;
import org.network.speed.report.SpeedMonitor;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Client implements Downloader, ItemDownloader, ContentLength {

    // protected int retries;
    protected Properties properties;
    protected ClientRequest clientRequest;
    protected ExecutorService executor;

    public Client(Properties properties, ClientRequest clientRequest, ExecutorService executor) {
        super();
        this.properties = properties;
        this.clientRequest = clientRequest;
        this.executor = executor;
    }

    public Proxy getProxy() {
        return clientRequest.proxy();
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public ClientRequest getClientRequest() {
        return clientRequest;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public void shutdownServiceNow() {
        executor.shutdownNow();
    }

    @Override
    public Future<?> downloadPart(ItemMetaData metaData, int index, SpeedMonitor... monitors) {
        if (properties.getRetries() == 0) {
            return executor.submit(() -> {
                boolean finised = false;
                while (!finised && metaData.isDownloading()) {
                    finised = downloadTask(metaData, index, monitors);
                    if (!finised && monitors[0].getTcpDownloadSpeed() == 0) {
                        break;
                    }
                }
            });
        } else {
            return executor.submit(() -> {
                boolean finised = false;
                for (int i = 0; (i < properties.getRetries() && !finised && metaData.isDownloading()); i++) {
                    finised = downloadTask(metaData, index, monitors);
                    if (!finised && monitors[0].getTcpDownloadSpeed() == 0) {
                        Thread.currentThread().interrupt();
                        /*
                         * no need for 'break' her, the interrupt handel it
                         */
                        break;
                    }
                }
            });
        }
    }

    public final void updateItemOnline(Item item) {

        item.setState(ItemState.INIT_HTTP);
        for (int i = 0; i < 5; i++) {
            try {
                updateItemOnline(item, false);
                break;
            } catch (IOException e) {
                try {
                    updateItemOnline(item, true);
                    break;
                } catch (IOException e1) {
                }
            }
        }
    }

    private void updateItemOnline(Item item, boolean headOrGet) throws IOException {

        try (Response response = headOrGet ? clientRequest.head(item) : clientRequest.get(item)) {
            if (response.code() == 404) {
                item.setFilename("404_Not_Found");
                return;
            }
            if (!response.requestUrl().equals(item.getUrl())) {
                Log.trace(getClass(), "redirect item to another location", "base url:\t" + item.getUrl() + "\nredirect url: \t" + response.requestUrl());

                String decodedUrl;
                try {
                    decodedUrl = URLDecoder.decode(response.requestUrl(), StandardCharsets.UTF_8.name());
                } catch (Exception e) {
                    decodedUrl = response.requestUrl();
                }
                String fileName = new File(decodedUrl).getName().split("\\?")[0];
                if ("".equals(fileName)) {
                    String[] fileParts = decodedUrl.split("/");
                    fileName = fileParts[fileParts.length - 2].split("\\?")[0];
                }
                item.setFilename(fileName);
            }
            OptionalLong contentLength = response.firstValueAsLong("Content-Length");
            if (contentLength.isPresent()) {
                Optional<String> optinalAcceptRanges = response.firstValue("Accept-Ranges");
                String acceptRanges = optinalAcceptRanges.orElseGet(() -> "none");
                if ("bytes".equalsIgnoreCase(acceptRanges)) {
                    item.setRangeInfo(RangeInfo.recommendedRange(contentLength.getAsLong()));
                } else {
                    item.setRangeInfo(new RangeInfo(contentLength.getAsLong()));
                }
            }

            Optional<String> contentDisposition = response.firstValue("Content-disposition");

            if (contentDisposition.isPresent() && contentDisposition.get().contains("filename")) {
                Map<String, String> filenames = Stream.of(contentDisposition.get().split("; "))
                        .filter(line -> line.contains("="))
                        .map(parameter -> parameter.split("="))
                        .collect(Collectors.toMap(strings -> strings[0], strings -> strings[1]));
                String encode = "UTF-8";
                String name = null;
                if (filenames.containsKey("filename*")) {
                    String value = filenames.get("filename*");
                    String[] temp = value.split("''", 2);
                    if (temp.length == 2) {
                        encode = temp[0];
                        name = temp[1];
                    } else {
                        name = value;
                    }
                } else if (filenames.containsKey("filename")) {
                    name = filenames.get("filename");
                    if (name.startsWith("\"")) {
                        name = name.substring(1, name.length() - 1);
                    }
                }
                if (Objects.nonNull(name)) {
                    item.setFilename(name, encode);
                }
            }
        } catch (IOException e) {
            Log.warn(getClass(), e.getClass().getSimpleName(), e.getMessage());
            throw e;
        }

    }

}
