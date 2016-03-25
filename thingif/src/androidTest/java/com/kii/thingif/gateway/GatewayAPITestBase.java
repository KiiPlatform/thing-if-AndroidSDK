package com.kii.thingif.gateway;

import android.support.test.InstrumentationRegistry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kii.thingif.KiiApp;
import com.kii.thingif.SmallTestBase;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GatewayAPITestBase extends SmallTestBase {

    protected static final String APP_ID = "smalltest";
    protected static final String APP_KEY = "abcdefghijklmnopqrstuvwxyz123456789";
    protected static final String ACCESS_TOKEN = "token-0000-1111-aaaa-bbbb";
    private static final String SDK_VERSION = "0.10.0";
    protected MockWebServer server;

    @Before
    public void before() throws Exception {
        super.before();
        this.server = new MockWebServer();
        this.server.start();
    }
    @After
    public void after() throws Exception {
        this.server.shutdown();
    }
    protected KiiApp getApp(String appId, String appKey) {
        String hostName = server.getHostName();
        KiiApp app = KiiApp.Builder.builderWithHostName(appId, appKey, hostName).
                setPort(server.getPort()).setURLSchema("http").build();
        return app;
    }
    protected GatewayAPI4GatewayImpl craeteGatewayAPI4GatewayWithLoggedIn() throws Exception {
        KiiApp app = getApp(APP_ID, APP_KEY);
        this.addMockResponseForLogin(200, ACCESS_TOKEN);
        GatewayAPI4GatewayImpl api = new GatewayAPI4GatewayImpl(InstrumentationRegistry.getTargetContext(), app);
        api.login("dummy", "dummy");
        this.server.takeRequest();
        return api;
    }
    protected GatewayAPI4EndNodeImpl craeteGatewayAPI4EndNodeWithLoggedIn() throws Exception {
        KiiApp app = getApp(APP_ID, APP_KEY);
        this.addMockResponseForLogin(200, ACCESS_TOKEN);
        GatewayAPI4EndNodeImpl api = new GatewayAPI4EndNodeImpl(InstrumentationRegistry.getTargetContext(), app);
        api.login("dummy", "dummy");
        this.server.takeRequest();
        return api;
    }
    protected void addMockResponse(int httpStatus, JsonElement body) {
        this.server.enqueue(new MockResponse().setResponseCode(httpStatus).setBody(body.toString()));
    }
    protected void addEmptyMockResponse(int httpStatus) {
        this.server.enqueue(new MockResponse().setResponseCode(httpStatus));
    }
    protected void addMockResponseForLogin(int httpStatus, String accessToken) {
        JsonObject responseBody = new JsonObject();
        responseBody.addProperty("accessToken", accessToken);
        this.server.enqueue(new MockResponse().setResponseCode(httpStatus).setBody(responseBody.toString()));
    }
    protected void addMockResponseForGetGatewayInformation(int httpStatus, String vendorThingID) {
        JsonObject responseBody = new JsonObject();
        responseBody.addProperty("vendorThingID", vendorThingID);
        this.server.enqueue(new MockResponse().setResponseCode(httpStatus).setBody(responseBody.toString()));
    }
    protected void addMockResponseForOnboardGateway(int httpStatus, String thingID) {
        JsonObject responseBody = new JsonObject();
        responseBody.addProperty("thingID", thingID);
        this.server.enqueue(new MockResponse().setResponseCode(httpStatus).setBody(responseBody.toString()));
    }
    protected void addMockResponseForGetGatewayID(int httpStatus, String thingID) {
        JsonObject responseBody = new JsonObject();
        responseBody.addProperty("thingID", thingID);
        this.server.enqueue(new MockResponse().setResponseCode(httpStatus).setBody(responseBody.toString()));
    }
    protected void addMockResponseForListPendingEndNodes(int httpStatus, PendingEndNode... nodes) {
        JsonObject responseBody = new JsonObject();
        JsonArray nodeArray = new JsonArray();
        for (PendingEndNode node : nodes) {
            JsonObject pendingNode = new JsonObject();
            pendingNode.addProperty("vendorThingID", node.getVendorThingID());
            if (node.getThingProperties() != null) {
                pendingNode.add("thingProperties", new JsonParser().parse(node.getThingProperties().toString()));
            }
            nodeArray.add(pendingNode);
        }
        responseBody.add("results", nodeArray);
        this.server.enqueue(new MockResponse().setResponseCode(httpStatus).setBody(responseBody.toString()));
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

    /**
     * Utilities of checking request header.
     * Don't include X-Kii-SDK header in expected param and don't remove it from
     * actual param.
     * @param expected
     * @param actual
     */
    protected void assertRequestHeader(Map<String, String> expected, RecordedRequest actual) {
        Map<String, List<String>> actualMap = new HashMap<String, List<String>>();
        for (String headerName : actual.getHeaders().names()) {
            actualMap.put(headerName, actual.getHeaders().values(headerName));
        }
        // following headers are added by OkHttp client automatically. So we need to ignore them.
        actualMap.remove("Content-Length");
        actualMap.remove("Host");
        actualMap.remove("Connection");
        actualMap.remove("Accept-Encoding");
        actualMap.remove("User-Agent");

        // Check X-Kii-SDK Header
        List<String> kiiSDK = actualMap.remove("X-Kii-SDK");
        Assert.assertEquals(1, kiiSDK.size());
        Pattern p = Pattern.compile("sn=at;sv=" + SDK_VERSION + ";pv=\\d*");
        Matcher m = p.matcher(kiiSDK.get(0));
        Assert.assertTrue(m.matches());

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
