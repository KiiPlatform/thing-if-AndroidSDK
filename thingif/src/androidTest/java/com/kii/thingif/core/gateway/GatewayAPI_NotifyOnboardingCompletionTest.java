package com.kii.thingif.core.gateway;

import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.JsonObject;
import com.kii.thingif.core.KiiApp;
import com.kii.thingif.core.exception.BadRequestException;
import com.kii.thingif.core.exception.ConflictException;
import com.kii.thingif.core.exception.NotFoundException;
import com.kii.thingif.core.exception.ServiceUnavailableException;
import com.kii.thingif.core.exception.UnauthorizedException;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class GatewayAPI_NotifyOnboardingCompletionTest extends GatewayAPITestBase {
    @Test
    public void notifyOnboardingCompletionTest() throws Exception {
        String thingID = "th." + UUID.randomUUID().toString();
        String vendorThingID = UUID.randomUUID().toString();

        GatewayAPI api = this.craeteGatewayAPIWithLoggedIn();

        this.addEmptyMockResponse(204);
        api.notifyOnboardingCompletion(new EndNode(thingID, vendorThingID, null));

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals("/CUSTOM/apps/" + APP_ID + "/gateway/end-nodes/VENDOR_THING_ID:" + vendorThingID, request.getPath());
        Assert.assertEquals("PUT", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        expectedRequestHeaders.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("thingID", thingID);
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test(expected = IllegalStateException.class)
    public void notifyOnboardingCompletionNoLoggedInTest() throws Exception {
        String thingID = "th." + UUID.randomUUID().toString();
        String vendorThingID =  UUID.randomUUID().toString();

        KiiApp app = getApp(APP_ID, APP_KEY);
        Uri gatewayAddress = getGatewayAddress();
        GatewayAPI api = new GatewayAPI(InstrumentationRegistry.getTargetContext(), app, gatewayAddress);
        api.notifyOnboardingCompletion(new EndNode(thingID, vendorThingID, null));
    }
    @Test(expected = IllegalArgumentException.class)
    public void notifyOnboardingCompletionWithNullThingIDTest() throws Exception {
        String thingID = null;
        String vendorThingID =  UUID.randomUUID().toString();

        GatewayAPI api = this.craeteGatewayAPIWithLoggedIn();

        this.addEmptyMockResponse(204);
        api.notifyOnboardingCompletion(new EndNode(thingID, vendorThingID, null));
    }
    @Test(expected = IllegalArgumentException.class)
    public void notifyOnboardingCompletionWithEmptyThingIDTest() throws Exception {
        String thingID = "";
        String vendorThingID = UUID.randomUUID().toString();

        GatewayAPI api = this.craeteGatewayAPIWithLoggedIn();

        this.addEmptyMockResponse(204);
        api.notifyOnboardingCompletion(new EndNode(thingID, vendorThingID, null));
    }
    @Test(expected = IllegalArgumentException.class)
    public void notifyOnboardingCompletionWithNullVenderThingIDTest() throws Exception {
        String thingID = "th." + UUID.randomUUID().toString();
        String vendorThingID = null;

        GatewayAPI api = this.craeteGatewayAPIWithLoggedIn();

        this.addEmptyMockResponse(204);
        api.notifyOnboardingCompletion(new EndNode(thingID, vendorThingID, null));
    }
    @Test(expected = IllegalArgumentException.class)
    public void notifyOnboardingCompletionWithEmptyVenderThingIDTest() throws Exception {
        String thingID = "th." + UUID.randomUUID().toString();
        String vendorThingID = "";

        GatewayAPI api = this.craeteGatewayAPIWithLoggedIn();

        this.addEmptyMockResponse(204);
        api.notifyOnboardingCompletion(new EndNode(thingID, vendorThingID, null));
    }
    @Test
    public void notifyOnboardingCompletion400ErrorTest() throws Exception {
        String thingID = "th." + UUID.randomUUID().toString();
        String vendorThingID = UUID.randomUUID().toString();

        GatewayAPI api = this.craeteGatewayAPIWithLoggedIn();

        this.addEmptyMockResponse(400);
        try {
            api.notifyOnboardingCompletion(new EndNode(thingID, vendorThingID, null));
            Assert.fail("BadRequestException should be thrown");
        } catch (BadRequestException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals("/CUSTOM/apps/" + APP_ID + "/gateway/end-nodes/VENDOR_THING_ID:" + vendorThingID, request.getPath());
        Assert.assertEquals("PUT", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        expectedRequestHeaders.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("thingID", thingID);
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void notifyOnboardingCompletion401ErrorTest() throws Exception {
        String thingID = "th." + UUID.randomUUID().toString();
        String vendorThingID = UUID.randomUUID().toString();

        GatewayAPI api = this.craeteGatewayAPIWithLoggedIn();

        this.addEmptyMockResponse(401);
        try {
            api.notifyOnboardingCompletion(new EndNode(thingID, vendorThingID, null));
            Assert.fail("UnauthorizedException should be thrown");
        } catch (UnauthorizedException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals("/CUSTOM/apps/" + APP_ID + "/gateway/end-nodes/VENDOR_THING_ID:" + vendorThingID, request.getPath());
        Assert.assertEquals("PUT", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        expectedRequestHeaders.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("thingID", thingID);
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void notifyOnboardingCompletion404ErrorTest() throws Exception {
        String thingID = "th." + UUID.randomUUID().toString();
        String vendorThingID = UUID.randomUUID().toString();

        GatewayAPI api = this.craeteGatewayAPIWithLoggedIn();

        this.addEmptyMockResponse(404);
        try {
            api.notifyOnboardingCompletion(new EndNode(thingID, vendorThingID, null));
            Assert.fail("NotFoundException should be thrown");
        } catch (NotFoundException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals("/CUSTOM/apps/" + APP_ID + "/gateway/end-nodes/VENDOR_THING_ID:" + vendorThingID, request.getPath());
        Assert.assertEquals("PUT", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        expectedRequestHeaders.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("thingID", thingID);
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void notifyOnboardingCompletion409ErrorTest() throws Exception {
        String thingID = "th." + UUID.randomUUID().toString();
        String vendorThingID = UUID.randomUUID().toString();

        GatewayAPI api = this.craeteGatewayAPIWithLoggedIn();

        this.addEmptyMockResponse(409);
        try {
            api.notifyOnboardingCompletion(new EndNode(thingID, vendorThingID, null));
            Assert.fail("ConflictException should be thrown");
        } catch (ConflictException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals("/CUSTOM/apps/" + APP_ID + "/gateway/end-nodes/VENDOR_THING_ID:" + vendorThingID, request.getPath());
        Assert.assertEquals("PUT", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        expectedRequestHeaders.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("thingID", thingID);
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void notifyOnboardingCompletion503ErrorTest() throws Exception {
        String thingID = "th." + UUID.randomUUID().toString();
        String vendorThingID = UUID.randomUUID().toString();

        GatewayAPI api = this.craeteGatewayAPIWithLoggedIn();

        this.addEmptyMockResponse(503);
        try {
            api.notifyOnboardingCompletion(new EndNode(thingID, vendorThingID, null));
            Assert.fail("ServiceUnavailableException should be thrown");
        } catch (ServiceUnavailableException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals("/CUSTOM/apps/" + APP_ID + "/gateway/end-nodes/VENDOR_THING_ID:" + vendorThingID, request.getPath());
        Assert.assertEquals("PUT", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        expectedRequestHeaders.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("thingID", thingID);
        this.assertRequestBody(expectedRequestBody, request);
    }
}
