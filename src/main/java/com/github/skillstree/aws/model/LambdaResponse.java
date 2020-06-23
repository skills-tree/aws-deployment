package com.github.skillstree.aws.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LambdaResponse {

    private int statusCode;

    private Map<String, String> headers = new HashMap<>();

    private String body;

    private boolean isBase64Encoded;

    public int getStatusCode() {
        return statusCode;
    }

    public LambdaResponse setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public LambdaResponse setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public LambdaResponse addHeader(String name, String value) {
        headers.put(name, value);
        return this;
    }

    public String getBody() {
        return body;
    }

    public LambdaResponse setBody(String body) {
        this.body = body;
        return this;
    }

    public boolean isBase64Encoded() {
        return isBase64Encoded;
    }

    public LambdaResponse setBase64Encoded(boolean base64Encoded) {
        isBase64Encoded = base64Encoded;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LambdaResponse that = (LambdaResponse) o;
        return statusCode == that.statusCode &&
                isBase64Encoded == that.isBase64Encoded &&
                Objects.equals(headers, that.headers) &&
                Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusCode, headers, body, isBase64Encoded);
    }

    @Override
    public String toString() {
        return "LambdaResponse{" +
                "statusCode=" + statusCode +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                ", isBase64Encoded=" + isBase64Encoded +
                '}';
    }
}
