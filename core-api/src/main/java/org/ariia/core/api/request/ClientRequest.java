package org.ariia.core.api.request;

import org.ariia.items.Item;
import org.ariia.logging.Log;

import java.io.IOException;
import java.net.Proxy;
import java.util.*;


public interface ClientRequest {

    default Response head(String url) throws IOException {
        return executeAndDebugRequest("HEAD", url, Collections.emptyMap());
    }

    default Response head(Item item) throws IOException {
        return executeAndDebugRequest("HEAD", item.getUrl(), item.getHeaders());
    }

    default Response head(String url, Map<String, List<String>> headers) throws IOException {
        return executeAndDebugRequest("HEAD", url, headers);
    }

    default Response get(String url) throws IOException {
        return executeAndDebugRequest("GET", url, Collections.emptyMap());
    }

    default Response get(String url, Map<String, List<String>> headers) throws IOException {
        return executeAndDebugRequest("GET", url, headers);
    }

    default Response get(Item item) throws IOException {
        return executeAndDebugRequest("GET",
                item.isRedirected() ? item.getRedirectUrl() : item.getUrl(), item.getHeaders());
    }

    default Response get(Item item, int index) throws IOException {
        long[] range = item.getRangeInfo().indexOf(index);
        return get(item.isRedirected() ? item.getRedirectUrl() : item.getUrl(),
                range[0], range[1], item.getHeaders());
    }

    default Response get(String url, Long startRange, Long endRange, Map<String, List<String>> headers) throws IOException {

        Map<String, List<String>> headersCopy = new HashMap<>(headers);
        if (Objects.nonNull(startRange) && startRange > -1) {
            if (Objects.isNull(endRange) || endRange == -1) {
                headersCopy.put("Range", Arrays.asList("bytes=" + startRange + "-"));
            } else {
                headersCopy.put("Range", Arrays.asList("bytes=" + startRange + "-" + endRange));
            }
        }
        return executeAndDebugRequest("GET", url, headersCopy);
    }

    default Response executeAndDebugRequest(String method, String url, Map<String, List<String>> headers) throws IOException {
        var response = executeRequest(method, url, headers);
        debugResponse(response, headers);
        return response;
    }

    default void debugResponse(Response response, Map<String, List<String>> requestHeaders) {
        var builder = new StringBuilder();
        builder.append("request send:\n");
        builder.append('\n');
        builder.append(response.requestMethod());
        builder.append(' ');
        builder.append(response.requestUrl());
        builder.append(' ');
        builder.append(response.protocol());
        builder.append('\n');
        requestHeaders.forEach((headerName, valueList) -> {
            valueList.forEach(value -> {
                builder.append(headerName);
                builder.append(": ");
                builder.append(value);
                builder.append('\n');
            });
        });

        builder.append("response begin:\n");
        builder.append('\n');
        builder.append(response.protocol());
        builder.append(' ');
        builder.append(response.code());
        builder.append(' ');
        builder.append(response.responseMessage());
        builder.append('\n');
        response.headers().forEach((headerName, valueList) -> {
            valueList.forEach(value -> {
                builder.append(headerName);
                builder.append(": ");
                builder.append(value);
                builder.append('\n');
            });
        });
//    	builder.append(response.headers().toString());
        Log.debug(getClass(), "Client Request / Server Response", builder.toString());
    }


    Response executeRequest(String method, String url, Map<String, List<String>> headers) throws IOException;

    Proxy proxy();
}
