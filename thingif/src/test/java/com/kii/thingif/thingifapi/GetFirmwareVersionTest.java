package com.kii.thingif.thingifapi;

import android.content.Context;

import com.kii.thingif.StandaloneThing;
import com.kii.thingif.Target;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.ThingIFAPITestBase;
import com.kii.thingif.TypedID;
import com.kii.thingif.exception.ForbiddenException;
import com.kii.thingif.exception.NotFoundException;
import com.kii.thingif.exception.ServiceUnavailableException;
import com.kii.thingif.exception.UnauthorizedException;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RunWith(RobolectricTestRunner.class)
public class GetFirmwareVersionTest extends ThingIFAPITestBase {

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
    public void successTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        String responseBody = "{\"_firmwareVersion\" : \"V1\"}";
        MockResponse response = new MockResponse().setResponseCode(200);
        response.setBody(responseBody);
        this.server.enqueue(response);

        ThingIFAPI api = createDefaultThingIFAPIBuilder(this.context, APP_ID, APP_KEY)
                .setTarget(target)
                .build();

        String fv = api.getFirmwareVersion();

        Assert.assertEquals("V1", fv);

        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals(API_BASE_PATH + "/things/" + thingID.getID(), request.getPath());
        org.junit.Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }

    @Test
    public void successNullTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        String responseBody = "{ }";
        MockResponse response = new MockResponse().setResponseCode(200);
        response.setBody(responseBody);
        this.server.enqueue(response);

        ThingIFAPI api = createDefaultThingIFAPIBuilder(this.context, APP_ID, APP_KEY)
                .setTarget(target)
                .build();

        String fv = api.getFirmwareVersion();

        Assert.assertNull(fv);

        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals(API_BASE_PATH + "/things/" + thingID.getID(), request.getPath());
        org.junit.Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }

    @Test
    public void error401Test() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        this.addEmptyMockResponse(401);

        ThingIFAPI api = createDefaultThingIFAPIBuilder(this.context, APP_ID, APP_KEY)
                .setTarget(target)
                .build();

        try {
            api.getFirmwareVersion();
        } catch (UnauthorizedException e) {
            // Expected.
        } catch (Exception e) {
            Assert.fail("Unexpected exception: " + e.getMessage());
        }

        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals(API_BASE_PATH + "/things/" + thingID.getID(), request.getPath());
        org.junit.Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }

    @Test
    public void error403Test() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        this.addEmptyMockResponse(403);

        ThingIFAPI api = createDefaultThingIFAPIBuilder(this.context, APP_ID, APP_KEY)
                .setTarget(target)
                .build();

        try {
            api.getFirmwareVersion();
        } catch (ForbiddenException e) {
            // Expected.
        } catch (Exception e) {
            Assert.fail("Unexpected exception: " + e.getMessage());
        }

        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals(API_BASE_PATH + "/things/" + thingID.getID(), request.getPath());
        org.junit.Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }

    @Test
    public void error404Test() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        this.addEmptyMockResponse(404);

        ThingIFAPI api = createDefaultThingIFAPIBuilder(this.context, APP_ID, APP_KEY)
                .setTarget(target)
                .build();

        try {
            api.getFirmwareVersion();
        } catch (NotFoundException e) {
            // Expected.
        } catch (Exception e) {
            Assert.fail("Unexpected exception: " + e.getMessage());
        }

        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals(API_BASE_PATH + "/things/" + thingID.getID(), request.getPath());
        org.junit.Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }

    @Test
    public void error503Test() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        this.addEmptyMockResponse(503);

        ThingIFAPI api = createDefaultThingIFAPIBuilder(this.context, APP_ID, APP_KEY)
                .setTarget(target)
                .build();

        try {
            api.getFirmwareVersion();
        } catch (ServiceUnavailableException e) {
            // Expected.
        } catch (Exception e) {
            Assert.fail("Unexpected exception: " + e.getMessage());
        }

        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals(API_BASE_PATH + "/things/" + thingID.getID(), request.getPath());
        org.junit.Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
}
