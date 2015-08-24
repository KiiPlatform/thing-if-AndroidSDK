package com.kii.iotcloud;

import android.test.mock.MockContext;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kii.iotcloud.schema.Schema;
import com.kii.iotcloud.schema.SchemaBuilder;
import com.kii.iotcloud.testmodel.LightState;
import com.kii.iotcloud.testmodel.SetBrightness;
import com.kii.iotcloud.testmodel.SetBrightnessResult;
import com.kii.iotcloud.testmodel.SetColor;
import com.kii.iotcloud.testmodel.SetColorResult;
import com.kii.iotcloud.testmodel.SetColorTemperature;
import com.kii.iotcloud.testmodel.SetColorTemperatureResult;
import com.kii.iotcloud.testmodel.TurnPower;
import com.kii.iotcloud.testmodel.TurnPowerResult;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class IoTCloudAPITestBase {
    public static final String DEMO_THING_TYPE = "LED";
    public static final String DEMO_SCHEMA_NAME = "SmartLightDemo";
    public static final int DEMO_SCHEMA_VERSION = 1;

    protected MockWebServer server;

    @Before
    public void before() throws Exception {
        this.server = new MockWebServer();
        this.server.start();
    }
    @After
    public void after() throws Exception {
        this.server.shutdown();
    }

    protected Schema createDefaultSchema() {
        SchemaBuilder sb = SchemaBuilder.newSchemaBuilder(DEMO_THING_TYPE, DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, LightState.class);
        sb.addActionClass(SetColor.class, SetColorResult.class);
        sb.addActionClass(SetBrightness.class, SetBrightnessResult.class);
        sb.addActionClass(SetColorTemperature.class, SetColorTemperatureResult.class);
        sb.addActionClass(TurnPower.class, TurnPowerResult.class);
        return sb.build();
    }
    protected IoTCloudAPI craeteIoTCloudAPIWithDemoSchema(String appID, String appKey) throws Exception {
        String ownerID = UUID.randomUUID().toString();
        Owner owner = new Owner(new TypedID(TypedID.Types.USER, ownerID), "owner-access-token-1234");
        URL baseUrl = this.server.getUrl("/");
        IoTCloudAPIBuilder builder = IoTCloudAPIBuilder.newBuilder(new MockContext(), appID, appKey, baseUrl.toString(), owner);
        builder.addSchema(this.createDefaultSchema());
        return builder.build();
    }
    protected void addMockResponseForOnBoard(int httpStatus, String thingID, String accessToken) {
        MockResponse response = new MockResponse().setResponseCode(httpStatus);
        if (thingID != null && accessToken != null) {
            JsonObject responseBody = new JsonObject();
            responseBody.addProperty("thingID", thingID);
            responseBody.addProperty("accessToken", accessToken);
            response.setBody(responseBody.toString());
        }
        this.server.enqueue(response);
    }
    protected void assertRequestBody(String expected, RecordedRequest actual) {
        this.assertRequestBody(new JsonParser().parse(expected), actual);
    }
    protected void assertRequestBody(JSONObject expected, RecordedRequest actual) {
        this.assertRequestBody(new JsonParser().parse(expected.toString()), actual);
    }
    protected void assertRequestBody(JsonElement expected, RecordedRequest actual) {
        Assert.assertEquals("request body", expected, new JsonParser().parse(actual.getBody().readUtf8()));
    }
    protected void assertRequestHeader(Map<String, String> expected, RecordedRequest actual) {
        Map<String, List<String>> actualMap = actual.getHeaders().toMultimap();
        // following headers are added by OkHttp client automatically. So we need to ignore them.
        actualMap.remove("Content-Length");
        actualMap.remove("Host");
        actualMap.remove("Connection");
        actualMap.remove("Accept-Encoding");
        actualMap.remove("User-Agent");

        Assert.assertEquals("number of request headers", expected.size(), actualMap.size());
        for (Map.Entry<String, String> h : expected.entrySet()) {
            String expectedHeaderValue = h.getValue();
            if ("Content-Type".equalsIgnoreCase(h.getKey())) {
                // OkHttp adds charset to the Content-Type automatically.
                if (expectedHeaderValue.indexOf("; charset=utf-8") < 0) {
                    expectedHeaderValue += "; charset=utf-8";
                }
            }
            Assert.assertEquals("request header(" + h.getKey() + ")", expectedHeaderValue, actualMap.get(h.getKey()).get(0));
        }
    }
}
