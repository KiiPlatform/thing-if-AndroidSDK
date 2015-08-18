package com.kii.iotcloud.http;

import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.kii.iotcloud.Site;
import com.kii.iotcloud.TypedID;
import com.kii.iotcloud.exception.IoTCloudException;
import com.kii.iotcloud.exception.IoTCloudRestException;
import com.kii.iotcloud.http.IoTRestRequest.Method;
import com.kii.iotcloud.utils.IOUtils;
import com.kii.iotcloud.utils.Path;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request.Builder;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import okio.BufferedSink;

public class IoTRestClient {

    protected static final OkHttpClient client = OkHttpClientFactory.newInstance();
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json");

//    private final String appID;
//    private final String appKey;
//    private final Site site;
//    private final String accessToken;

    public IoTRestClient() {
//        this.appID = appID;
//        this.appKey = appKey;
//        this.site = site;
//        this.accessToken = accessToken;
    }
    public JSONObject sendRequest(IoTRestRequest request) throws IoTCloudException {
        Response response = this.execute(request);
        return this.parseResponseAsJsonObject(request, response);
    }


//    public JSONObject onBoarding(JSONObject requestBody) throws IoTCloudException {
//        String path = MessageFormat.format("/iot-api/apps/{0}/onboardings", this.appID);
//        String url = Path.combine(site.getBaseUrl(), path);
//        Map<String, String> headers = this.newHeader();
//        IoTRestRequest request = new IoTRestRequest(url, Method.POST, headers, MEDIA_TYPE_JSON, requestBody);
//        Response response = this.execute(request);
//        return this.parseResponseAsJsonObject(request, response);
//    }
//    /**
//     *
//     * @param target
//     * @param requestBody
//     * @return commandID
//     * @throws IoTCloudException
//     */
//    public String createCommand(TypedID target, JSONObject requestBody) throws IoTCloudException {
//    }
//    public JSONObject updateCommandActionResult(TypedID target, String commandID, JSONObject requestBody) throws IoTCloudException {
//        String path = MessageFormat.format("/iot-api/apps/{0}/targets/{1}/commands/{2}/action-results", this.appID, target.toString(), commandID);
//        String url = Path.combine(site.getBaseUrl(), path);
//        Map<String, String> headers = this.newHeader();
//        IoTRestRequest request = new IoTRestRequest(url, Method.PUT, headers, MEDIA_TYPE_JSON, requestBody);
//        Response response = this.execute(request);
//        return this.parseResponseAsJsonObject(request, response);
//    }
//    public JSONObject getCommand(TypedID target, String commandID) throws IoTCloudException {
//    }
//    public JSONObject getCommands(TypedID target) throws IoTCloudException {
//    }
//    public JSONObject getTargetStatus(TypedID target) throws IoTCloudException {
//        String path = MessageFormat.format("/iot-api/apps/{0}/targets/{1}/states", this.appID, target.toString());
//        String url = Path.combine(site.getBaseUrl(), path);
//        Map<String, String> headers = this.newHeader();
//        IoTRestRequest request = new IoTRestRequest(url, Method.GET, headers);
//        Response response = this.execute(request);
//        return this.parseResponseAsJsonObject(request, response);
//    }
//    public JSONObject updateTargetStatus(TypedID target, JSONObject requestBody) throws IoTCloudException {
//        String path = MessageFormat.format("/iot-api/apps/{0}/targets/{1}/states", this.appID, target.toString());
//        String url = Path.combine(site.getBaseUrl(), path);
//        Map<String, String> headers = this.newHeader();
//        IoTRestRequest request = new IoTRestRequest(url, Method.PUT, headers, MEDIA_TYPE_JSON, requestBody);
//        Response response = this.execute(request);
//        return this.parseResponseAsJsonObject(request, response);
//    }
//    public JSONObject createTrigger(TypedID target, JSONObject requestBody) throws IoTCloudException {
//    }
//    public JSONObject getTrigger(TypedID target, String triggerID) throws IoTCloudException {
//        String path = MessageFormat.format("/iot-api/apps/{0}/targets/{1}/triggers/{2}", this.appID, target.toString(), triggerID);
//        String url = Path.combine(site.getBaseUrl(), path);
//        Map<String, String> headers = this.newHeader();
//        IoTRestRequest request = new IoTRestRequest(url, Method.GET, headers);
//        Response response = this.execute(request);
//        return this.parseResponseAsJsonObject(request, response);
//    }
//    public JSONObject getTriggers(TypedID target) throws IoTCloudException {
//        String path = MessageFormat.format("/iot-api/apps/{0}/targets/{1}/triggers", this.appID, target.toString());
//        String url = Path.combine(site.getBaseUrl(), path);
//        Map<String, String> headers = this.newHeader();
//        IoTRestRequest request = new IoTRestRequest(url, Method.GET, headers);
//        Response response = this.execute(request);
//        return this.parseResponseAsJsonObject(request, response);
//    }
//    public void deleteTrigger(TypedID target, String triggerID) throws IoTCloudException {
//        String path = MessageFormat.format("/iot-api/apps/{0}/targets/{1}/triggers/{2}", this.appID, target.toString(), triggerID);
//        String url = Path.combine(site.getBaseUrl(), path);
//        Map<String, String> headers = this.newHeader();
//        IoTRestRequest request = new IoTRestRequest(url, Method.DELETE, headers);
//        Response response = this.execute(request);
//        this.parseResponse(request, response);
//    }
//    public void disableTrigger(TypedID target, String triggerID) throws IoTCloudException {
//        String path = MessageFormat.format("/iot-api/apps/{0}/targets/{1}/triggers/{2}/disable", this.appID, target.toString(), triggerID);
//        String url = Path.combine(site.getBaseUrl(), path);
//        Map<String, String> headers = this.newHeader();
//        IoTRestRequest request = new IoTRestRequest(url, Method.PUT, headers);
//        Response response = this.execute(request);
//        this.parseResponse(request, response);
//    }
//    public void enableTrigger(TypedID target, String triggerID) throws IoTCloudException {
//        String path = MessageFormat.format("/iot-api/apps/{0}/targets/{1}/triggers/{2}/enable", this.appID, target.toString(), triggerID);
//        String url = Path.combine(site.getBaseUrl(), path);
//        Map<String, String> headers = this.newHeader();
//        IoTRestRequest request = new IoTRestRequest(url, Method.PUT, headers);
//        Response response = this.execute(request);
//        this.parseResponse(request, response);
//    }
    private Response execute(IoTRestRequest request) throws IoTCloudException {
        Builder builder = new Builder();
        builder.url(request.getUrl());
        builder.headers(Headers.of(request.getHeaders()));

        OkHttpClient httpClient = client.clone();
        switch (request.getMethod()) {
            case HEAD:
                builder.head();
                break;
            case GET:
                builder.get();
                break;
            case POST:
                builder.post(this.createRequestBody(request.getContentType(), request.getEntity()));
                httpClient.setRetryOnConnectionFailure(false);
                break;
            case PUT:
                builder.put(this.createRequestBody(request.getContentType(), request.getEntity()));
                httpClient.setRetryOnConnectionFailure(false);
                break;
            case DELETE:
                builder.delete();
                break;
        }
        try {
            return httpClient.newCall(builder.build()).execute();
        } catch (IOException e) {
            throw new IoTCloudException(request.getCurl(), e);
        }
    }
//    private Map<String, String> newHeader() {
//        Map<String, String> headers = new HashMap<String, String>();
//        if (!TextUtils.isEmpty(this.appID)) {
//            headers.put("X-Kii-AppID", this.appID);
//        }
//        if (!TextUtils.isEmpty(this.appKey)) {
//            headers.put("X-Kii-AppKey", this.appKey);
//        }
//        if (!TextUtils.isEmpty(this.accessToken)) {
//            headers.put("Authorization", "Bearer " + this.accessToken);
//        }
//        return headers;
//    }
    protected RequestBody createRequestBody(final MediaType contentType, final Object entity) {
        if (entity == null) {
            return RequestBody.create(contentType, "");
        }
        if (entity instanceof String) {
            return RequestBody.create(contentType, (String)entity);
        }
        if (entity instanceof byte[]) {
            return RequestBody.create(contentType, (byte[])entity);
        }
        if (entity instanceof File) {
            return RequestBody.create(contentType, (File)entity);
        }
        if (entity instanceof JSONObject) {
            return RequestBody.create(contentType, ((JSONObject)entity).toString());
        }
        if (entity instanceof JSONArray) {
            return RequestBody.create(contentType, ((JSONArray)entity).toString());
        }
        if (entity instanceof InputStream) {
            return new RequestBody() {
                @Override
                public long contentLength() throws IOException {
                    return ((InputStream)entity).available();
                }
                @Override
                public MediaType contentType() {
                    return contentType;
                }
                @Override
                public void writeTo(BufferedSink sink) throws IOException {
                    OutputStream os = sink.outputStream();
                    IOUtils.copy((InputStream) entity, os);
                }
            };
        }
        throw new RuntimeException("Unexpected entity type.");
    }
    private void parseResponse(IoTRestRequest request, Response response) throws IoTCloudException {
        try {
            String body = response.body().string();
            this.checkHttpStatus(request, response, body);
        } catch (IOException e) {
            throw new IoTCloudException(request.getCurl(), e);
        }
    }
    private String parseResponseAsString(IoTRestRequest request, Response response) throws IoTCloudException {
        try {
            String body = response.body().string();
            this.checkHttpStatus(request, response, body);
            if (TextUtils.isEmpty(body)) {
                return null;
            }
            return body;
        } catch (IOException e) {
            throw new IoTCloudException(request.getCurl(), e);
        }
    }
    private JSONObject parseResponseAsJsonObject(IoTRestRequest request, Response response) throws IoTCloudException {
        try {
            String body = response.body().string();
            this.checkHttpStatus(request, response, body);
            if (TextUtils.isEmpty(body)) {
                return null;
            }
            return new JSONObject(body);
        } catch (JSONException e) {
            throw new IoTCloudException(request.getCurl(), e);
        } catch (IOException e) {
            throw new IoTCloudException(request.getCurl(), e);
        }
    }
    private JSONArray parseResponseAsJsonArray(IoTRestRequest request, Response response) throws IoTCloudException {
        try {
            String body = response.body().string();
            this.checkHttpStatus(request, response, body);
            if (TextUtils.isEmpty(body)) {
                return null;
            }
            return new JSONArray(body);
        } catch (JSONException e) {
            throw new IoTCloudException(request.getCurl(), e);
        } catch (IOException e) {
            throw new IoTCloudException(request.getCurl(), e);
        }
    }
    private InputStream parseResponseAsInputStream(IoTRestRequest request, Response response) throws IoTCloudException {
        try {
            if (!response.isSuccessful()) {
                String body = response.body().string();
                this.checkHttpStatus(request, response, body);
            }
            return response.body().byteStream();
        } catch (IOException e) {
            throw new IoTCloudException(request.getCurl(), e);
        }
    }
    private void checkHttpStatus(IoTRestRequest request, Response response, String responseBody) throws IoTCloudRestException {
        if (!response.isSuccessful()) {
            JSONObject errorDetail = null;
            try {
                errorDetail = new JSONObject(responseBody);
            } catch (Exception ignore) {
            }
            if (!response.isSuccessful()) {
                throw new IoTCloudRestException(request.getCurl(), response.code(), errorDetail);
            }
        }
    }
}
