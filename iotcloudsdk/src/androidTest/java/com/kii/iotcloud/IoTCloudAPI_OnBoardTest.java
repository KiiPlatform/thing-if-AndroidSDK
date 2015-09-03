package com.kii.iotcloud;

import android.support.test.runner.AndroidJUnit4;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kii.iotcloud.exception.ForbiddenException;
import com.kii.iotcloud.exception.InternalServerErrorException;
import com.kii.iotcloud.exception.NotFoundException;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * https://github.com/KiiCorp/IoTCloud/blob/master/rest_api_spec/onboarding-swagger.yml
 */
@RunWith(AndroidJUnit4.class)
public class IoTCloudAPI_OnBoardTest extends IoTCloudAPITestBase {

    @Test
    public void onboardingWithVendorThingIDByOwnerTest() throws Exception {
        String vendorThingID = "v1234567890abcde";
        String thingPassword = "password";
        JSONObject thingProperties = new JSONObject();
        thingProperties.put("manufacturer", "Kii");
        String thingID = "th.1234567890";
        String accessToken = "thing-access-token-1234";
        this.addMockResponseForOnBoard(200, thingID, accessToken);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        Assert.assertFalse(api.onBoarded());
        Target target = api.onboard(vendorThingID, thingPassword, DEMO_THING_TYPE, thingProperties);
        Assert.assertTrue(api.onBoarded());

        // verify the result
        Assert.assertEquals(new TypedID(TypedID.Types.THING, thingID), target.getID());
        Assert.assertEquals(accessToken, target.getAccessToken());
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/onboardings", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.OnboardingWithVendorThingIDByOwner+json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("owner", api.getOwner().getID().toString());
        expectedRequestBody.addProperty("vendorThingID", vendorThingID);
        expectedRequestBody.addProperty("thingPassword", thingPassword);
        expectedRequestBody.addProperty("thingType", DEMO_THING_TYPE);
        expectedRequestBody.add("thingProperties", new JsonParser().parse(thingProperties.toString()));
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void onboardingWithVendorThingIDByOwner403ErrorTest() throws Exception {
        String vendorThingID = "v1234567890abcde";
        String thingPassword = "password";
        JSONObject thingProperties = new JSONObject();
        thingProperties.put("manufacturer", "Kii");
        String thingID = "th.1234567890";
        String accessToken = "thing-access-token-1234";
        this.addEmptyMockResponse(403);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        try {
            api.onboard(vendorThingID, thingPassword, DEMO_THING_TYPE, thingProperties);
            Assert.fail("IoTCloudRestException should be thrown");
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
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.OnboardingWithVendorThingIDByOwner+json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("owner", api.getOwner().getID().toString());
        expectedRequestBody.addProperty("vendorThingID", vendorThingID);
        expectedRequestBody.addProperty("thingPassword", thingPassword);
        expectedRequestBody.addProperty("thingType", DEMO_THING_TYPE);
        expectedRequestBody.add("thingProperties", new JsonParser().parse(thingProperties.toString()));
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void onboardingWithVendorThingIDByOwner404ErrorTest() throws Exception {
        String vendorThingID = "v1234567890abcde";
        String thingPassword = "password";
        JSONObject thingProperties = new JSONObject();
        thingProperties.put("manufacturer", "Kii");
        String thingID = "th.1234567890";
        String accessToken = "thing-access-token-1234";
        this.addEmptyMockResponse(404);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        try {
            api.onboard(vendorThingID, thingPassword, DEMO_THING_TYPE, thingProperties);
            Assert.fail("IoTCloudRestException should be thrown");
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
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.OnboardingWithVendorThingIDByOwner+json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("owner", api.getOwner().getID().toString());
        expectedRequestBody.addProperty("vendorThingID", vendorThingID);
        expectedRequestBody.addProperty("thingPassword", thingPassword);
        expectedRequestBody.addProperty("thingType", DEMO_THING_TYPE);
        expectedRequestBody.add("thingProperties", new JsonParser().parse(thingProperties.toString()));
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void onboardingWithVendorThingIDByOwner500ErrorTest() throws Exception {
        String vendorThingID = "v1234567890abcde";
        String thingPassword = "password";
        JSONObject thingProperties = new JSONObject();
        thingProperties.put("manufacturer", "Kii");
        String thingID = "th.1234567890";
        String accessToken = "thing-access-token-1234";
        this.addEmptyMockResponse(500);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        try {
            api.onboard(vendorThingID, thingPassword, DEMO_THING_TYPE, thingProperties);
            Assert.fail("IoTCloudRestException should be thrown");
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
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.OnboardingWithVendorThingIDByOwner+json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("owner", api.getOwner().getID().toString());
        expectedRequestBody.addProperty("vendorThingID", vendorThingID);
        expectedRequestBody.addProperty("thingPassword", thingPassword);
        expectedRequestBody.addProperty("thingType", DEMO_THING_TYPE);
        expectedRequestBody.add("thingProperties", new JsonParser().parse(thingProperties.toString()));
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardingWithVendorThingIDByOwnerWithNullVendorThingIDTest() throws Exception {
        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        Target target = api.onboard(null, "password", DEMO_THING_TYPE, null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardingWithVendorThingIDByOwnerWithEmptyVendorThingIDTest() throws Exception {
        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        Target target = api.onboard("", "password", DEMO_THING_TYPE, null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardingWithVendorThingIDByOwnerWithNullVendorThingPasswordTest() throws Exception {
        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        Target target = api.onboard("v1234567890abcde", null, DEMO_THING_TYPE, null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardingWithVendorThingIDByOwnerWithEmptyVendorThingPasswordTest() throws Exception {
        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        Target target = api.onboard("v1234567890abcde", "", DEMO_THING_TYPE, null);
    }
    @Test
    public void onboardingWithThingIDByOwnerTest() throws Exception {
        String thingID = "th.1234567890";
        String thingPassword = "password";
        String accessToken = "thing-access-token-1234";
        this.addMockResponseForOnBoard(200, thingID, accessToken);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        Assert.assertFalse(api.onBoarded());
        Target target = api.onboard(thingID, thingPassword);
        Assert.assertTrue(api.onBoarded());

        // verify the result
        Assert.assertEquals(new TypedID(TypedID.Types.THING, thingID), target.getID());
        Assert.assertEquals(accessToken, target.getAccessToken());
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/onboardings", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.OnboardingWithThingIDByOwner+json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("owner", api.getOwner().getID().toString());
        expectedRequestBody.addProperty("thingID", thingID);
        expectedRequestBody.addProperty("thingPassword", thingPassword);
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void onboardingWithThingIDByOwner403ErrorTest() throws Exception {
        String thingID = "th.1234567890";
        String thingPassword = "password";
        String accessToken = "thing-access-token-1234";
        this.addMockResponseForOnBoard(403, null, null);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        try {
            api.onboard(thingID, thingPassword);
            Assert.fail("IoTCloudRestException should be thrown");
        } catch (ForbiddenException e) {
        }
    }
    @Test
    public void onboardingWithThingIDByOwner404ErrorTest() throws Exception {
        String thingID = "th.1234567890";
        String thingPassword = "password";
        String accessToken = "thing-access-token-1234";
        this.addMockResponseForOnBoard(404, null, null);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        try {
            api.onboard(thingID, thingPassword);
            Assert.fail("IoTCloudRestException should be thrown");
        } catch (NotFoundException e) {
        }
    }
    @Test
    public void onboardingWithThingIDByOwner500ErrorTest() throws Exception {
        String thingID = "th.1234567890";
        String thingPassword = "password";
        String accessToken = "thing-access-token-1234";
        this.addMockResponseForOnBoard(500, null, null);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        try {
            api.onboard(thingID, thingPassword);
            Assert.fail("IoTCloudRestException should be thrown");
        } catch (InternalServerErrorException e) {
        }
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardingWithThingIDByOwnerTestWithNullThingIDTest() throws Exception {
        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        Target target = api.onboard(null, "password");
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardingWithThingIDByOwnerTestWithEmptyThingIDTest() throws Exception {
        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        Target target = api.onboard("", "password");
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardingWithThingIDByOwnerTestWithNullPasswordTest() throws Exception {
        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        Target target = api.onboard("th.1234567890", null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardingWithThingIDByOwnerTestWithEmptyPasswordTest() throws Exception {
        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        Target target = api.onboard("th.1234567890", "");
    }
}
