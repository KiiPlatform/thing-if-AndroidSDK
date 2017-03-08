package com.kii.thingif.gateway;

import android.net.Uri;

import com.kii.thingif.KiiApp;
import com.kii.thingif.exception.BadRequestException;
import com.kii.thingif.exception.ConflictException;
import com.kii.thingif.exception.ServiceUnavailableException;
import com.kii.thingif.exception.UnauthorizedException;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RunWith(RobolectricTestRunner.class)
public class GatewayAPI_OnboardGatewayTest extends GatewayAPITestBase {
    @Test
    public void onboardGatewayTest() throws Exception {
        String thingID = "th." + UUID.randomUUID().toString();

        GatewayAPI api = this.createGatewayAPIWithLoggedIn();
        this.addMockResponseForOnboardGateway(200, thingID);
        Gateway result = api.onboardGateway();

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals("/CUSTOM/apps/" + APP_ID + "/gateway/onboarding", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        this.assertRequestHeader(expectedRequestHeaders, request);

        Assert.assertEquals(0, request.getBodySize());
        Assert.assertEquals(thingID, result.getThingID());
    }
    @Test(expected = IllegalStateException.class)
    public void onboardGatewayNoLoggedInTest() throws Exception {
        KiiApp app = getApp(APP_ID, APP_KEY);
        Uri gatewayAddress = getGatewayAddress();
        GatewayAPI api = new GatewayAPI(RuntimeEnvironment.application.getApplicationContext(), app, gatewayAddress);
        api.onboardGateway();
    }
    @Test
    public void onboardGateway400ErrorTest() throws Exception {
        String thingID = "th." + UUID.randomUUID().toString();

        GatewayAPI api = this.createGatewayAPIWithLoggedIn();
        this.addEmptyMockResponse(400);
        try {
            api.onboardGateway();
            Assert.fail("BadRequestException should be thrown");
        } catch (BadRequestException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals("/CUSTOM/apps/" + APP_ID + "/gateway/onboarding", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        this.assertRequestHeader(expectedRequestHeaders, request);

        Assert.assertEquals(0, request.getBodySize());
    }
    @Test
    public void onboardGateway401ErrorTest() throws Exception {
        String thingID = "th." + UUID.randomUUID().toString();

        GatewayAPI api = this.createGatewayAPIWithLoggedIn();
        this.addEmptyMockResponse(401);
        try {
            api.onboardGateway();
            Assert.fail("UnauthorizedException should be thrown");
        } catch (UnauthorizedException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals("/CUSTOM/apps/" + APP_ID + "/gateway/onboarding", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        this.assertRequestHeader(expectedRequestHeaders, request);

        Assert.assertEquals(0, request.getBodySize());
    }
    @Test
    public void onboardGateway409ErrorTest() throws Exception {
        String thingID = "th." + UUID.randomUUID().toString();

        GatewayAPI api = this.createGatewayAPIWithLoggedIn();
        this.addEmptyMockResponse(409);
        try {
            api.onboardGateway();
            Assert.fail("ConflictException should be thrown");
        } catch (ConflictException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals("/CUSTOM/apps/" + APP_ID + "/gateway/onboarding", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        this.assertRequestHeader(expectedRequestHeaders, request);

        Assert.assertEquals(0, request.getBodySize());
    }
    @Test
    public void onboardGateway503ErrorTest() throws Exception {
        String thingID = "th." + UUID.randomUUID().toString();

        GatewayAPI api = this.createGatewayAPIWithLoggedIn();
        this.addEmptyMockResponse(503);
        try {
            api.onboardGateway();
            Assert.fail("ServiceUnavailableException should be thrown");
        } catch (ServiceUnavailableException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals("/CUSTOM/apps/" + APP_ID + "/gateway/onboarding", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        this.assertRequestHeader(expectedRequestHeaders, request);

        Assert.assertEquals(0, request.getBodySize());
    }
}
