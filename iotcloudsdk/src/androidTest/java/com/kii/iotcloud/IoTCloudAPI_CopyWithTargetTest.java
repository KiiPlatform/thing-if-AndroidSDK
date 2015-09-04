package com.kii.iotcloud;

import android.support.test.runner.AndroidJUnit4;

import com.google.gson.JsonParser;
import com.kii.iotcloud.internal.GsonRepository;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 */
@RunWith(AndroidJUnit4.class)
public class IoTCloudAPI_CopyWithTargetTest extends IoTCloudAPITestBase {
    @Test
    public void basicTest() throws Exception {
        String vendorThingID = "v1234567890abcde";
        String thingPassword = "password";
        JSONObject thingProperties = new JSONObject();
        thingProperties.put("manufacturer", "Kii");
        String thingID = "th.1234567890";
        String accessToken = "thing-access-token-1234";
        this.addMockResponseForOnBoard(200, thingID, accessToken);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        Assert.assertFalse(api.onboarded());
        Target target1 = api.onboard(vendorThingID, thingPassword, DEMO_THING_TYPE, thingProperties);

        TypedID thingID2 = new TypedID(TypedID.Types.THING, "th.9876543210");
        String accessToken2 = "thing-access-token-4321";
        Target target2 = new Target(thingID2, accessToken2);

        IoTCloudAPI copiedApi =  api.copyWithTarget(target2, "ThingB");
        Assert.assertNotEquals(api.hashCode(), copiedApi.hashCode());
        Assert.assertNotEquals(api.getTarget().hashCode(), copiedApi.getTarget().hashCode());
        assertEquals(api.getAppID(), copiedApi.getAppID());
        assertEquals(api.getAppKey(), copiedApi.getAppKey());
        assertEquals(api.getBaseUrl(), copiedApi.getBaseUrl());
        assertEquals(api.getOwner().getID(), copiedApi.getOwner().getID());
        assertEquals(api.getOwner().getAccessToken(), copiedApi.getOwner().getAccessToken());
        assertEquals(thingID2, copiedApi.getTarget().getID());
        assertEquals("thing-access-token-4321", copiedApi.getTarget().getAccessToken());
    }
    @Test
    public void sameTargetTest() throws Exception {
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

        IoTCloudAPI copiedApi =  api.copyWithTarget(target, "ThingB");
        Assert.assertNotEquals(api.hashCode(), copiedApi.hashCode());
        Assert.assertEquals(api.getTarget().hashCode(), copiedApi.getTarget().hashCode());
        assertEquals(api.getAppID(), copiedApi.getAppID());
        assertEquals(api.getAppKey(), copiedApi.getAppKey());
        assertEquals(api.getBaseUrl(), copiedApi.getBaseUrl());
        assertEquals(api.getOwner().getID(), copiedApi.getOwner().getID());
        assertEquals(api.getOwner().getAccessToken(), copiedApi.getOwner().getAccessToken());
        assertEquals(new JsonParser().parse(GsonRepository.gson().toJson(api)),
                new JsonParser().parse(GsonRepository.gson().toJson(copiedApi)));
    }
}