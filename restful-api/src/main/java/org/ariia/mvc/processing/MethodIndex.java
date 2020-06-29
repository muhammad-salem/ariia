package org.ariia.mvc.processing;


import org.ariia.mvc.model.ParameterInfo;

import java.lang.reflect.Method;
import java.util.List;


public class MethodIndex {

    private Method method;
    private String context;
    private String httpMethod;
    private String regexPattern;
    private boolean canDoExchange;
    private List<String> pathVariables;
    private List<String> headers;
    private List<String> produces;
    private List<ParameterInfo> parametersInfo;


    public String httpMethod() {
        return httpMethod;
    }

    public String context() {
        return context;
    }

    public Method method() {
        return method;
    }

    public boolean canDoExchange() {
        return canDoExchange;
    }

    public String getRegexPattern() {
        return regexPattern;
    }

    public List<String> headers() {
        return headers;
    }

    public List<String> produces() {
        return produces;
    }

    public List<String> pathVariables() {
        return pathVariables;
    }

    public List<ParameterInfo> getParametersInfo() {
        return parametersInfo;
    }

    public boolean hasBodyParameter() {
        return parametersInfo.stream()
                .filter(ParameterInfo::isRequestBody).findAny().isPresent();
    }

    @Override
    public String toString() {
        return String.format("request\t%s %s\ncontext\t%s %s\n%b %s\n%s %s %s\n",
                httpMethod, method.getName(),
                context, parametersInfo,
                canDoExchange, regexPattern,
                headers, pathVariables, produces);
    }

    public static class MethodIndexBuilder {

        private Method method;
        private String context;
        private String httpMethod;
        private String regexPattern;
        private boolean canDoExchange;
        private List<String> pathVariables;
        private List<String> headers;
        private List<String> produces;
        private List<ParameterInfo> parametersInfo;

        public MethodIndexBuilder httpMethod(String httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public MethodIndexBuilder method(Method method) {
            this.method = method;
            return this;
        }

        public MethodIndexBuilder canDoExchange(boolean canDoExchange) {
            this.canDoExchange = canDoExchange;
            return this;
        }

        public MethodIndexBuilder context(String context) {
            this.context = context;
            return this;
        }

        public MethodIndexBuilder regexPattern(String regexPattern) {
            this.regexPattern = regexPattern;
            return this;
        }

        public MethodIndexBuilder pathVariables(List<String> pathVariables) {
            this.pathVariables = pathVariables;
            return this;
        }

        public MethodIndexBuilder headers(List<String> headers) {
            this.headers = headers;
            return this;
        }

        public MethodIndexBuilder produces(List<String> produces) {
            this.produces = produces;
            return this;
        }

        public MethodIndexBuilder parametersInfo(List<ParameterInfo> parametersInfo) {
            this.parametersInfo = parametersInfo;
            return this;
        }

        public MethodIndex build() {
            MethodIndex methodIndex = new MethodIndex();

            methodIndex.httpMethod = this.httpMethod;
            methodIndex.method = this.method;
            methodIndex.context = this.context;
            methodIndex.regexPattern = this.regexPattern;
            methodIndex.canDoExchange = this.canDoExchange;
            methodIndex.pathVariables = this.pathVariables;
            methodIndex.headers = this.headers;
            methodIndex.produces = this.produces;
            methodIndex.parametersInfo = this.parametersInfo;

            return methodIndex;
        }

    }

}