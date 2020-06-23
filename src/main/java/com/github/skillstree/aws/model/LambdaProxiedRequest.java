package com.github.skillstree.aws.model;

import java.util.Map;

public class LambdaProxiedRequest {

    private String methodArn;

    private String httpMethod;

    private Map<String, String> headers;

    private String body;

    private String path;

    private Map<String, String> pathParameters;

    public String getMethodArn() {
        return methodArn;
    }

    public LambdaProxiedRequest setMethodArn(String methodArn) {
        this.methodArn = methodArn;
        return this;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public LambdaProxiedRequest setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
        return this;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public LambdaProxiedRequest setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public String getBody() {
        return body;
    }

    public LambdaProxiedRequest setBody(String body) {
        this.body = body;
        return this;
    }

    public String getPath() {
        return path;
    }

    public LambdaProxiedRequest setPath(String path) {
        this.path = path;
        return this;
    }

    public Map<String, String> getPathParameters() {
        return pathParameters;
    }

    public LambdaProxiedRequest setPathParameters(Map<String, String> pathParameters) {
        this.pathParameters = pathParameters;
        return this;
    }

    @Override
    public String toString() {
        return "LambdaProxiedRequest{" +
                "httpMethod='" + httpMethod + '\'' +
                ", body='" + body + '\'' +
                ", pathParameters=" + pathParameters +
                '}';
    }
}
