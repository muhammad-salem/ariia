package org.ariia.mvc.processing;

import org.ariia.mvc.annotation.*;
import org.ariia.mvc.annotation.method.*;
import org.ariia.mvc.model.ParameterInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProxySwitcher {

    private static List<Class<? extends Annotation>> requestAnnotation = new ArrayList<>();
    private static List<Class<? extends Annotation>> parameterAnnotation = new ArrayList<>();

    static {
        requestAnnotation.add(GetRequest.class);
        requestAnnotation.add(HeadRequest.class);
        requestAnnotation.add(PostRequest.class);
        requestAnnotation.add(PutRequest.class);
        requestAnnotation.add(DeleteRequest.class);
        requestAnnotation.add(OptionsRequest.class);
        requestAnnotation.add(TraceRequest.class);
        requestAnnotation.add(PatchRequest.class);

        parameterAnnotation.add(PathVariable.class);
        parameterAnnotation.add(RequestParam.class);
        parameterAnnotation.add(RequestBody.class);
        parameterAnnotation.add(HeaderValue.class);
    }


    private Object controller;
    private List<String> contextParamter;
    private String context;
    private String controllerContext;
    private List<MethodIndex> methodIndexs;

    public ProxySwitcher(Object controller) {
        this.controller = controller;
        this.methodIndexs = new ArrayList<>();
        initController();
    }

    public List<MethodIndex> getMethodIndexs() {
        return methodIndexs;
    }

    public List<String> getContextParamter() {
        return contextParamter;
    }

    public String getControllerContext() {
        return controllerContext;
    }

    public String getContext() {
        return context;
    }

    private void initController() {
        Class<?> clazz = controller.getClass();
//		String context = clazz.getSimpleName();
        if (clazz.isAnnotationPresent(RestController.class)) {
            RestController restContext = clazz.getAnnotation(RestController.class);
            controllerContext = restContext.value();
            if (controllerContext.contains("{")) {
                context = controllerContext.substring(0, controllerContext.indexOf('{'));
                contextParamter = listOfPathVariavle(controllerContext);
            } else {
                context = controllerContext;
                contextParamter = Collections.emptyList();
            }
        } else {
            // not controller, should return or throw an exceptions, not valid controller 
            // return;
            controllerContext = clazz.getSimpleName();
            context = controllerContext;
            contextParamter = Collections.emptyList();
        }

        //trackAnnotation
        for (Method method : clazz.getMethods()) {
            for (Class<? extends Annotation> annotationClass : requestAnnotation) {
                if (method.isAnnotationPresent(annotationClass)) {
                    method.setAccessible(true);
                    Annotation annotation = method.getAnnotation(annotationClass);
                    methodIndexs.add(getMethodIndex(method, annotation));
                    break; // for now it only support 1 request for a method
                }
            }
        }

    }

    private MethodIndex getMethodIndex(Method method, Annotation annotation) {
        try {

            MethodIndex.MethodIndexBuilder indexBuilder = new MethodIndex.MethodIndexBuilder();
            indexBuilder.method(method);
            indexBuilder.canDoExchange(method.isAnnotationPresent(DoExchange.class));

            HttpMethod httpMethod = annotation.annotationType().getAnnotation(HttpMethod.class);
            indexBuilder.httpMethod(httpMethod.method().name());

            Method pathMethod = annotation.getClass().getMethod("path");
            Method headersMethod = annotation.getClass().getMethod("headers");
            Method producesMethod = annotation.getClass().getMethod("produces");

            indexBuilder.headers(Arrays.asList((String[]) headersMethod.invoke(annotation)));
            indexBuilder.produces(Arrays.asList((String[]) producesMethod.invoke(annotation)));

            String methodPathContext = pathMethod.invoke(annotation).toString();
//			String fullPath = controllerContext +  methodPathContext;

            if (methodPathContext.contains("{")) {
                indexBuilder.context(methodPathContext.substring(0, methodPathContext.indexOf('{')));
                indexBuilder.pathVariables(listOfPathVariavle(methodPathContext));
            } else {
                indexBuilder.context(methodPathContext);
                indexBuilder.pathVariables(Collections.emptyList());
            }

//			indexBuilder.pathVariables(listOfPathVariavle(methodPathContext));
            indexBuilder.regexPattern(setupRegxPattern(controllerContext + methodPathContext));
            indexBuilder.parametersInfo(getParametersInfo(method));
            return indexBuilder.build();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    String setupRegxPattern(String pattern) {
        return '^' + pattern.replaceAll("\\{(?:.[a-zA-Z0-9]*)\\}", "(?:.*)");
    }

    String removeBraces(String string) {
        return string.substring(1, string.length() - 1);
    }

    ArrayList<String> listOfPathVariavle(String path) {
        ArrayList<String> list = new ArrayList<>(3);
        Matcher matcher = Pattern.compile("\\{(?:.[a-zA-Z0-9]*)\\}").matcher(path);
        while (matcher.find()) {
            list.add(removeBraces(matcher.group()));
        }
        return list;
    }


    String getPattern(String pathContext) {
        StringBuilder pattern = new StringBuilder();
        pattern.append('^');


        int mark = 0, start = 0, end = 0;
        do {
            start = pathContext.indexOf('{', mark);
            end = pathContext.indexOf("}", start);
            pattern.append(pathContext.subSequence(mark, start));
            pattern.append("(?:.*)");
            mark = end + 1;
        } while (mark < pathContext.length());
        return pattern.toString();
    }

    private List<ParameterInfo> getParametersInfo(Method method) {
        ParameterInfo.Builder builder = null;
        Parameter parameter;
        Parameter[] parameters = method.getParameters();
        List<ParameterInfo> infos = new ArrayList<>(parameters.length);
        for (int i = 0; i < parameters.length; i++) {
            builder = new ParameterInfo.Builder();
            parameter = parameters[i];
            builder.parameterType(parameter.getType());
            for (Class<? extends Annotation> annotationClass : parameterAnnotation) {
                if (parameter.isAnnotationPresent(annotationClass)) {
                    builder.index(i);
                    Annotation annotation = parameter.getAnnotation(annotationClass);
                    builder.annotation(annotation);
                    if (annotationClass.equals(RequestBody.class)) {
                        builder.name("@Body");
                    } else {
                        try {
                            builder.name(annotationClass.getMethod("value").invoke(annotation).toString());
                        } catch (Exception e) {
                        }
                    }
                    break;
                }
            }
            infos.add(builder.build());
        }
        ;
        return infos;
    }


}
