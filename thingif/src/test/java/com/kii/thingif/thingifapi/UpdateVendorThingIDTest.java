package com.kii.thingif.thingifapi;

import android.content.Context;

import com.google.gson.JsonObject;
import com.kii.thingif.StandaloneThing;
import com.kii.thingif.Target;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.ThingIFAPITestBase;
import com.kii.thingif.TypedID;
import com.kii.thingif.exception.BadRequestException;
import com.kii.thingif.exception.ConflictException;
import com.kii.thingif.thingifapi.utils.ThingIFAPIUtils;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

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
public class UpdateVendorThingIDTest extends ThingIFAPITestBase {
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
    public void updateVendorThingIDTest() throws Exception {
        String newVendorThingID = UUID.randomUUID().toString();
        String newPassword = "password999";
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);

        this.addEmptyMockResponse(204);

        api.updateVendorThingID(newVendorThingID, newPassword);

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(KII_CLOUD_BASE_PATH + "/things/" + thingID.getID() + "/vendor-thing-id", request.getPath());
        Assert.assertEquals("PUT", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.VendorThingIDUpdateRequest+json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("_vendorThingID", newVendorThingID);
        expectedRequestBody.addProperty("_password", newPassword);
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void updateVendorThingID400ErrorTest() throws Exception {
        String newVendorThingID = UUID.randomUUID().toString();
        String newPassword = "password999";
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);

        this.addEmptyMockResponse(400);

        try {
            api.updateVendorThingID(newVendorThingID, newPassword);
            Assert.fail("BadRequestException should be thrown");
        } catch (BadRequestException e) {
            // expected.
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(KII_CLOUD_BASE_PATH + "/things/" + thingID.getID() + "/vendor-thing-id", request.getPath());
        Assert.assertEquals("PUT", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.VendorThingIDUpdateRequest+json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("_vendorThingID", newVendorThingID);
        expectedRequestBody.addProperty("_password", newPassword);
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void updateVendorThingID409ErrorTest() throws Exception {
        String newVendorThingID = UUID.randomUUID().toString();
        String newPassword = "password999";
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);

        this.addEmptyMockResponse(409);

        try {
            api.updateVendorThingID(newVendorThingID, newPassword);
            Assert.fail("ConflictException should be thrown");
        } catch (ConflictException e) {
            // expected.
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(KII_CLOUD_BASE_PATH + "/things/" + thingID.getID() + "/vendor-thing-id", request.getPath());
        Assert.assertEquals("PUT", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.VendorThingIDUpdateRequest+json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("_vendorThingID", newVendorThingID);
        expectedRequestBody.addProperty("_password", newPassword);
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test(expected = IllegalArgumentException.class)
    public void updateVendorThingIDWithNullVendorThingIDTest() throws Exception {
        String newVendorThingID = null;
        String newPassword = "password999";
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);

        api.updateVendorThingID(newVendorThingID, newPassword);
    }
    @Test(expected = IllegalArgumentException.class)
    public void updateVendorThingIDWithEmptyVendorThingIDTest() throws Exception {
        String newVendorThingID = "";
        String newPassword = "password999";
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);

        api.updateVendorThingID(newVendorThingID, newPassword);
    }
    @Test(expected = IllegalArgumentException.class)
    public void updateVendorThingIDWithNullPasswordTest() throws Exception {
        String newVendorThingID = UUID.randomUUID().toString();
        String newPassword = null;
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);

        api.updateVendorThingID(newVendorThingID, newPassword);
    }
    @Test(expected = IllegalArgumentException.class)
    public void updateVendorThingIDWithEmptyPasswordTest() throws Exception {
        String newVendorThingID = UUID.randomUUID().toString();
        String newPassword = "";
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);

        api.updateVendorThingID(newVendorThingID, newPassword);
    }
}
