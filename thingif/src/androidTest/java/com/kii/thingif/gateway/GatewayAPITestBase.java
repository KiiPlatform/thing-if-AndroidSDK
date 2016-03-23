package com.kii.thingif.gateway;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kii.thingif.KiiApp;
import com.kii.thingif.SmallTestBase;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import org.junit.After;
import org.junit.Before;

public class GatewayAPITestBase extends SmallTestBase {
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
    protected KiiApp getApp(String appId, String appKey) throws NoSuchFieldException, IllegalAccessException {
        String hostName = server.getHostName();
        KiiApp app = KiiApp.Builder.builderWithHostName(appId, appKey, hostName).
                setPort(server.getPort()).setURLSchema("http").build();
        return app;
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

}
