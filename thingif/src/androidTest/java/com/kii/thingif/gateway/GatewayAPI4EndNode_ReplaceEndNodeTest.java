package com.kii.thingif.gateway;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.JsonObject;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class GatewayAPI4EndNode_ReplaceEndNodeTest extends GatewayAPITestBase {

    @Test
    public void replaceEndNodeTest() throws Exception {
        String thingID = "th." + UUID.randomUUID().toString();
        String vendorThingID = UUID.randomUUID().toString();

        GatewayAPI4EndNode api = this.craeteGatewayAPI4EndNodeWithLoggedIn();

        this.addEmptyMockResponse(204);
        api.replaceEndNode(thingID, vendorThingID);

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals("/CUSTOM/apps/" + APP_ID + "/gateway/end-nodes/THING_ID:" + thingID, request.getPath());
        Assert.assertEquals("PUT", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        expectedRequestHeaders.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("vendorThingID", vendorThingID);
        this.assertRequestBody(expectedRequestBody, request);
    }

    @Test(expected = IllegalStateException.class)
    public void replaceEndNodeNoLoggedInTest() throws Exception {
        String thingID = "th." + UUID.randomUUID().toString();
        String vendorThingID =  UUID.randomUUID().toString();

        KiiApp app = getApp(APP_ID, APP_KEY);
        GatewayAPI4EndNode api = new GatewayAPI4EndNode(InstrumentationRegistry.getTargetContext(), app);
        api.replaceEndNode(thingID, vendorThingID);
    }
    @Test(expected = IllegalArgumentException.class)
    public void replaceEndNodeWithNullThingIDTest() throws Exception {
        String thingID = null;
        String vendorThingID =  UUID.randomUUID().toString();

        GatewayAPI4EndNode api = this.craeteGatewayAPI4EndNodeWithLoggedIn();

        this.addEmptyMockResponse(204);
        api.replaceEndNode(thingID, vendorThingID);
    }
    @Test(expected = IllegalArgumentException.class)
    public void replaceEndNodeWithEmptyThingIDTest() throws Exception {
        String thingID = "";
        String vendorThingID = UUID.randomUUID().toString();

        GatewayAPI4EndNode api = this.craeteGatewayAPI4EndNodeWithLoggedIn();

        this.addEmptyMockResponse(204);
        api.replaceEndNode(thingID, vendorThingID);
    }
    @Test(expected = IllegalArgumentException.class)
    public void replaceEndNodeWithNullVenderThingIDTest() throws Exception {
        String thingID = "th." + UUID.randomUUID().toString();
        String vendorThingID = null;

        GatewayAPI4EndNode api = this.craeteGatewayAPI4EndNodeWithLoggedIn();

        this.addEmptyMockResponse(204);
        api.replaceEndNode(thingID, vendorThingID);
    }
    @Test(expected = IllegalArgumentException.class)
    public void replaceEndNodeWithEmptyVenderThingIDTest() throws Exception {
        String thingID = "th." + UUID.randomUUID().toString();
        String vendorThingID = "";

        GatewayAPI4EndNode api = this.craeteGatewayAPI4EndNodeWithLoggedIn();

        this.addEmptyMockResponse(204);
        api.replaceEndNode(thingID, vendorThingID);
    }
    @Test
    public void replaceEndNode400ErrorTest() throws Exception {
        String thingID = "th." + UUID.randomUUID().toString();
        String vendorThingID = UUID.randomUUID().toString();

        GatewayAPI4EndNode api = this.craeteGatewayAPI4EndNodeWithLoggedIn();

        this.addEmptyMockResponse(400);
        try {
            api.replaceEndNode(thingID, vendorThingID);
            Assert.fail("BadRequestException should be thrown");
        } catch (BadRequestException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals("/CUSTOM/apps/" + APP_ID + "/gateway/end-nodes/THING_ID:" + thingID, request.getPath());
        Assert.assertEquals("PUT", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        expectedRequestHeaders.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("vendorThingID", vendorThingID);
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void replaceEndNode401ErrorTest() throws Exception {
        String thingID = "th." + UUID.randomUUID().toString();
        String vendorThingID = UUID.randomUUID().toString();

        GatewayAPI4EndNode api = this.craeteGatewayAPI4EndNodeWithLoggedIn();

        this.addEmptyMockResponse(401);
        try {
            api.replaceEndNode(thingID, vendorThingID);
            Assert.fail("UnauthorizedException should be thrown");
        } catch (UnauthorizedException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals("/CUSTOM/apps/" + APP_ID + "/gateway/end-nodes/THING_ID:" + thingID, request.getPath());
        Assert.assertEquals("PUT", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        expectedRequestHeaders.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("vendorThingID", vendorThingID);
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void replaceEndNode404ErrorTest() throws Exception {
        String thingID = "th." + UUID.randomUUID().toString();
        String vendorThingID = UUID.randomUUID().toString();

        GatewayAPI4EndNode api = this.craeteGatewayAPI4EndNodeWithLoggedIn();

        this.addEmptyMockResponse(404);
        try {
            api.replaceEndNode(thingID, vendorThingID);
            Assert.fail("NotFoundException should be thrown");
        } catch (NotFoundException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals("/CUSTOM/apps/" + APP_ID + "/gateway/end-nodes/THING_ID:" + thingID, request.getPath());
        Assert.assertEquals("PUT", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        expectedRequestHeaders.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("vendorThingID", vendorThingID);
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void replaceEndNode409ErrorTest() throws Exception {
        String thingID = "th." + UUID.randomUUID().toString();
        String vendorThingID = UUID.randomUUID().toString();

        GatewayAPI4EndNode api = this.craeteGatewayAPI4EndNodeWithLoggedIn();

        this.addEmptyMockResponse(409);
        try {
            api.replaceEndNode(thingID, vendorThingID);
            Assert.fail("ConflictException should be thrown");
        } catch (ConflictException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals("/CUSTOM/apps/" + APP_ID + "/gateway/end-nodes/THING_ID:" + thingID, request.getPath());
        Assert.assertEquals("PUT", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        expectedRequestHeaders.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("vendorThingID", vendorThingID);
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void replaceEndNode503ErrorTest() throws Exception {
        String thingID = "th." + UUID.randomUUID().toString();
        String vendorThingID = UUID.randomUUID().toString();

        GatewayAPI4EndNode api = this.craeteGatewayAPI4EndNodeWithLoggedIn();

        this.addEmptyMockResponse(503);
        try {
            api.replaceEndNode(thingID, vendorThingID);
            Assert.fail("ServiceUnavailableException should be thrown");
        } catch (ServiceUnavailableException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals("/CUSTOM/apps/" + APP_ID + "/gateway/end-nodes/THING_ID:" + thingID, request.getPath());
        Assert.assertEquals("PUT", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        expectedRequestHeaders.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("vendorThingID", vendorThingID);
        this.assertRequestBody(expectedRequestBody, request);
    }
}
