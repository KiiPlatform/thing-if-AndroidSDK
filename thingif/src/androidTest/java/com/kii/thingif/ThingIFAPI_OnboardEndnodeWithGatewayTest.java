package com.kii.thingif;

import android.support.test.runner.AndroidJUnit4;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kii.thingif.exception.ForbiddenException;
import com.kii.thingif.exception.InternalServerErrorException;
import com.kii.thingif.exception.NotFoundException;
import com.kii.thingif.gateway.EndNode;
import com.kii.thingif.gateway.Gateway;
import com.kii.thingif.gateway.PendingEndNode;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class ThingIFAPI_OnboardEndnodeWithGatewayTest extends ThingIFAPITestBase {
    @Test
    public void onboardEndnodeWithGatewayTest() throws Exception {
        String gatewayThingID = UUID.randomUUID().toString();
        String gatewayVendorThingID = UUID.randomUUID().toString();
        String vendorThingID = UUID.randomUUID().toString();
        String thingPassword = "password";
        JSONObject thingProperties = new JSONObject();
        thingProperties.put("manufacturer", "Kii");
        String thingID = "th.1234567890";
        String accessToken = "thing-access-token-1234";
        this.addMockResponseForOnBoardEndnode(200, thingID, accessToken);

        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        api.setTarget(new Gateway(gatewayThingID, gatewayVendorThingID));
        Assert.assertTrue(api.onboarded());
        EndNode target = api.onboardEndnodeWithGateway(new PendingEndNode(vendorThingID, DEMO_THING_TYPE, thingProperties), thingPassword);
        Assert.assertTrue(api.onboarded());

        // verify the result
        Assert.assertEquals(new TypedID(TypedID.Types.THING, thingID), target.getTypedID());
        Assert.assertEquals(accessToken, target.getAccessToken());
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/onboardings", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.OnboardingEndNodeWithGatewayThingID+json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("owner", api.getOwner().getTypedID().toString());
        expectedRequestBody.addProperty("gatewayThingID", gatewayThingID);
        expectedRequestBody.addProperty("endNodeVendorThingID", vendorThingID);
        expectedRequestBody.addProperty("endNodePassword", thingPassword);
        expectedRequestBody.addProperty("endNodeThingType", DEMO_THING_TYPE);
        expectedRequestBody.add("endNodeThingProperties", new JsonParser().parse(thingProperties.toString()));
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void onboardEndnodeWithGateway403ErrorTest() throws Exception {
        String gatewayThingID = UUID.randomUUID().toString();
        String gatewayVendorThingID = UUID.randomUUID().toString();
        String vendorThingID = UUID.randomUUID().toString();
        String thingPassword = "password";
        JSONObject thingProperties = new JSONObject();
        thingProperties.put("manufacturer", "Kii");
        this.addEmptyMockResponse(403);

        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        api.setTarget(new Gateway(gatewayThingID, gatewayVendorThingID));
        try {
            api.onboardEndnodeWithGateway(new PendingEndNode(vendorThingID, DEMO_THING_TYPE, thingProperties), thingPassword);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (ForbiddenException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/onboardings", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.OnboardingEndNodeWithGatewayThingID+json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("owner", api.getOwner().getTypedID().toString());
        expectedRequestBody.addProperty("gatewayThingID", gatewayThingID);
        expectedRequestBody.addProperty("endNodeVendorThingID", vendorThingID);
        expectedRequestBody.addProperty("endNodePassword", thingPassword);
        expectedRequestBody.addProperty("endNodeThingType", DEMO_THING_TYPE);
        expectedRequestBody.add("endNodeThingProperties", new JsonParser().parse(thingProperties.toString()));
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void onboardEndnodeWithGateway404ErrorTest() throws Exception {
        String gatewayThingID = UUID.randomUUID().toString();
        String gatewayVendorThingID = UUID.randomUUID().toString();
        String vendorThingID = UUID.randomUUID().toString();
        String thingPassword = "password";
        JSONObject thingProperties = new JSONObject();
        thingProperties.put("manufacturer", "Kii");
        this.addEmptyMockResponse(404);

        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        api.setTarget(new Gateway(gatewayThingID, gatewayVendorThingID));
        try {
            api.onboardEndnodeWithGateway(new PendingEndNode(vendorThingID, DEMO_THING_TYPE, thingProperties), thingPassword);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (NotFoundException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/onboardings", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.OnboardingEndNodeWithGatewayThingID+json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("owner", api.getOwner().getTypedID().toString());
        expectedRequestBody.addProperty("gatewayThingID", gatewayThingID);
        expectedRequestBody.addProperty("endNodeVendorThingID", vendorThingID);
        expectedRequestBody.addProperty("endNodePassword", thingPassword);
        expectedRequestBody.addProperty("endNodeThingType", DEMO_THING_TYPE);
        expectedRequestBody.add("endNodeThingProperties", new JsonParser().parse(thingProperties.toString()));
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void onboardEndnodeWithGateway500ErrorTest() throws Exception {
        String gatewayThingID = UUID.randomUUID().toString();
        String gatewayVendorThingID = UUID.randomUUID().toString();
        String vendorThingID = UUID.randomUUID().toString();
        String thingPassword = "password";
        JSONObject thingProperties = new JSONObject();
        thingProperties.put("manufacturer", "Kii");
        this.addEmptyMockResponse(500);

        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        api.setTarget(new Gateway(gatewayThingID, gatewayVendorThingID));
        try {
            api.onboardEndnodeWithGateway(new PendingEndNode(vendorThingID, DEMO_THING_TYPE, thingProperties), thingPassword);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (InternalServerErrorException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/onboardings", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.OnboardingEndNodeWithGatewayThingID+json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("owner", api.getOwner().getTypedID().toString());
        expectedRequestBody.addProperty("gatewayThingID", gatewayThingID);
        expectedRequestBody.addProperty("endNodeVendorThingID", vendorThingID);
        expectedRequestBody.addProperty("endNodePassword", thingPassword);
        expectedRequestBody.addProperty("endNodeThingType", DEMO_THING_TYPE);
        expectedRequestBody.add("endNodeThingProperties", new JsonParser().parse(thingProperties.toString()));
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test(expected = IllegalStateException.class)
    public void onboardEndnodeWithGatewayWithoutOnboardingGatewayTest() throws Exception {
        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        Target target = api.onboardEndnodeWithGateway(new PendingEndNode("v1234567890abcde", DEMO_THING_TYPE), "password");
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardEndnodeWithGatewayWithNullVendorThingIDTest() throws Exception {
        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        api.setTarget(new Gateway("gateway-thing-id", "gateway-vendor-thing-id"));
        Target target = api.onboardEndnodeWithGateway(new PendingEndNode(null, DEMO_THING_TYPE, null), "password");
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardEndnodeWithGatewayWithEmptyVendorThingIDTest() throws Exception {
        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        api.setTarget(new Gateway("gateway-thing-id", "gateway-vendor-thing-id"));
        Target target = api.onboardEndnodeWithGateway(new PendingEndNode("", DEMO_THING_TYPE, null), "password");
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardEndnodeWithGatewayWithNullVendorThingPasswordTest() throws Exception {
        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        api.setTarget(new Gateway("gateway-thing-id", "gateway-vendor-thing-id"));
        Target target = api.onboardEndnodeWithGateway(new PendingEndNode("v1234567890abcde", DEMO_THING_TYPE, null), null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardEndnodeWithGatewayWithEmptyVendorThingPasswordTest() throws Exception {
        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        api.setTarget(new Gateway("gateway-thing-id", "gateway-vendor-thing-id"));
        Target target = api.onboardEndnodeWithGateway(new PendingEndNode("v1234567890abcde", DEMO_THING_TYPE, null), "");
    }
}
