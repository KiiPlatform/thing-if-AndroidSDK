package com.kii.thingif.gateway;

import android.net.Uri;

import com.kii.thingif.KiiApp;
import com.kii.thingif.exception.BadRequestException;
import com.kii.thingif.exception.ConflictException;
import com.kii.thingif.exception.NotFoundException;
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
public class GatewayAPI_GetGatewayIDTest extends GatewayAPITestBase{

    @Test
    public void getGatewayIDTest() throws Exception {
        String thingID = "th." + UUID.randomUUID().toString();
        GatewayAPI api = this.createGatewayAPIWithLoggedIn();
        this.addMockResponseForGetGatewayID(200, thingID);
        String gatewayID = api.getGatewayID();

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals("/CUSTOM/apps/" + APP_ID + "/gateway/id", request.getPath());
        org.junit.Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        this.assertRequestHeader(expectedRequestHeaders, request);

        Assert.assertEquals(thingID, gatewayID);
    }
    @Test(expected = IllegalStateException.class)
    public void getGatewayIDNoLoggedInTest() throws Exception {
        KiiApp app = getApp(APP_ID, APP_KEY);
        Uri gatewayAddress = getGatewayAddress();
        GatewayAPI api = new GatewayAPI(RuntimeEnvironment.application.getApplicationContext(), app, gatewayAddress);
        api.getGatewayID();
    }
    @Test
    public void getGatewayID400ErrorTest() throws Exception {
        GatewayAPI api = this.createGatewayAPIWithLoggedIn();
        this.addEmptyMockResponse(400);

        try {
            api.getGatewayID();
            org.junit.Assert.fail("BadRequestException should be thrown");
        } catch (BadRequestException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals("/CUSTOM/apps/" + APP_ID + "/gateway/id", request.getPath());
        org.junit.Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
    @Test
    public void getGatewayID401ErrorTest() throws Exception {
        GatewayAPI api = this.createGatewayAPIWithLoggedIn();
        this.addEmptyMockResponse(401);

        try {
            api.getGatewayID();
            org.junit.Assert.fail("UnauthorizedException should be thrown");
        } catch (UnauthorizedException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals("/CUSTOM/apps/" + APP_ID + "/gateway/id", request.getPath());
        org.junit.Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
    @Test
    public void getGatewayID404ErrorTest() throws Exception {
        GatewayAPI api = this.createGatewayAPIWithLoggedIn();
        this.addEmptyMockResponse(404);

        try {
            api.getGatewayID();
            org.junit.Assert.fail("NotFoundException should be thrown");
        } catch (NotFoundException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals("/CUSTOM/apps/" + APP_ID + "/gateway/id", request.getPath());
        org.junit.Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
    @Test
    public void getGatewayID409ErrorTest() throws Exception {
        GatewayAPI api = this.createGatewayAPIWithLoggedIn();
        this.addEmptyMockResponse(409);

        try {
            api.getGatewayID();
            org.junit.Assert.fail("ConflictException should be thrown");
        } catch (ConflictException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals("/CUSTOM/apps/" + APP_ID + "/gateway/id", request.getPath());
        org.junit.Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
    @Test
    public void getGatewayID503ErrorTest() throws Exception {
        GatewayAPI api = this.createGatewayAPIWithLoggedIn();
        this.addEmptyMockResponse(503);

        try {
            api.getGatewayID();
            org.junit.Assert.fail("ServiceUnavailableException should be thrown");
        } catch (ServiceUnavailableException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals("/CUSTOM/apps/" + APP_ID + "/gateway/id", request.getPath());
        org.junit.Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
}
