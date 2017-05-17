package com.kii.thingiftrait.thingifapi;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.kii.thingiftrait.ThingIFAPI;
import com.kii.thingiftrait.ThingIFAPITestBase;
import com.kii.thingiftrait.exception.StoredInstanceNotFoundException;
import com.kii.thingiftrait.exception.UnloadableInstanceVersionException;
import com.kii.thingiftrait.internal.gson.ThingIFAPIAdapter;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public class StoredInstanceTest extends ThingIFAPITestBase {

    private Context context;

    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(ThingIFAPI.class, new ThingIFAPIAdapter())
            .create();

    @Before
    public void before() throws Exception{
        this.context = RuntimeEnvironment.application.getApplicationContext();
        this.clearSharedPreferences(context);
        this.server = new MockWebServer();
        this.server.start();
    }

    @After
    public void after() throws Exception {
        this.server.shutdown();
    }

    @Test
    public void loadFromStoredInstanceTest() throws Exception {

        String vendorThingID = "v1234567890abcde";
        String thingPassword = "password";
        String thingID = "th.1234567890";
        String accessToken = "thing-access-token-1234";
        this.addMockResponseForOnBoard(200, thingID, accessToken);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        Assert.assertFalse(api.onboarded());
        api.onboardWithVendorThingID(vendorThingID, thingPassword);
        Assert.assertTrue(api.onboarded());

        ThingIFAPI restoredApi = ThingIFAPI.loadFromStoredInstance(this.context);

        Assert.assertEquals(api.getAppID(), restoredApi.getAppID());
        Assert.assertEquals(api.getAppKey(), restoredApi.getAppKey());
        Assert.assertEquals(api.getBaseUrl(), restoredApi.getBaseUrl());
        Assert.assertEquals(api.getOwner().getTypedID(), restoredApi.getOwner().getTypedID());
        Assert.assertEquals(api.getOwner().getAccessToken(), restoredApi.getOwner().getAccessToken());
        Assert.assertNotNull(api.getTarget());
        Assert.assertNotNull(restoredApi.getTarget());
        Assert.assertEquals(api.getTarget().getTypedID(), restoredApi.getTarget().getTypedID());
        Assert.assertEquals(api.getTarget().getAccessToken(), restoredApi.getTarget().getAccessToken());
        Assert.assertEquals(api.getActionTypes(), getDefaultActionTypes());
        Assert.assertEquals(api.getStateTypes(), getDefaultStateTypes());
        Assert.assertEquals(api.getActionTypes(), restoredApi.getActionTypes());
        Assert.assertEquals(api.getStateTypes(), restoredApi.getStateTypes());
        Assert.assertEquals(new JsonParser().parse(gson.toJson(api)),
                new JsonParser().parse(gson.toJson(restoredApi)));

        ThingIFAPI.removeAllStoredInstances();
        try {
            ThingIFAPI.loadFromStoredInstance(this.context, "ThingB");
            Assert.fail("StoredInstanceNotFoundException should be thrown");
        } catch (StoredInstanceNotFoundException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void loadFromStoredInstanceWithTagTest() throws Exception {
        String vendorThingID = "v1234567890abcde";
        String thingPassword = "password";
        String thingID = "th.1234567890";
        String accessToken = "thing-access-token-1234";
        this.addMockResponseForOnBoard(200, thingID, accessToken);

        ThingIFAPI.Builder builder = this
                .createDefaultThingIFAPIBuilder(this.context, APP_ID, APP_KEY)
                .setTag("ThingA");
        ThingIFAPI api = builder.build();
        Assert.assertFalse(api.onboarded());
        api.onboardWithVendorThingID(vendorThingID, thingPassword);
        Assert.assertTrue(api.onboarded());

        ThingIFAPI restoredApi = ThingIFAPI.loadFromStoredInstance(context, "ThingA");

        Assert.assertEquals(api.getAppID(), restoredApi.getAppID());
        Assert.assertEquals(api.getAppKey(), restoredApi.getAppKey());
        Assert.assertEquals(api.getBaseUrl(), restoredApi.getBaseUrl());
        Assert.assertEquals(api.getOwner().getTypedID(), restoredApi.getOwner().getTypedID());
        Assert.assertEquals(api.getOwner().getAccessToken(), restoredApi.getOwner().getAccessToken());
        Assert.assertNotNull(api.getTarget());
        Assert.assertNotNull(restoredApi.getTarget());
        Assert.assertEquals(api.getTarget().getTypedID(), restoredApi.getTarget().getTypedID());
        Assert.assertEquals(api.getTarget().getAccessToken(), restoredApi.getTarget().getAccessToken());
        Assert.assertEquals(api.getActionTypes(), getDefaultActionTypes());
        Assert.assertEquals(api.getStateTypes(), getDefaultStateTypes());
        Assert.assertEquals(api.getActionTypes(), restoredApi.getActionTypes());
        Assert.assertEquals(api.getStateTypes(), restoredApi.getStateTypes());
        Assert.assertEquals(new JsonParser().parse(gson.toJson(api)),
                new JsonParser().parse(gson.toJson(restoredApi)));

        try {
            ThingIFAPI.loadFromStoredInstance(this.context, "ThingB");
            Assert.fail("StoredInstanceNotFoundException should be thrown");
        } catch (StoredInstanceNotFoundException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }
    @Test(expected = StoredInstanceNotFoundException.class)
    public void loadFromStoredInstanceWithoutStoredInstanceTest() throws Exception {
        ThingIFAPI.loadFromStoredInstance(this.context);
    }

    @Test(expected = UnloadableInstanceVersionException.class)
    public void loadFromStoredInstanceNoSDKVersionTest() throws Exception {
        String vendorThingID = "v1234567890abcde";
        String thingPassword = "password";
        String thingID = "th.1234567890";
        String accessToken = "thing-access-token-1234";
        this.addMockResponseForOnBoard(200, thingID, accessToken);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        Assert.assertFalse(api.onboarded());
        api.onboardWithVendorThingID(vendorThingID, thingPassword);
        Assert.assertTrue(api.onboarded());

        SharedPreferences preferences = context.getSharedPreferences(
                "com.kii.thingif.preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("ThingIFAPI_VERSION");
        editor.apply();

        ThingIFAPI.loadFromStoredInstance(context);
    }

    @Test(expected = UnloadableInstanceVersionException.class)
    public void loadFromStoredInstanceLowerSDKVersionTest() throws Exception {
        String vendorThingID = "v1234567890abcde";
        String thingPassword = "password";
        JSONObject thingProperties = new JSONObject();
        thingProperties.put("manufacturer", "Kii");
        String thingID = "th.1234567890";
        String accessToken = "thing-access-token-1234";
        this.addMockResponseForOnBoard(200, thingID, accessToken);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        Assert.assertFalse(api.onboarded());
        api.onboardWithVendorThingID(vendorThingID, thingPassword);
        Assert.assertTrue(api.onboarded());

        SharedPreferences preferences = context.getSharedPreferences(
                "com.kii.thingif.preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("ThingIFAPI_VERSION", "0.0.0");
        editor.apply();

        ThingIFAPI.loadFromStoredInstance(context);
    }

    @Test
    public void loadFromStoredInstanceUpperSDKVersionTest() throws Exception {
        String vendorThingID = "v1234567890abcde";
        String thingPassword = "password";
        JSONObject thingProperties = new JSONObject();
        thingProperties.put("manufacturer", "Kii");
        String thingID = "th.1234567890";
        String accessToken = "thing-access-token-1234";
        this.addMockResponseForOnBoard(200, thingID, accessToken);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        Assert.assertFalse(api.onboarded());
        api.onboardWithVendorThingID(vendorThingID, thingPassword);
        Assert.assertTrue(api.onboarded());

        SharedPreferences preferences = context.getSharedPreferences(
                "com.kii.thingif.preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("ThingIFAPI_VERSION", "1000.0.0");
        editor.apply();

        ThingIFAPI restoredApi = ThingIFAPI.loadFromStoredInstance(context);

        Assert.assertEquals(api.getAppID(), restoredApi.getAppID());
        Assert.assertEquals(api.getAppKey(), restoredApi.getAppKey());
        Assert.assertEquals(api.getBaseUrl(), restoredApi.getBaseUrl());
        Assert.assertEquals(api.getOwner().getTypedID(), restoredApi.getOwner().getTypedID());
        Assert.assertEquals(api.getOwner().getAccessToken(), restoredApi.getOwner().getAccessToken());
        Assert.assertNotNull(api.getTarget());
        Assert.assertNotNull(restoredApi.getTarget());
        Assert.assertEquals(api.getTarget().getTypedID(), restoredApi.getTarget().getTypedID());
        Assert.assertEquals(api.getTarget().getAccessToken(), restoredApi.getTarget().getAccessToken());
        Assert.assertEquals(api.getActionTypes(), getDefaultActionTypes());
        Assert.assertEquals(api.getStateTypes(), getDefaultStateTypes());
        Assert.assertEquals(api.getActionTypes(), restoredApi.getActionTypes());
        Assert.assertEquals(api.getStateTypes(), restoredApi.getStateTypes());
        Assert.assertEquals(new JsonParser().parse(gson.toJson(api)),
                new JsonParser().parse(gson.toJson(restoredApi)));

        ThingIFAPI.removeAllStoredInstances();
        try {
            ThingIFAPI.loadFromStoredInstance(this.context, "ThingB");
            Assert.fail("StoredInstanceNotFoundException should be thrown");
        } catch (StoredInstanceNotFoundException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }
}
