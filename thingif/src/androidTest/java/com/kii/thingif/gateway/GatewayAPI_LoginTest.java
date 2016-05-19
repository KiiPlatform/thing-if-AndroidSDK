package com.kii.thingif.gateway;

import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Base64;

import com.google.gson.JsonObject;
import com.kii.thingif.KiiApp;
import com.kii.thingif.exception.BadRequestException;
import com.kii.thingif.exception.UnauthorizedException;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class GatewayAPI_LoginTest extends GatewayAPITestBase {
    @Test
    public void loginTest() throws Exception {
        String username = "user01";
        String password = "pa$$word";
        String accessToken = "token-abcd1234";
        KiiApp app = this.getApp(APP_ID, APP_KEY);
        Uri gatewayAddress = getGatewayAddress();
        GatewayAPI api = new GatewayAPI(InstrumentationRegistry.getTargetContext(), app, gatewayAddress);

        this.addMockResponseForLogin(200, accessToken);
        api.login(username, password);

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals("/CUSTOM/token", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("Authorization", "Basic " + Base64.encodeToString((APP_ID + ":" + APP_KEY).getBytes(), Base64.NO_WRAP));
        expectedRequestHeaders.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("username", username);
        expectedRequestBody.addProperty("password", password);
        this.assertRequestBody(expectedRequestBody, request);

        Assert.assertEquals(accessToken, api.getAccessToken());
    }
    @Test(expected = IllegalArgumentException.class)
    public void loginWithNullUsernameTest() throws Exception {
        String username = null;
        String password = "pa$$word";
        String accessToken = "token-abcd1234";
        KiiApp app = this.getApp(APP_ID, APP_KEY);
        Uri gatewayAddress = getGatewayAddress();
        GatewayAPI api = new GatewayAPI(InstrumentationRegistry.getTargetContext(), app, gatewayAddress);

        api.login(username, password);
    }
    @Test(expected = IllegalArgumentException.class)
    public void loginWithEmptyUsernameTest() throws Exception {
        String username = "";
        String password = "pa$$word";
        String accessToken = "token-abcd1234";
        KiiApp app = this.getApp(APP_ID, APP_KEY);
        Uri gatewayAddress = getGatewayAddress();
        GatewayAPI api = new GatewayAPI(InstrumentationRegistry.getTargetContext(), app, gatewayAddress);

        api.login(username, password);
    }
    @Test(expected = IllegalArgumentException.class)
    public void loginWithNullPasswordTest() throws Exception {
        String username = "user01";
        String password = null;
        String accessToken = "token-abcd1234";
        KiiApp app = this.getApp(APP_ID, APP_KEY);
        Uri gatewayAddress = getGatewayAddress();
        GatewayAPI api = new GatewayAPI(InstrumentationRegistry.getTargetContext(), app, gatewayAddress);

        api.login(username, password);
    }
    @Test(expected = IllegalArgumentException.class)
    public void loginWithEmptyPasswordTest() throws Exception {
        String username = "user01";
        String password = "";
        String accessToken = "token-abcd1234";
        KiiApp app = this.getApp(APP_ID, APP_KEY);
        Uri gatewayAddress = getGatewayAddress();
        GatewayAPI api = new GatewayAPI(InstrumentationRegistry.getTargetContext(), app, gatewayAddress);

        api.login(username, password);
    }
    @Test
    public void login400ErrorTest() throws Exception {
        String username = "user01";
        String password = "pa$$word";
        String accessToken = "token-abcd1234";
        KiiApp app = this.getApp(APP_ID, APP_KEY);
        Uri gatewayAddress = getGatewayAddress();
        GatewayAPI api = new GatewayAPI(InstrumentationRegistry.getTargetContext(), app, gatewayAddress);

        this.addEmptyMockResponse(400);
        try {
            api.login(username, password);
            Assert.fail("BadRequestException should be thrown");
        } catch (BadRequestException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals("/CUSTOM/token", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("Authorization", "Basic " + Base64.encodeToString((APP_ID + ":" + APP_KEY).getBytes(), Base64.NO_WRAP));
        expectedRequestHeaders.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("username", username);
        expectedRequestBody.addProperty("password", password);
        this.assertRequestBody(expectedRequestBody, request);

        Assert.assertNull(api.getAccessToken());
    }
    @Test
    public void login401ErrorTest() throws Exception {
        String username = "user01";
        String password = "pa$$word";
        String accessToken = "token-abcd1234";
        KiiApp app = this.getApp(APP_ID, APP_KEY);
        Uri gatewayAddress = getGatewayAddress();
        GatewayAPI api = new GatewayAPI(InstrumentationRegistry.getTargetContext(), app, gatewayAddress);

        this.addEmptyMockResponse(401);
        try {
            api.login(username, password);
            Assert.fail("UnauthorizedException should be thrown");
        } catch (UnauthorizedException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals("/CUSTOM/token", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("Authorization", "Basic " + Base64.encodeToString((APP_ID + ":" + APP_KEY).getBytes(), Base64.NO_WRAP));
        expectedRequestHeaders.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("username", username);
        expectedRequestBody.addProperty("password", password);
        this.assertRequestBody(expectedRequestBody, request);

        Assert.assertNull(api.getAccessToken());
    }
}
