package com.kii.thing_if.thingifapi;

import android.content.Context;

import com.kii.thing_if.ThingIFAPI;
import com.kii.thing_if.ThingIFAPITestBase;
import com.kii.thing_if.TypedID;
import com.kii.thing_if.exception.ForbiddenException;
import com.kii.thing_if.exception.InternalServerErrorException;
import com.kii.thing_if.exception.NotFoundException;
import com.kii.thing_if.gateway.EndNode;
import com.kii.thing_if.gateway.Gateway;
import com.kii.thing_if.gateway.PendingEndNode;
import com.kii.thing_if.thingifapi.utils.ThingIFAPIUtils;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RunWith(RobolectricTestRunner.class)
public class OnboardEndnodeWithGateway extends ThingIFAPITestBase {
    private Context context;

    @Before
    public void before() throws Exception {
        context = RuntimeEnvironment.application.getApplicationContext();
        this.server = new MockWebServer();
        this.server.start();
    }

    @After
    public void after() throws Exception {
        this.server.shutdown();
    }
    @Test
    public void onboardEndNodeWithGatewayTest() throws Exception {
        String gatewayThingID = UUID.randomUUID().toString();
        String gatewayVendorThingID = UUID.randomUUID().toString();
        String vendorThingID = UUID.randomUUID().toString();
        String thingType = "type1";
        String firmwareVersion = "v1";
        String thingPassword = "password";
        JSONObject thingProperties = new JSONObject();
        thingProperties.put("manufacturer", "Kii");
        String thingID = "th.1234567890";
        String accessToken = "thing-access-token-1234";
        this.addMockResponseForOnBoardEndnode(200, thingID, accessToken);

        ThingIFAPI api = this.createDefaultThingIFAPI(context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, new Gateway(gatewayThingID, gatewayVendorThingID));
        Assert.assertTrue(api.onboarded());
        EndNode target = api.onboardEndNodeWithGateway(
                new PendingEndNode(
                        vendorThingID, 
                        thingType, 
                        firmwareVersion, 
                        thingProperties), 
                thingPassword);
        Assert.assertTrue(api.onboarded());

        // verify the result
        Assert.assertEquals(new TypedID(TypedID.Types.THING, thingID), target.getTypedID());
        Assert.assertEquals(accessToken, target.getAccessToken());
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/onboardings", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.OnboardingEndNodeWithGatewayThingID+json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("owner", api.getOwner().getTypedID().toString());
        expectedRequestBody.put("gatewayThingID", gatewayThingID);
        expectedRequestBody.put("endNodeVendorThingID", vendorThingID);
        expectedRequestBody.put("endNodePassword", thingPassword);
        expectedRequestBody.put("endNodeThingType", thingType);
        expectedRequestBody.put("endNodeFirmwareVersion", firmwareVersion);
        expectedRequestBody.put("endNodeThingProperties", thingProperties);
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void onboardEndNodeWithGateway403ErrorTest() throws Exception {
        String gatewayThingID = UUID.randomUUID().toString();
        String gatewayVendorThingID = UUID.randomUUID().toString();
        String vendorThingID = UUID.randomUUID().toString();
        String thingPassword = "password";
        JSONObject thingProperties = new JSONObject();
        thingProperties.put("manufacturer", "Kii");
        this.addEmptyMockResponse(403);

        ThingIFAPI api = this.createDefaultThingIFAPI(context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, new Gateway(gatewayThingID, gatewayVendorThingID));
        try {
            api.onboardEndNodeWithGateway(new PendingEndNode(vendorThingID, "type1", null, thingProperties), thingPassword);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (ForbiddenException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/onboardings", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.OnboardingEndNodeWithGatewayThingID+json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("owner", api.getOwner().getTypedID().toString());
        expectedRequestBody.put("gatewayThingID", gatewayThingID);
        expectedRequestBody.put("endNodeVendorThingID", vendorThingID);
        expectedRequestBody.put("endNodePassword", thingPassword);
        expectedRequestBody.put("endNodeThingType", "type1");
        expectedRequestBody.put("endNodeThingProperties", thingProperties);
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void onboardEndNodeWithGateway404ErrorTest() throws Exception {
        String gatewayThingID = UUID.randomUUID().toString();
        String gatewayVendorThingID = UUID.randomUUID().toString();
        String vendorThingID = UUID.randomUUID().toString();
        String thingPassword = "password";
        JSONObject thingProperties = new JSONObject();
        thingProperties.put("manufacturer", "Kii");
        this.addEmptyMockResponse(404);

        ThingIFAPI api = this.createDefaultThingIFAPI(context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, new Gateway(gatewayThingID, gatewayVendorThingID));
        try {
            api.onboardEndNodeWithGateway(new PendingEndNode(vendorThingID, "type1", null, thingProperties), thingPassword);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (NotFoundException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/onboardings", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.OnboardingEndNodeWithGatewayThingID+json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("owner", api.getOwner().getTypedID().toString());
        expectedRequestBody.put("gatewayThingID", gatewayThingID);
        expectedRequestBody.put("endNodeVendorThingID", vendorThingID);
        expectedRequestBody.put("endNodePassword", thingPassword);
        expectedRequestBody.put("endNodeThingType", "type1");
        expectedRequestBody.put("endNodeThingProperties", thingProperties);
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void onboardEndNodeWithGateway500ErrorTest() throws Exception {
        String gatewayThingID = UUID.randomUUID().toString();
        String gatewayVendorThingID = UUID.randomUUID().toString();
        String vendorThingID = UUID.randomUUID().toString();
        String thingPassword = "password";
        JSONObject thingProperties = new JSONObject();
        thingProperties.put("manufacturer", "Kii");
        this.addEmptyMockResponse(500);

        ThingIFAPI api = this.createDefaultThingIFAPI(context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, new Gateway(gatewayThingID, gatewayVendorThingID));
        try {
            api.onboardEndNodeWithGateway(new PendingEndNode(vendorThingID, "type1", null, thingProperties), thingPassword);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (InternalServerErrorException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/onboardings", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.OnboardingEndNodeWithGatewayThingID+json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("owner", api.getOwner().getTypedID().toString());
        expectedRequestBody.put("gatewayThingID", gatewayThingID);
        expectedRequestBody.put("endNodeVendorThingID", vendorThingID);
        expectedRequestBody.put("endNodePassword", thingPassword);
        expectedRequestBody.put("endNodeThingType", "type1");
        expectedRequestBody.put("endNodeThingProperties", thingProperties);
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test(expected = IllegalStateException.class)
    public void onboardEndNodeWithGatewayWithoutOnboardingGatewayTest() throws Exception {
        ThingIFAPI api = this.createDefaultThingIFAPI(context, APP_ID, APP_KEY);
        api.onboardEndNodeWithGateway(new PendingEndNode("v1234567890abcde", "type1"), "password");
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardEndNodeWithGatewayWithNullVendorThingIDTest() throws Exception {
        ThingIFAPI api = this.createDefaultThingIFAPI(context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, new Gateway("gateway-thing-id", "gateway-vendor-thing-id"));
        api.onboardEndNodeWithGateway(new PendingEndNode(null, "type1", null, null), "password");
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardEndNodeWithGatewayWithEmptyVendorThingIDTest() throws Exception {
        ThingIFAPI api = this.createDefaultThingIFAPI(context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, new Gateway("gateway-thing-id", "gateway-vendor-thing-id"));
        api.onboardEndNodeWithGateway(new PendingEndNode("", "type1", null, null), "password");
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardEndNodeWithGatewayWithNullVendorThingPasswordTest() throws Exception {
        ThingIFAPI api = this.createDefaultThingIFAPI(context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, new Gateway("gateway-thing-id", "gateway-vendor-thing-id"));
        api.onboardEndNodeWithGateway(new PendingEndNode("v1234567890abcde", "type1", null, null), null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardEndNodeWithGatewayWithEmptyVendorThingPasswordTest() throws Exception {
        ThingIFAPI api = this.createDefaultThingIFAPI(context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, new Gateway("gateway-thing-id", "gateway-vendor-thing-id"));
        api.onboardEndNodeWithGateway(new PendingEndNode("v1234567890abcde", "type1", null, null), "");
    }
}
