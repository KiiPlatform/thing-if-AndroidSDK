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
    public void onboardWithVendorThingIDByOwnerTest() throws Exception {
        String vendorThingID = "v1234567890abcde";
        String thingPassword = "password";
        JSONObject thingProperties = new JSONObject();
        thingProperties.put("manufacturer", "Kii");
        String thingID = "th.1234567890";
        String accessToken = "thing-access-token-1234";
        this.addMockResponseForOnBoard(200, thingID, accessToken);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        Assert.assertFalse(api.onboarded());
        Target target = api.onboard(vendorThingID, thingPassword, DEMO_THING_TYPE, thingProperties);
        Assert.assertTrue(api.onboarded());

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
    public void onboardWithVendorThingIDByOwner403ErrorTest() throws Exception {
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
    public void onboardWithVendorThingIDByOwner404ErrorTest() throws Exception {
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
    public void onboardWithVendorThingIDByOwner500ErrorTest() throws Exception {
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
    @Test(expected = IllegalStateException.class)
    public void onboardTwiceTest() throws Exception {
        String vendorThingID = "v1234567890abcde";
        String thingPassword = "password";
        JSONObject thingProperties = new JSONObject();
        thingProperties.put("manufacturer", "Kii");
        String thingID = "th.1234567890";
        String accessToken = "thing-access-token-1234";
        this.addMockResponseForOnBoard(200, thingID, accessToken);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        Assert.assertFalse(api.onboarded());
        Target target = api.onboard(vendorThingID, thingPassword, DEMO_THING_TYPE, thingProperties);
        Assert.assertTrue(api.onboarded());
        target = api.onboard(vendorThingID, thingPassword, DEMO_THING_TYPE, thingProperties);
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardWithVendorThingIDByOwnerWithNullVendorThingIDTest() throws Exception {
        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        Target target = api.onboard(null, "password", DEMO_THING_TYPE, null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardWithVendorThingIDByOwnerWithEmptyVendorThingIDTest() throws Exception {
        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        Target target = api.onboard("", "password", DEMO_THING_TYPE, null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardWithVendorThingIDByOwnerWithNullVendorThingPasswordTest() throws Exception {
        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        Target target = api.onboard("v1234567890abcde", null, DEMO_THING_TYPE, null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardWithVendorThingIDByOwnerWithEmptyVendorThingPasswordTest() throws Exception {
        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        Target target = api.onboard("v1234567890abcde", "", DEMO_THING_TYPE, null);
    }
    @Test
    public void onboardWithThingIDByOwnerTest() throws Exception {
        String thingID = "th.1234567890";
        String thingPassword = "password";
        String accessToken = "thing-access-token-1234";
        this.addMockResponseForOnBoard(200, thingID, accessToken);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        Assert.assertFalse(api.onboarded());
        Target target = api.onboard(thingID, thingPassword);
        Assert.assertTrue(api.onboarded());

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
    public void onboardWithThingIDByOwner403ErrorTest() throws Exception {
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
    public void onboardWithThingIDByOwner404ErrorTest() throws Exception {
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
    public void onboardWithThingIDByOwner500ErrorTest() throws Exception {
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
    public void onboardWithThingIDByOwnerTestWithNullThingIDTest() throws Exception {
        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        Target target = api.onboard(null, "password");
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardWithThingIDByOwnerTestWithEmptyThingIDTest() throws Exception {
        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        Target target = api.onboard("", "password");
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardWithThingIDByOwnerTestWithNullPasswordTest() throws Exception {
        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        Target target = api.onboard("th.1234567890", null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardWithThingIDByOwnerTestWithEmptyPasswordTest() throws Exception {
        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        Target target = api.onboard("th.1234567890", "");
    }
}
