package com.kii.iotcloud;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.JsonParser;
import com.kii.iotcloud.exception.StoredIoTCloudAPIInstanceNotFoundException;
import com.kii.iotcloud.internal.GsonRepository;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class IoTCloudAPI_StoredInstanceTest extends IoTCloudAPITestBase {
    @Before
    public void before() throws Exception {
        super.before();
        this.clearSharedPreferences();
    }
    @Test
    public void loadFromStoredInstanceTest() throws Exception {
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

        IoTCloudAPI restoredApi = IoTCloudAPI.loadFromStoredInstance(InstrumentationRegistry.getTargetContext());

        assertEquals(api.getAppID(), restoredApi.getAppID());
        assertEquals(api.getAppKey(), restoredApi.getAppKey());
        assertEquals(api.getBaseUrl(), restoredApi.getBaseUrl());
        assertEquals(api.getOwner().getID(), restoredApi.getOwner().getID());
        assertEquals(api.getOwner().getAccessToken(), restoredApi.getOwner().getAccessToken());
        assertEquals(api.getTarget().getID(), restoredApi.getTarget().getID());
        assertEquals(api.getTarget().getAccessToken(), restoredApi.getTarget().getAccessToken());
        assertEquals(new JsonParser().parse(GsonRepository.gson().toJson(api)),
                new JsonParser().parse(GsonRepository.gson().toJson(restoredApi)));
    }
    @Test
    public void loadFromStoredInstanceWithTagTest() throws Exception {
        String vendorThingID = "v1234567890abcde";
        String thingPassword = "password";
        JSONObject thingProperties = new JSONObject();
        thingProperties.put("manufacturer", "Kii");
        String thingID = "th.1234567890";
        String accessToken = "thing-access-token-1234";
        this.addMockResponseForOnBoard(200, thingID, accessToken);

        IoTCloudAPIBuilder builder = this.craeteIoTCloudAPIBuilderWithDemoSchema(APP_ID, APP_KEY);
        IoTCloudAPI api = builder.build("ThingA");
        Assert.assertFalse(api.onboarded());
        Target target = api.onboard(vendorThingID, thingPassword, DEMO_THING_TYPE, thingProperties);
        Assert.assertTrue(api.onboarded());

        IoTCloudAPI restoredApi = IoTCloudAPI.loadFromStoredInstance(InstrumentationRegistry.getTargetContext(), "ThingA");

        assertEquals(api.getAppID(), restoredApi.getAppID());
        assertEquals(api.getAppKey(), restoredApi.getAppKey());
        assertEquals(api.getBaseUrl(), restoredApi.getBaseUrl());
        assertEquals(api.getOwner().getID(), restoredApi.getOwner().getID());
        assertEquals(api.getOwner().getAccessToken(), restoredApi.getOwner().getAccessToken());
        assertEquals(api.getTarget().getID(), restoredApi.getTarget().getID());
        assertEquals(api.getTarget().getAccessToken(), restoredApi.getTarget().getAccessToken());
        assertEquals(new JsonParser().parse(GsonRepository.gson().toJson(api)),
                new JsonParser().parse(GsonRepository.gson().toJson(restoredApi)));

        try {
            IoTCloudAPI.loadFromStoredInstance(InstrumentationRegistry.getTargetContext(), "ThingB");
            fail("StoredIoTCloudAPIInstanceNotFoundException should be thrown");
        } catch (StoredIoTCloudAPIInstanceNotFoundException e) {
        }
    }
    @Test(expected = StoredIoTCloudAPIInstanceNotFoundException.class)
    public void loadFromStoredInstanceWithoutStoredInstanceTest() throws Exception {
        IoTCloudAPI.loadFromStoredInstance(InstrumentationRegistry.getTargetContext());
    }
}
