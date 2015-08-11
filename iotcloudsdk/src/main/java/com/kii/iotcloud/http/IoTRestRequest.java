package com.kii.iotcloud.http;

import com.squareup.okhttp.MediaType;

import java.util.Map;

class IoTRestRequest {
    public enum Method {
        HEAD,
        GET,
        POST,
        PUT,
        DELETE
    }
    private final String url;
    private final Method method;
    private final Map<String, String> headers;
    private final MediaType contentType;
    private final Object entity;

    public IoTRestRequest(String url, Method method, Map<String, String> headers) {
        this(url, method, headers, null, null);
    }
    public IoTRestRequest(String url, Method method, Map<String, String> headers, MediaType contentType, Object entity) {
        this.url = url;
        this.method = method;
        this.headers = headers;
        this.contentType = contentType;
        this.entity = entity;
    }
    public String getUrl() {
        return url;
    }
    public Method getMethod() {
        return method;
    }
    public Map<String, String> getHeaders() {
        return headers;
    }
    public MediaType getContentType() {
        return contentType;
    }
    public Object getEntity() {
        return entity;
    }
    public String getCurl() {
        StringBuilder curl = new StringBuilder();
        curl.append("curl -v -X " + this.method.name());
        if (this.contentType != null) {
            curl.append(" -H 'Content-Type:" + this.contentType.toString() + "'");
        }
        for (Map.Entry<String, String> header : this.headers.entrySet()) {
            curl.append(" -H '" + header.getKey() + ":" + header.getValue() + "'");
        }
        curl.append(" '" + this.url + "'");
        if (this.entity != null) {
            curl.append(" -d '" + entity.toString() + "'");
        }
        return curl.toString();
    }
}
