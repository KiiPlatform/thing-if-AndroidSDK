package com.kii.thing_if.gateway;

import android.net.Uri;

import com.kii.thing_if.KiiApp;
import com.kii.thing_if.exception.BadRequestException;
import com.kii.thing_if.exception.ConflictException;
import com.kii.thing_if.exception.UnauthorizedException;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RunWith(RobolectricTestRunner.class)
public class GatewayAPI_RestoreTest extends GatewayAPITestBase {
    @Test
    public void restoreTest() throws Exception {
        GatewayAPI api = this.createGatewayAPIWithLoggedIn();
        this.addEmptyMockResponse(204);
        api.restore();

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals("/gateway-app/gateway/restore", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        this.assertRequestHeader(expectedRequestHeaders, request);

        Assert.assertEquals(0, request.getBodySize());
    }
    @Test(expected = IllegalStateException.class)
    public void restoreNoLoggedInTest() throws Exception {
        KiiApp app = getApp(APP_ID, APP_KEY);
        Uri gatewayAddress = getGatewayAddress();
        GatewayAPI api = new GatewayAPI(RuntimeEnvironment.application.getApplicationContext(), app, gatewayAddress);
        api.restore();
    }
    @Test
    public void restore400ErrorTest() throws Exception {
        GatewayAPI api = this.createGatewayAPIWithLoggedIn();
        this.addEmptyMockResponse(400);
        try {
            api.restore();
            Assert.fail("BadRequestException should be thrown");
        } catch (BadRequestException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals("/gateway-app/gateway/restore", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        this.assertRequestHeader(expectedRequestHeaders, request);

        Assert.assertEquals(0, request.getBodySize());
    }
    @Test
    public void restore401ErrorTest() throws Exception {
        GatewayAPI api = this.createGatewayAPIWithLoggedIn();
        this.addEmptyMockResponse(401);
        try {
            api.restore();
            Assert.fail("UnauthorizedException should be thrown");
        } catch (UnauthorizedException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals("/gateway-app/gateway/restore", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        this.assertRequestHeader(expectedRequestHeaders, request);

        Assert.assertEquals(0, request.getBodySize());
    }
    @Test
    public void restore409ErrorTest() throws Exception {
        GatewayAPI api = this.createGatewayAPIWithLoggedIn();
        this.addEmptyMockResponse(409);
        try {
            api.restore();
            Assert.fail("ConflictException should be thrown");
        } catch (ConflictException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals("/gateway-app/gateway/restore", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        this.assertRequestHeader(expectedRequestHeaders, request);

        Assert.assertEquals(0, request.getBodySize());
    }
}
