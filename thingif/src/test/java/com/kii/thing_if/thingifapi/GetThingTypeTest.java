package com.kii.thing_if.thingifapi;

import android.content.Context;

import com.kii.thing_if.StandaloneThing;
import com.kii.thing_if.Target;
import com.kii.thing_if.ThingIFAPI;
import com.kii.thing_if.ThingIFAPITestBase;
import com.kii.thing_if.TypedID;
import com.kii.thing_if.exception.ForbiddenException;
import com.kii.thing_if.exception.NotFoundException;
import com.kii.thing_if.exception.ServiceUnavailableException;
import com.kii.thing_if.exception.UnauthorizedException;
import com.squareup.okhttp.mockwebserver.MockResponse;
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
import java.util.concurrent.TimeUnit;

@RunWith(RobolectricTestRunner.class)
public class GetThingTypeTest extends ThingIFAPITestBase {

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

        String responseBody = "{\"thingType\" : \"dummyThingType\"}";
        MockResponse response = new MockResponse().setResponseCode(200);
        response.setBody(responseBody);
        this.server.enqueue(response);

        ThingIFAPI api = createDefaultThingIFAPIBuilder(this.context, APP_ID, APP_KEY)
                .setTarget(target)
                .build();

        String type = api.getThingType();

        Assert.assertEquals("dummyThingType", type);

        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/things/" + thingID.getID() + "/thing-type",
                request.getPath());
        Assert.assertEquals("GET", request.getMethod());

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

        String responseBody = "{ \"errorCode\": \"THING_WITHOUT_THING_TYPE\" }";
        MockResponse response = new MockResponse().setResponseCode(404);
        response.setBody(responseBody);
        this.server.enqueue(response);

        ThingIFAPI api = createDefaultThingIFAPIBuilder(this.context, APP_ID, APP_KEY)
                .setTarget(target)
                .build();

        String type = api.getThingType();

        Assert.assertNull(type);

        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/things/" + thingID.getID() + "/thing-type",
                request.getPath());
        Assert.assertEquals("GET", request.getMethod());

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
            api.getThingType();
            Assert.fail("UnauthorizedException should be thrown");
        } catch (UnauthorizedException e) {
            // Expected.
        } catch (Exception e) {
            Assert.fail("Unexpected exception: " + e.getMessage());
        }

        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/things/" + thingID.getID() + "/thing-type",
                request.getPath());
        Assert.assertEquals("GET", request.getMethod());

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
            api.getThingType();
            Assert.fail("ForbiddenException should be thrown");
        } catch (ForbiddenException e) {
            // Expected.
        } catch (Exception e) {
            Assert.fail("Unexpected exception: " + e.getMessage());
        }

        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/things/" + thingID.getID() + "/thing-type",
                request.getPath());
        Assert.assertEquals("GET", request.getMethod());

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

        String responseBody = "{ \"errorCode\": \"THING_NOT_FOUND\" }";
        MockResponse response = new MockResponse().setResponseCode(404);
        response.setBody(responseBody);
        this.server.enqueue(response);

        ThingIFAPI api = createDefaultThingIFAPIBuilder(this.context, APP_ID, APP_KEY)
                .setTarget(target)
                .build();

        try {
            api.getThingType();
            Assert.fail("NotFoundException should be thrown");
        } catch (NotFoundException e) {
            // Expected.
        } catch (Exception e) {
            Assert.fail("Unexpected exception: " + e.getMessage());
        }

        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/things/" + thingID.getID() + "/thing-type",
                request.getPath());
        Assert.assertEquals("GET", request.getMethod());

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
            api.getThingType();
            Assert.fail("ServiceUnavailableException should be thrown");
        } catch (ServiceUnavailableException e) {
            // Expected.
        } catch (Exception e) {
            Assert.fail("Unexpected exception: " + e.getMessage());
        }

        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/things/" + thingID.getID() + "/thing-type",
                request.getPath());
        Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
}
