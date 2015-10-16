package com.kii.thingif;

import android.support.test.runner.AndroidJUnit4;

import com.google.gson.JsonParser;
import com.kii.thingif.testschemas.LightState;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * https://github.com/KiiCorp/IoTCloud/blob/master/rest_api_spec/states-endpoint.yaml
 */
@RunWith(AndroidJUnit4.class)
public class ThingIFAPI_TargetStateTest extends IoTCloudAPITestBase {
    @Test
    public void basicTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new Target(thingID, accessToken);

        String responseBody =
                "{" +
                        "\"power\":true," +
                        "\"brightness\":90," +
                        "\"color\":[255, 0, 128]," +
                        "\"colorTemperature\":30" +
                "}";
        this.addMockResponse(200, new JsonParser().parse(responseBody));

        ThingIFAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        api.setTarget(target);
        LightState lightState = api.getTargetState(LightState.class);
        // verify the result
        Assert.assertEquals(true, lightState.power);
        Assert.assertEquals(90, lightState.brightness);
        Assert.assertArrayEquals(new int[]{255, 0, 128}, lightState.color);
        Assert.assertEquals(30, lightState.colorTemperature);
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
    public void getTargetStateWithNullTargetTest() throws Exception {
        ThingIFAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        api.getTargetState(LightState.class);
    }
    @Test(expected = IllegalArgumentException.class)
    public void getTargetStateWithNullClassTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new Target(thingID, accessToken);
        ThingIFAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        api.setTarget(target);
        api.getTargetState(null);
    }
}
