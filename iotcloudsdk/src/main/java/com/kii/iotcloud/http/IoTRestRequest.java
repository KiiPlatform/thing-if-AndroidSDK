package com.kii.iotcloud.http;

import com.squareup.okhttp.MediaType;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class IoTRestRequest {
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
    private final Map<String, String> queryParameters = new HashMap<String, String>();
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
        return url + this.getQueryParameter();
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
    public IoTRestRequest addQueryParameter(String name, Object value) {
        if (value != null) {
            this.queryParameters.put(name, value.toString());
        }
        return this;
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
        curl.append(" '" + this.getUrl() + "'");
        if (this.entity != null) {
            curl.append(" -d '" + entity.toString() + "'");
        }
        return curl.toString();
    }
    private String getQueryParameter() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> queryParameter : this.queryParameters.entrySet()) {
            if (sb.length() == 0) {
                sb.append("?");
            } else {
                sb.append("&");
            }
            try {
                sb.append(queryParameter.getKey() + "=" + URLEncoder.encode(queryParameter.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                // Won’t happen
            }
        }
        return sb.toString();
    }
}
