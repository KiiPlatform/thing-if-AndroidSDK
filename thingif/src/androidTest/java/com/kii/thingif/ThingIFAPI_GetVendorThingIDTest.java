package com.kii.thingif;

import android.support.test.runner.AndroidJUnit4;

import com.kii.thingif.exception.NotFoundException;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class ThingIFAPI_GetVendorThingIDTest extends ThingIFAPITestBase {

    @Test
    public void getVendorThingIDTest() throws Exception {
        String vendorThingID = UUID.randomUUID().toString();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        ThingIFAPI api = this.createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);

        this.addMockResponseForGetVendorThingID(200, vendorThingID);

        String result = api.getVendorThingID();
        Assert.assertEquals(vendorThingID, result);

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(KII_CLOUD_BASE_PATH + "/things/" + thingID.getID() + "/vendor-thing-id", request.getPath());
        Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
    @Test
    public void getVendorThingID404ErrorTest() throws Exception {
        String vendorThingID = UUID.randomUUID().toString();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        ThingIFAPI api = this.createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);

        this.addEmptyMockResponse(404);

        try {
            api.getVendorThingID();
            Assert.fail("NotFoundException should be thrown");
        } catch (NotFoundException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(KII_CLOUD_BASE_PATH + "/things/" + thingID.getID() + "/vendor-thing-id", request.getPath());
        Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
}
