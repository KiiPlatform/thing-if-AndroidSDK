package com.kii.thingif;

import android.content.Context;

import com.kii.thingif.states.AirConditionerState;
import com.kii.thingif.states.HumidityState;
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

/**
 * https://github.com/KiiCorp/ThingIF/blob/master/rest_api_spec/states-endpoint.yaml
 */
@RunWith(RobolectricTestRunner.class)
public class ThingIFAPI_TargetStateTest extends ThingIFAPITestBase {
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
    public void getTargetStateTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        String responseBody =
                "{" +
                        "\"" + ALIAS1 + "\":{" +
                                "\"power\":true," +
                                "\"currentTemperature\":30" +
                        "}," +
                        "\"" + ALIAS2 + "\":{" +
                                "\"currentHumidity\":80" +
                        "}" +
                "}";
        MockResponse response = new MockResponse().setResponseCode(200);
        response.setBody(responseBody);
        this.server.enqueue(response);

        ThingIFAPI api = createDefaultThingIFAPIBuilder(this.context, APP_ID, APP_KEY)
                .setTarget(target)
                .build();
        Map states = api.getTargetState();
        // verify the result
        Assert.assertNotNull(states);
        Assert.assertEquals(2, states.size());

        Assert.assertTrue(states.get(ALIAS1) instanceof AirConditionerState);
        AirConditionerState airState = (AirConditionerState) states.get(ALIAS1);
        Assert.assertTrue(airState.power);
        Assert.assertEquals(30, airState.currentTemperature);

        Assert.assertTrue(states.get(ALIAS2) instanceof HumidityState);
        HumidityState humState = (HumidityState) states.get(ALIAS2);
        Assert.assertEquals(80, humState.currentHumidity);

        // verify the 1st request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/states", request.getPath());
        org.junit.Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }

    @Test(expected = IllegalStateException.class)
    public void getTargetState_NullTargetTest() throws Exception {
        ThingIFAPI api = this.createDefaultThingIFAPIBuilder(this.context, APP_ID, APP_KEY).build();
        api.getTargetState();
    }

    @Test
    public void getTargetStateWithAliasTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        String responseBody =
                "{" +
                        "\"power\":false," +
                        "\"currentTemperature\":25" +
                "}";
        MockResponse response = new MockResponse().setResponseCode(200);
        response.setBody(responseBody);
        this.server.enqueue(response);

        ThingIFAPI api = createDefaultThingIFAPIBuilder(this.context, APP_ID, APP_KEY)
                .setTarget(target)
                .build();
        AirConditionerState state = api.getTargetState(ALIAS1);
        // verify the result
        Assert.assertNotNull(state);
        Assert.assertFalse(state.power);
        Assert.assertEquals(25, state.currentTemperature);

        // verify the 1st request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals(
                BASE_PATH + "/targets/" + thingID.toString() + "/states/aliases/" + ALIAS1,
                request.getPath());
        org.junit.Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }

    @Test(expected = IllegalStateException.class)
    public void getTargetStateWithAlias_NullTargetTest() throws Exception {
        ThingIFAPI api = this.createDefaultThingIFAPIBuilder(this.context, APP_ID, APP_KEY).build();
        api.getTargetState(ALIAS1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getTargetStateWithAlias_UnknownAliasTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);
        ThingIFAPI api = this.createDefaultThingIFAPIBuilder(this.context, APP_ID, APP_KEY)
                .setTarget(target)
                .build();

        api.getTargetState("unknown");
    }
}
