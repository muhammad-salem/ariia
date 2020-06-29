package org.ariia.mvc.model;

import org.ariia.mvc.annotation.HeaderValue;
import org.ariia.mvc.annotation.PathVariable;
import org.ariia.mvc.annotation.RequestBody;
import org.ariia.mvc.annotation.RequestParam;

import java.lang.annotation.Annotation;
import java.util.Objects;

public class ParameterInfo {

    private Integer index;
    private String name;
    private Annotation annotation;
    private Class<?> parameterType;

    public Integer getIndex() {
        return index;
    }

    public String name() {
        return name;
    }

    public PathVariable asPathVariable() {
        return (PathVariable) annotation;
    }

    public RequestBody asRequestBody() {
        return (RequestBody) annotation;
    }

    public RequestParam asRequestParam() {
        return (RequestParam) annotation;
    }

    public HeaderValue asHeaderValue() {
        return (HeaderValue) annotation;
    }

    public Class<?> getParameterType() {
        return parameterType;
    }

    private boolean hasAnnotationType(Class<? extends Annotation> annotationClass) {
        return Objects.nonNull(annotation) && annotation.annotationType().equals(annotationClass);
    }

    public boolean isPathVariable() {
        return hasAnnotationType(PathVariable.class);
    }

    public boolean isRequestBody() {
        return hasAnnotationType(RequestBody.class);
    }

    public boolean isRequestParam() {
        return hasAnnotationType(RequestParam.class);
    }

    public boolean isHeaderValue() {
        return hasAnnotationType(HeaderValue.class);
    }


    @Override
    public String toString() {
        return "ParameterInfo[ " + index + ", " + name + ", " + annotation + ", " + parameterType + "]";
    }

    public static class Builder {

        private Integer index;
        private String name;
        private Annotation annotation;
        private Class<?> parameterType;

        public Builder index(Integer index) {
            this.index = index;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder annotation(Annotation annotation) {
            this.annotation = annotation;
            return this;
        }

        public Builder parameterType(Class<?> parameterType) {
            this.parameterType = parameterType;
            return this;
        }

        public ParameterInfo build() {
            ParameterInfo info = new ParameterInfo();
            info.index = this.index;
            info.name = this.name;
            info.annotation = this.annotation;
            info.parameterType = this.parameterType;
            return info;
        }

    }

}
