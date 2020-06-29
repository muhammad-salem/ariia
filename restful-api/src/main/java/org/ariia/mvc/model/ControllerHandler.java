package org.ariia.mvc.model;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.ariia.mvc.processing.MethodIndex;
import org.ariia.mvc.processing.ProxySwitcher;
import org.ariia.mvc.resource.MimeType;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class ControllerHandler implements HttpHandler {

    private static Gson gson = new Gson();

    private Object controller;
    private ProxySwitcher switcher;
    private List<MethodIndex> methodIndexs;

    public ControllerHandler(Object controller, ProxySwitcher switcher) {
        this.controller = controller;
        this.switcher = switcher;
        this.methodIndexs = switcher.getMethodIndexs();
    }

    private static Object[] getParamters(HttpExchange exchange, MethodIndex methodIndex, RequestInfo requestInfo) {
        Object[] parameters = new Object[methodIndex.getParametersInfo().size()];
        for (int i = 0; i < parameters.length; i++) {
            ParameterInfo info = methodIndex.getParametersInfo().get(i);
            if (info.isRequestBody()) {
                parameters[i] = toObject(
                        info.getParameterType(),
                        requestInfo.getBody());
            } else if (info.isPathVariable()) {
                parameters[i] = toObject(
                        info.getParameterType(),
                        requestInfo.getPathVariable(info.name()));
            } else if (info.isRequestParam()) {
                parameters[i] = toObject(
                        info.getParameterType(),
                        requestInfo.getParamter(info.name()));
            } else if (info.isHeaderValue()) {
                parameters[i] = toObject(
                        info.getParameterType(),
                        exchange.getRequestHeaders().getFirst(info.name()));
            } else if (info.getParameterType().equals(HttpExchange.class)) {
                parameters[i] = exchange;
            } else if (info.getParameterType().equals(OutputStream.class)) {
                parameters[i] = exchange.getResponseBody();
            } else if (info.getParameterType().equals(InputStream.class)) {
                parameters[i] = exchange.getRequestBody();
            }
        }
        return parameters;
    }

    private static Object toObject(Class<?> clazz, String value) {
        if (String.class == clazz) return value;
        if (Boolean.class == clazz || Boolean.TYPE == clazz) return Boolean.parseBoolean(value);
        if (Byte.class == clazz || Byte.TYPE == clazz) return Byte.parseByte(value);
        if (Short.class == clazz || Short.TYPE == clazz) return Short.parseShort(value);
        if (Integer.class == clazz || Integer.TYPE == clazz) return Integer.parseInt(value);
        if (Long.class == clazz || Long.TYPE == clazz) return Long.parseLong(value);
        if (Float.class == clazz || Float.TYPE == clazz) return Float.parseFloat(value);
        if (Double.class == clazz || Double.TYPE == clazz) return Double.parseDouble(value);
        return gson.fromJson(value, clazz);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            URI uri = exchange.getRequestURI();
            RequestInfo requestInfo = new RequestInfo();
            if (exchange.getRequestBody().available() > 0) {
                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)
                        );
                requestInfo.setBody(reader.lines().collect(Collectors.joining("\n")));
            }
            String path = uri.getPath();
            requestInfo.setParamters(uri.getQuery());
            requestInfo.setMethodContext(path.split(switcher.getContext(), 2)[1]);
            if (switcher.getContextParamter().size() > 0) {
                for (String param : switcher.getContextParamter()) {
                    String methodContext = requestInfo.getMethodContext();
                    int i = methodContext.indexOf('/');
                    requestInfo.putPathVariable(param, methodContext.substring(0, i));
                    requestInfo.setMethodContext(methodContext.substring(i + 1));
                }
                requestInfo.setMethodContext('/' + requestInfo.getMethodContext());
            }

            Optional<MethodIndex> optional = filterMethods(
                    requestInfo,
                    exchange.getRequestMethod(),
                    path,
                    exchange.getRequestHeaders());
            if (!optional.isPresent()) {
                exchange.sendResponseHeaders(500, -1);
                return;
            }
            MethodIndex methodIndex = optional.get();

            if (!methodIndex.context().equals(requestInfo.getMethodContext())) {
                String mContext = requestInfo.getMethodContext().split(methodIndex.context())[1];
                for (String pathVariable : methodIndex.pathVariables()) {
                    String methodContext = mContext;
                    int i = methodContext.indexOf('/');
                    if (i == -1) {
                        requestInfo.putPathVariable(pathVariable, methodContext);
                        break;
                    }
                    requestInfo.putPathVariable(pathVariable, methodContext.substring(0, i));
                    mContext = methodContext.substring(i + 1);
                }
            }

            try {
                executeMathod(exchange, methodIndex, requestInfo);
            } catch (Exception e) {
                e.printStackTrace();
                throw new IOException(e);
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }


    /// TO:DO handle body >> cast & JSON 
    /// TO:DO handle path arguments, path variable, query parameter
    /// TO:DO handle return type

    boolean printMethodIndex(MethodIndex method) {
        System.out.println(method);
        return true;
    }

    Optional<MethodIndex> filterMethods(RequestInfo requestInfo, String method, String path, Headers requestHeaders) {
        List<MethodIndex> base = methodIndexs.stream()
                .filter(m -> method.equalsIgnoreCase(m.httpMethod()))
                .filter(m -> requestInfo.getMethodContext().startsWith(m.context()))
                .filter(m -> requestInfo.hasBody() == m.hasBodyParameter())
                .filter(m -> path.matches(m.getRegexPattern()))
//			.filter(this::printMethodIndex)
                .collect(Collectors.toList());

        List<MethodIndex> headerFilter = base.stream()
                .filter(m -> {
                    boolean hasAll = m.headers().isEmpty() ? false : true;
                    for (String header : m.headers()) {
                        hasAll &= requestHeaders.containsKey(header);
                    }
                    return hasAll;
                })
//			.filter(this::printMethodIndex)
                .limit(1l)
                .collect(Collectors.toList());

        if (!headerFilter.isEmpty()) {
            return headerFilter.stream().findFirst();
        } else {
            return base.stream().limit(1l).findFirst();
        }
    }

    private void executeMathod(HttpExchange exchange, MethodIndex methodIndex, RequestInfo requestInfo)
            throws IllegalAccessException, InvocationTargetException, IOException {

        int paramterLength = methodIndex.getParametersInfo().size();
        Object returnObject = null;
        if (paramterLength == 0) {
            returnObject = methodIndex.method().invoke(controller);
        } else {
            Object[] parameters = getParamters(exchange, methodIndex, requestInfo);
            returnObject = methodIndex.method().invoke(controller, parameters);
        }

        // check if exchange is closed, that mean the the invoker
        // get the exchange as a paramter and handel it self,
        // the return type of the invoker method is void.
        // method should had Annotation DoExchange


        // check void
        if (Objects.isNull(returnObject)) {
            if (methodIndex.canDoExchange()) {
                return;
            } else {
                exchange.sendResponseHeaders(200, -1);
                exchange.close();
            }
        } else {
            String responseBody = gson.toJson(returnObject);
            byte[] bodyBytes = responseBody.getBytes(StandardCharsets.UTF_8);
            if (responseBody.startsWith("{") || responseBody.startsWith("[")) {
                exchange.getResponseHeaders().add("Content-Type", MimeType.mime("json"));
            }

            exchange.sendResponseHeaders(200, bodyBytes.length);
            exchange.getResponseBody().write(bodyBytes);
            exchange.close();
        }
    }

}
