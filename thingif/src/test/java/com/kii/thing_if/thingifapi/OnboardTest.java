package com.kii.thing_if.thingifapi;

import android.content.Context;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kii.thing_if.LayoutPosition;
import com.kii.thing_if.OnboardWithThingIDOptions;
import com.kii.thing_if.OnboardWithVendorThingIDOptions;
import com.kii.thing_if.Target;
import com.kii.thing_if.ThingIFAPI;
import com.kii.thing_if.ThingIFAPITestBase;
import com.kii.thing_if.TypedID;
import com.kii.thing_if.exception.ForbiddenException;
import com.kii.thing_if.exception.InternalServerErrorException;
import com.kii.thing_if.exception.NotFoundException;
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
import java.util.concurrent.TimeUnit;

@RunWith(RobolectricTestRunner.class)
public class OnboardTest extends ThingIFAPITestBase {
    protected static final String APP_ID = "smalltest";
    protected static final String APP_KEY = "abcdefghijklmnopqrstuvwxyz123456789";
    protected static final String BASE_PATH = "/thing-if/apps/" + APP_ID;
    protected static final String DEMO_THING_TYPE = "LED";
    private Context context;

    @Before
    public void before() throws Exception{
        this.context = RuntimeEnvironment.application.getApplicationContext();
        this.server = new MockWebServer();
        this.server.start();
    }

    @After
    public void after() throws Exception {
        this.server.shutdown();
    }
    @Test
    public void onboardWithVendorThingIDByOwnerTest() throws Exception {
        String vendorThingID = "v1234567890abcde";
        String thingPassword = "password";
        String thingID = "th.1234567890";
        String accessToken = "thing-access-token-1234";
        this.addMockResponseForOnBoard(200, thingID, accessToken);
        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        Assert.assertFalse(api.onboarded());
        Target target = api.onboardWithVendorThingID(vendorThingID, thingPassword);
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
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.OnboardingWithVendorThingIDByOwner+json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("owner", api.getOwner().getTypedID().toString());
        expectedRequestBody.addProperty("vendorThingID", vendorThingID);
        expectedRequestBody.addProperty("thingPassword", thingPassword);
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void onboardWithVendorThingIDByOwner403ErrorTest() throws Exception {
        String vendorThingID = "v1234567890abcde";
        String thingPassword = "password";
        this.addEmptyMockResponse(403);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        try {
            api.onboardWithVendorThingID(vendorThingID, thingPassword);
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
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.OnboardingWithVendorThingIDByOwner+json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("owner", api.getOwner().getTypedID().toString());
        expectedRequestBody.addProperty("vendorThingID", vendorThingID);
        expectedRequestBody.addProperty("thingPassword", thingPassword);
        this.assertRequestBody(expectedRequestBody, request);
    }

    @Test
    public void onboardWithVendorThingIDByOwner404ErrorTest() throws Exception {
        String vendorThingID = "v1234567890abcde";
        String thingPassword = "password";
        this.addEmptyMockResponse(404);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        try {
            api.onboardWithVendorThingID(vendorThingID, thingPassword);
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
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.OnboardingWithVendorThingIDByOwner+json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("owner", api.getOwner().getTypedID().toString());
        expectedRequestBody.addProperty("vendorThingID", vendorThingID);
        expectedRequestBody.addProperty("thingPassword", thingPassword);
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void onboardWithVendorThingIDByOwner500ErrorTest() throws Exception {
        String vendorThingID = "v1234567890abcde";
        String thingPassword = "password";
        JSONObject thingProperties = new JSONObject();
        thingProperties.put("manufacturer", "Kii");
        this.addEmptyMockResponse(500);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        try {
            api.onboardWithVendorThingID(vendorThingID, thingPassword);
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
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.OnboardingWithVendorThingIDByOwner+json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("owner", api.getOwner().getTypedID().toString());
        expectedRequestBody.addProperty("vendorThingID", vendorThingID);
        expectedRequestBody.addProperty("thingPassword", thingPassword);
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void onboardTwiceTest() throws Exception {
        String vendorThingID = "v1234567890abcde";
        String thingPassword = "password";
        String thingID = "th.1234567890";
        String accessToken = "thing-access-token-1234";
        this.addMockResponseForOnBoard(200, thingID, accessToken);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context,APP_ID, APP_KEY);
        Assert.assertFalse(api.onboarded());
        Target target = api.onboardWithVendorThingID(vendorThingID, thingPassword);
        Assert.assertNotNull(target);
        Assert.assertTrue(api.onboarded());
        try {
            api.onboardWithThingID(vendorThingID, thingPassword);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (IllegalStateException e) {
        }
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardWithVendorThingIDByOwnerWithNullVendorThingIDTest() throws Exception {
        ThingIFAPI api = this.createDefaultThingIFAPI(this.context,APP_ID, APP_KEY);
        Target target = api.onboardWithVendorThingID(null, "password");
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardWithVendorThingIDByOwnerWithEmptyVendorThingIDTest() throws Exception {
        ThingIFAPI api = this.createDefaultThingIFAPI(this.context,APP_ID, APP_KEY);
        Target target = api.onboardWithVendorThingID("", "password");
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardWithVendorThingIDByOwnerWithNullVendorThingPasswordTest() throws Exception {
        ThingIFAPI api = this.createDefaultThingIFAPI(this.context,APP_ID, APP_KEY);
        Target target = api.onboardWithVendorThingID("v1234567890abcde", null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardWithVendorThingIDByOwnerWithEmptyVendorThingPasswordTest() throws Exception {
        ThingIFAPI api = this.createDefaultThingIFAPI(this.context,APP_ID, APP_KEY);
        Target target = api.onboardWithVendorThingID("v1234567890abcde", "");
    }
    @Test
    public void onboardWithThingIDByOwnerTest() throws Exception {
        String thingID = "th.1234567890";
        String thingPassword = "password";
        String accessToken = "thing-access-token-1234";
        this.addMockResponseForOnBoard(200, thingID, accessToken);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context,APP_ID, APP_KEY);
        Assert.assertFalse(api.onboarded());
        Target target = api.onboardWithThingID(thingID, thingPassword);
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
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.OnboardingWithThingIDByOwner+json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("owner", api.getOwner().getTypedID().toString());
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

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context,APP_ID, APP_KEY);
        try {
            api.onboardWithThingID(thingID, thingPassword);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (ForbiddenException e) {
        }
    }
    @Test
    public void onboardWithThingIDByOwner404ErrorTest() throws Exception {
        String thingID = "th.1234567890";
        String thingPassword = "password";
        String accessToken = "thing-access-token-1234";
        this.addMockResponseForOnBoard(404, null, null);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context,APP_ID, APP_KEY);
        try {
            api.onboardWithThingID(thingID, thingPassword);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (NotFoundException e) {
        }
    }
    @Test
    public void onboardWithThingIDByOwner500ErrorTest() throws Exception {
        String thingID = "th.1234567890";
        String thingPassword = "password";
        String accessToken = "thing-access-token-1234";
        this.addMockResponseForOnBoard(500, null, null);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context,APP_ID, APP_KEY);
        try {
            api.onboardWithThingID(thingID, thingPassword);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (InternalServerErrorException e) {
        }
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardWithThingIDByOwnerTestWithNullThingIDTest() throws Exception {
        ThingIFAPI api = this.createDefaultThingIFAPI(this.context,APP_ID, APP_KEY);
        Target target = api.onboardWithThingID(null, "password");
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardWithThingIDByOwnerTestWithEmptyThingIDTest() throws Exception {
        ThingIFAPI api = this.createDefaultThingIFAPI(this.context,APP_ID, APP_KEY);
        Target target = api.onboardWithThingID("", "password");
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardWithThingIDByOwnerTestWithNullPasswordTest() throws Exception {
        ThingIFAPI api = this.createDefaultThingIFAPI(this.context,APP_ID, APP_KEY);
        Target target = api.onboardWithThingID("th.1234567890", null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardWithThingIDByOwnerTestWithEmptyPasswordTest() throws Exception {
        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        Target target = api.onboardWithThingID("th.1234567890", "");
    }

    @Test
    public void onboardWithVendorThingIDAndOptionsTest() throws Exception {
        String vendorThingID = "v1234567890abcde";
        String thingPassword = "password";
        JSONObject thingProperties = new JSONObject();
        thingProperties.put("manufacturer", "Kii");
        String thingID = "th.1234567890";
        String accessToken = "thing-access-token-1234";
        OnboardWithVendorThingIDOptions.Builder options = new OnboardWithVendorThingIDOptions.Builder();
        options.setThingType(DEMO_THING_TYPE)
                .setFirmwareVersion("dummyVersion")
                .setThingProperties(thingProperties)
                .setLayoutPosition(LayoutPosition.STANDALONE);
        this.addMockResponseForOnBoard(200, thingID, accessToken);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        Assert.assertFalse(api.onboarded());
        Target target = api.onboardWithVendorThingID(vendorThingID, thingPassword, options.build());
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
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.OnboardingWithVendorThingIDByOwner+json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("owner", api.getOwner().getTypedID().toString());
        expectedRequestBody.addProperty("vendorThingID", vendorThingID);
        expectedRequestBody.addProperty("thingPassword", thingPassword);
        expectedRequestBody.addProperty("thingType", DEMO_THING_TYPE);
        expectedRequestBody.addProperty("firmwareVersion", "dummyVersion");
        expectedRequestBody.add("thingProperties", new JsonParser().parse(thingProperties.toString()));
        expectedRequestBody.addProperty("layoutPosition", "STANDALONE");
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void onboardWithVendorThingIDAndOptions403ErrorTest() throws Exception {
        String vendorThingID = "v1234567890abcde";
        String thingPassword = "password";
        JSONObject thingProperties = new JSONObject();
        thingProperties.put("manufacturer", "Kii");
        OnboardWithVendorThingIDOptions.Builder options = new OnboardWithVendorThingIDOptions.Builder();
        options.setThingType(DEMO_THING_TYPE)
                .setThingProperties(thingProperties)
                .setLayoutPosition(LayoutPosition.GATEWAY);
        this.addEmptyMockResponse(403);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        try {
            api.onboardWithVendorThingID(vendorThingID, thingPassword, options.build());
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
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.OnboardingWithVendorThingIDByOwner+json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("owner", api.getOwner().getTypedID().toString());
        expectedRequestBody.addProperty("vendorThingID", vendorThingID);
        expectedRequestBody.addProperty("thingPassword", thingPassword);
        expectedRequestBody.addProperty("thingType", DEMO_THING_TYPE);
        expectedRequestBody.add("thingProperties", new JsonParser().parse(thingProperties.toString()));
        expectedRequestBody.addProperty("layoutPosition", "GATEWAY");
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void onboardWithVendorThingIDAndOptions404ErrorTest() throws Exception {
        String vendorThingID = "v1234567890abcde";
        String thingPassword = "password";
        JSONObject thingProperties = new JSONObject();
        thingProperties.put("manufacturer", "Kii");
        OnboardWithVendorThingIDOptions.Builder options = new OnboardWithVendorThingIDOptions.Builder();
        options.setThingType(DEMO_THING_TYPE)
                .setThingProperties(thingProperties)
                .setLayoutPosition(LayoutPosition.ENDNODE);
        this.addEmptyMockResponse(404);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        try {
            api.onboardWithVendorThingID(vendorThingID, thingPassword, options.build());
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
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.OnboardingWithVendorThingIDByOwner+json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("owner", api.getOwner().getTypedID().toString());
        expectedRequestBody.addProperty("vendorThingID", vendorThingID);
        expectedRequestBody.addProperty("thingPassword", thingPassword);
        expectedRequestBody.addProperty("thingType", DEMO_THING_TYPE);
        expectedRequestBody.add("thingProperties", new JsonParser().parse(thingProperties.toString()));
        expectedRequestBody.addProperty("layoutPosition", "ENDNODE");
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void onboardWithVendorThingIDAndOptions500ErrorTest() throws Exception {
        String vendorThingID = "v1234567890abcde";
        String thingPassword = "password";
        JSONObject thingProperties = new JSONObject();
        thingProperties.put("manufacturer", "Kii");
        OnboardWithVendorThingIDOptions.Builder options = new OnboardWithVendorThingIDOptions.Builder();
        options.setThingType(DEMO_THING_TYPE)
                .setThingProperties(thingProperties)
                .setLayoutPosition(LayoutPosition.STANDALONE);
        this.addEmptyMockResponse(500);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        try {
            api.onboardWithVendorThingID(vendorThingID, thingPassword, options.build());
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
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.OnboardingWithVendorThingIDByOwner+json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("owner", api.getOwner().getTypedID().toString());
        expectedRequestBody.addProperty("vendorThingID", vendorThingID);
        expectedRequestBody.addProperty("thingPassword", thingPassword);
        expectedRequestBody.addProperty("thingType", DEMO_THING_TYPE);
        expectedRequestBody.add("thingProperties", new JsonParser().parse(thingProperties.toString()));
        expectedRequestBody.addProperty("layoutPosition", "STANDALONE");
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void onboardWithVendorThingIDAndOptionsTwiceTest() throws Exception {
        String vendorThingID = "v1234567890abcde";
        String thingPassword = "password";
        JSONObject thingProperties = new JSONObject();
        thingProperties.put("manufacturer", "Kii");
        String thingID = "th.1234567890";
        String accessToken = "thing-access-token-1234";
        OnboardWithVendorThingIDOptions.Builder options = new OnboardWithVendorThingIDOptions.Builder();
        options.setThingType(DEMO_THING_TYPE)
                .setThingProperties(thingProperties);
        this.addMockResponseForOnBoard(200, thingID, accessToken);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        Assert.assertFalse(api.onboarded());
        Target target = api.onboardWithVendorThingID(vendorThingID, thingPassword, options.build());
        Assert.assertNotNull(target);
        Assert.assertTrue(api.onboarded());
        try {
            api.onboardWithVendorThingID(vendorThingID, thingPassword, options.build());
            Assert.fail("IllegalStateException should be thrown");
        } catch (IllegalStateException e) {
        }
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardWithVendorThingIDAndOptionsWithNullVendorThingIDTest() throws Exception {
        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        Target target = api.onboardWithVendorThingID(null, "password", (OnboardWithVendorThingIDOptions) null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardWithVendorThingIDAndOptionsWithEmptyVendorThingIDTest() throws Exception {
        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        Target target = api.onboardWithVendorThingID("", "password", (OnboardWithVendorThingIDOptions) null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardWithVendorThingIDAndOptionsWithNullVendorThingPasswordTest() throws Exception {
        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        Target target = api.onboardWithVendorThingID("v1234567890abcde", null, (OnboardWithVendorThingIDOptions) null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardWithVendorThingIDAndOptionsWithEmptyVendorThingPasswordTest() throws Exception {
        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        Target target = api.onboardWithVendorThingID("v1234567890abcde", "", (OnboardWithVendorThingIDOptions) null);
    }
    @Test
    public void onboardWithThingIDAndOptionsTest() throws Exception {
        String thingID = "th.1234567890";
        String thingPassword = "password";
        String accessToken = "thing-access-token-1234";
        OnboardWithThingIDOptions.Builder options = new OnboardWithThingIDOptions.Builder();
        options.setLayoutPosition(LayoutPosition.STANDALONE);
        this.addMockResponseForOnBoard(200, thingID, accessToken);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        Assert.assertFalse(api.onboarded());
        Target target = api.onboardWithThingID(thingID, thingPassword, options.build());
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
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.OnboardingWithThingIDByOwner+json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("owner", api.getOwner().getTypedID().toString());
        expectedRequestBody.addProperty("thingID", thingID);
        expectedRequestBody.addProperty("layoutPosition", "STANDALONE");
        expectedRequestBody.addProperty("thingPassword", thingPassword);
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test(expected = ForbiddenException.class)
    public void onboardWithThingIDAndOptions403ErrorTest() throws Exception {
        this.addMockResponseForOnBoard(403, null, null);
        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        api.onboardWithThingID("th.1234567890", "password", (OnboardWithThingIDOptions) null);
    }
    @Test(expected = NotFoundException.class)
    public void onboardWithThingIDAndOptions404ErrorTest() throws Exception {
        this.addMockResponseForOnBoard(404, null, null);
        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        api.onboardWithThingID("th.1234567890", "password", (OnboardWithThingIDOptions) null);
    }
    @Test(expected = InternalServerErrorException.class)
    public void onboardWithThingIDAndOptions500ErrorTest() throws Exception {
        this.addMockResponseForOnBoard(500, null, null);
        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        api.onboardWithThingID("th.1234567890", "password", (OnboardWithThingIDOptions) null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardWithThingIDAndOptionsTestWithNullThingIDTest() throws Exception {
        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        Target target = api.onboardWithThingID(null, "password", (OnboardWithThingIDOptions) null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardWithThingIDAndOptionsTestWithEmptyThingIDTest() throws Exception {
        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        Target target = api.onboardWithThingID("", "password", (OnboardWithThingIDOptions) null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardWithThingIDAndOptionsTestWithNullPasswordTest() throws Exception {
        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        Target target = api.onboardWithThingID("th.1234567890", null, (OnboardWithThingIDOptions) null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void onboardWithThingIDAndOptionsTestWithEmptyPasswordTest() throws Exception {
        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        Target target = api.onboardWithThingID("th.1234567890", "", (OnboardWithThingIDOptions) null);
    }
}
