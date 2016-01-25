package com.kii.thingif;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.JsonParser;
import com.kii.thingif.exception.StoredThingIFAPIInstanceNotFoundException;
import com.kii.thingif.internal.GsonRepository;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ThingIFAPI_StoredInstanceTest extends ThingIFAPITestBase {
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

        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        Assert.assertFalse(api.onboarded());
        Target target = api.onboard(vendorThingID, thingPassword, DEMO_THING_TYPE, thingProperties);
        Assert.assertTrue(api.onboarded());

        ThingIFAPI restoredApi = ThingIFAPI.loadFromStoredInstance(InstrumentationRegistry.getTargetContext());

        assertEquals(api.getAppID(), restoredApi.getAppID());
        assertEquals(api.getAppKey(), restoredApi.getAppKey());
        assertEquals(api.getBaseUrl(), restoredApi.getBaseUrl());
        assertEquals(api.getOwner().getTypedID(), restoredApi.getOwner().getTypedID());
        assertEquals(api.getOwner().getAccessToken(), restoredApi.getOwner().getAccessToken());
        assertEquals(api.getTarget().getTypedID(), restoredApi.getTarget().getTypedID());
        assertEquals(api.getTarget().getAccessToken(), restoredApi.getTarget().getAccessToken());
        assertEquals(new JsonParser().parse(GsonRepository.gson().toJson(api)),
                new JsonParser().parse(GsonRepository.gson().toJson(restoredApi)));

        ThingIFAPI.removeAllStoredInstances();
        try {
            ThingIFAPI.loadFromStoredInstance(InstrumentationRegistry.getTargetContext(), "ThingB");
            fail("StoredThingIFAPIInstanceNotFoundException should be thrown");
        } catch (StoredThingIFAPIInstanceNotFoundException e) {
        }
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

        ThingIFAPIBuilder builder = this.craeteThingIFAPIBuilderWithDemoSchema(APP_ID, APP_KEY).
                setTag("ThingA");
        ThingIFAPI api = builder.build();
        Assert.assertFalse(api.onboarded());
        Target target = api.onboard(vendorThingID, thingPassword, DEMO_THING_TYPE, thingProperties);
        Assert.assertTrue(api.onboarded());

        ThingIFAPI restoredApi = ThingIFAPI.loadFromStoredInstance(InstrumentationRegistry.getTargetContext(), "ThingA");

        assertEquals(api.getAppID(), restoredApi.getAppID());
        assertEquals(api.getAppKey(), restoredApi.getAppKey());
        assertEquals(api.getBaseUrl(), restoredApi.getBaseUrl());
        assertEquals(api.getOwner().getTypedID(), restoredApi.getOwner().getTypedID());
        assertEquals(api.getOwner().getAccessToken(), restoredApi.getOwner().getAccessToken());
        assertEquals(api.getTarget().getTypedID(), restoredApi.getTarget().getTypedID());
        assertEquals(api.getTarget().getAccessToken(), restoredApi.getTarget().getAccessToken());
        assertEquals(new JsonParser().parse(GsonRepository.gson().toJson(api)),
                new JsonParser().parse(GsonRepository.gson().toJson(restoredApi)));

        try {
            ThingIFAPI.loadFromStoredInstance(InstrumentationRegistry.getTargetContext(), "ThingB");
            fail("StoredThingIFAPIInstanceNotFoundException should be thrown");
        } catch (StoredThingIFAPIInstanceNotFoundException e) {
        }
    }
    @Test(expected = StoredThingIFAPIInstanceNotFoundException.class)
    public void loadFromStoredInstanceWithoutStoredInstanceTest() throws Exception {
        ThingIFAPI.loadFromStoredInstance(InstrumentationRegistry.getTargetContext());
    }
}
