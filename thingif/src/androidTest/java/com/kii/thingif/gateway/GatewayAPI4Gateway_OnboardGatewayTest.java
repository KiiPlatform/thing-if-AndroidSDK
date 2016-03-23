package com.kii.thingif.gateway;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.kii.thingif.KiiApp;
import com.kii.thingif.exception.BadRequestException;
import com.kii.thingif.exception.ConflictException;
import com.kii.thingif.exception.ServiceUnavailableException;
import com.kii.thingif.exception.UnauthorizedException;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class GatewayAPI4Gateway_OnboardGatewayTest extends GatewayAPITestBase {

    @Test
    public void onboardGatewayTest() throws Exception {
        String thingID = "th." + UUID.randomUUID().toString();

        GatewayAPI4Gateway api = this.craeteGatewayAPI4GatewayWithLoggedIn();
        this.addMockResponseForOnboardGateway(200, thingID);
        String result = api.onboardGateway();

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals("/gateway-app/gateway/onboarding", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        this.assertRequestHeader(expectedRequestHeaders, request);

        Assert.assertEquals(0, request.getBodySize());
        Assert.assertEquals(thingID, result);
    }
    @Test(expected = IllegalStateException.class)
    public void onboardGatewayNoLoggedInTest() throws Exception {
        KiiApp app = getApp(APP_ID, APP_KEY);
        GatewayAPI4Gateway api = new GatewayAPI4Gateway(InstrumentationRegistry.getTargetContext(), app);
        api.onboardGateway();
    }
    @Test
    public void onboardGateway400ErrorTest() throws Exception {
        String thingID = "th." + UUID.randomUUID().toString();

        GatewayAPI4Gateway api = this.craeteGatewayAPI4GatewayWithLoggedIn();
        this.addEmptyMockResponse(400);
        try {
            api.onboardGateway();
            Assert.fail("BadRequestException should be thrown");
        } catch (BadRequestException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals("/gateway-app/gateway/onboarding", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        this.assertRequestHeader(expectedRequestHeaders, request);

        Assert.assertEquals(0, request.getBodySize());
    }
    @Test
    public void onboardGateway401ErrorTest() throws Exception {
        String thingID = "th." + UUID.randomUUID().toString();

        GatewayAPI4Gateway api = this.craeteGatewayAPI4GatewayWithLoggedIn();
        this.addEmptyMockResponse(401);
        try {
            api.onboardGateway();
            Assert.fail("UnauthorizedException should be thrown");
        } catch (UnauthorizedException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals("/gateway-app/gateway/onboarding", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        this.assertRequestHeader(expectedRequestHeaders, request);

        Assert.assertEquals(0, request.getBodySize());
    }
    @Test
    public void onboardGateway409ErrorTest() throws Exception {
        String thingID = "th." + UUID.randomUUID().toString();

        GatewayAPI4Gateway api = this.craeteGatewayAPI4GatewayWithLoggedIn();
        this.addEmptyMockResponse(409);
        try {
            api.onboardGateway();
            Assert.fail("ConflictException should be thrown");
        } catch (ConflictException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals("/gateway-app/gateway/onboarding", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        this.assertRequestHeader(expectedRequestHeaders, request);

        Assert.assertEquals(0, request.getBodySize());
    }
    @Test
    public void onboardGateway503ErrorTest() throws Exception {
        String thingID = "th." + UUID.randomUUID().toString();

        GatewayAPI4Gateway api = this.craeteGatewayAPI4GatewayWithLoggedIn();
        this.addEmptyMockResponse(503);
        try {
            api.onboardGateway();
            Assert.fail("ServiceUnavailableException should be thrown");
        } catch (ServiceUnavailableException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals("/gateway-app/gateway/onboarding", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        this.assertRequestHeader(expectedRequestHeaders, request);

        Assert.assertEquals(0, request.getBodySize());
    }
}
