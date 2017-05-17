package com.kii.thingiftrait.thingifapi;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kii.thingiftrait.StandaloneThing;
import com.kii.thingiftrait.Target;
import com.kii.thingiftrait.TargetThing;
import com.kii.thingiftrait.ThingIFAPI;
import com.kii.thingiftrait.ThingIFAPITestBase;
import com.kii.thingiftrait.TypedID;
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
public class CopyWithTargetTest extends ThingIFAPITestBase {
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
    public void basicTest() throws Exception {
        String vendorThingID = "v1234567890abcde";
        String thingPassword = "password";
        String thingID = "th.1234567890";
        String accessToken = "thing-access-token-1234";
        this.addMockResponseForOnBoard(200, thingID, accessToken);

        ThingIFAPI api = this.createDefaultThingIFAPIBuilder(context, APP_ID, APP_KEY).
                setTag("ThingA").build();
        Assert.assertEquals(api.getTag(), "ThingA");
        Assert.assertFalse(api.onboarded());
        api.onboardWithVendorThingID(
                vendorThingID,
                thingPassword);

        String vendorThingID2 = "vabcde1234567890";
        TypedID thingID2 = new TypedID(TypedID.Types.THING, "th.9876543210");
        String accessToken2 = "thing-access-token-4321";
        Target target2 = new StandaloneThing(thingID2.getID(), vendorThingID2, accessToken2);

        ThingIFAPI copiedApi = api.copyWithTarget(target2, "ThingB");
        Assert.assertEquals(copiedApi.getTag(), "ThingB");
        Assert.assertNotEquals(api.hashCode(), copiedApi.hashCode());
        Assert.assertNotNull(api.getTarget());
        Assert.assertNotNull(copiedApi.getTarget());
        Assert.assertNotEquals(api.getTarget().hashCode(), copiedApi.getTarget().hashCode());
        Assert.assertEquals(api.getAppID(), copiedApi.getAppID());
        Assert.assertEquals(api.getAppKey(), copiedApi.getAppKey());
        Assert.assertEquals(api.getBaseUrl(), copiedApi.getBaseUrl());
        Assert.assertEquals(api.getOwner().getTypedID(), copiedApi.getOwner().getTypedID());
        Assert.assertEquals(api.getOwner().getAccessToken(), copiedApi.getOwner().getAccessToken());
        Assert.assertEquals(thingID2, copiedApi.getTarget().getTypedID());
        Assert.assertEquals(vendorThingID2, ((TargetThing)copiedApi.getTarget()).getVendorThingID());
        Assert.assertEquals("thing-access-token-4321", copiedApi.getTarget().getAccessToken());
        Assert.assertEquals(api.getActionTypes(), copiedApi.getActionTypes());
        Assert.assertEquals(api.getStateTypes(), copiedApi.getStateTypes());
    }
    @Test
    public void sameTargetTest() throws Exception {
        String vendorThingID = "v1234567890abcde";
        String thingPassword = "password";
        String thingID = "th.1234567890";
        String accessToken = "thing-access-token-1234";
        this.addMockResponseForOnBoard(200, thingID, accessToken);

        ThingIFAPI api = this.createDefaultThingIFAPIBuilder(context, APP_ID, APP_KEY).
                setTag("ThingA").build();
        Assert.assertEquals(api.getTag(), "ThingA");
        Assert.assertFalse(api.onboarded());
        Target target = api.onboardWithVendorThingID(vendorThingID, thingPassword);

        ThingIFAPI copiedApi =  api.copyWithTarget(target, "ThingA");
        Assert.assertEquals(copiedApi.getTag(), "ThingA");
        Assert.assertNotEquals(api.hashCode(), copiedApi.hashCode());
        Assert.assertNotNull(api.getTarget());
        Assert.assertNotNull(copiedApi.getTarget());
        Assert.assertEquals(api.getTarget().hashCode(), copiedApi.getTarget().hashCode());
        Assert.assertEquals(api.getAppID(), copiedApi.getAppID());
        Assert.assertEquals(api.getAppKey(), copiedApi.getAppKey());
        Assert.assertEquals(api.getBaseUrl(), copiedApi.getBaseUrl());
        Assert.assertEquals(api.getOwner().getTypedID(), copiedApi.getOwner().getTypedID());
        Assert.assertEquals(api.getOwner().getAccessToken(), copiedApi.getOwner().getAccessToken());
        Assert.assertEquals(api.getActionTypes(), copiedApi.getActionTypes());
        Assert.assertEquals(api.getStateTypes(), copiedApi.getStateTypes());

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ThingIFAPI.class, new ThingIFAPIAdapter())
                .create();
        Assert.assertEquals(gson.toJson(api), gson.toJson(copiedApi));
    }
    @Test
    public void copyWithTargetWithNullTagTest() throws Exception {
        String vendorThingID = "v1234567890abcde";
        String thingPassword = "password";
        String thingID = "th.1234567890";
        String accessToken = "thing-access-token-1234";
        this.addMockResponseForOnBoard(200, thingID, accessToken);

        ThingIFAPI api = this.createDefaultThingIFAPIBuilder(context, APP_ID, APP_KEY).
                setTag("ThingA").build();
        Assert.assertEquals(api.getTag(), "ThingA");
        Assert.assertFalse(api.onboarded());
        api.onboardWithVendorThingID(vendorThingID, thingPassword, null);

        String vendorThingID2 = "vabcde1234567890";
        TypedID thingID2 = new TypedID(TypedID.Types.THING, "th.9876543210");
        String accessToken2 = "thing-access-token-4321";
        Target target2 = new StandaloneThing(thingID2.getID(), vendorThingID2, accessToken2);

        ThingIFAPI copiedApi = api.copyWithTarget(target2, null);
        Assert.assertNull(copiedApi.getTag());
        Assert.assertNotEquals(api.hashCode(), copiedApi.hashCode());
        Assert.assertNotNull(api.getTarget());
        Assert.assertNotNull(copiedApi.getTarget());
        Assert.assertNotEquals(api.getTarget().hashCode(), copiedApi.getTarget().hashCode());
        Assert.assertEquals(api.getAppID(), copiedApi.getAppID());
        Assert.assertEquals(api.getAppKey(), copiedApi.getAppKey());
        Assert.assertEquals(api.getBaseUrl(), copiedApi.getBaseUrl());
        Assert.assertEquals(api.getOwner().getTypedID(), copiedApi.getOwner().getTypedID());
        Assert.assertEquals(api.getOwner().getAccessToken(), copiedApi.getOwner().getAccessToken());
        Assert.assertEquals(thingID2, copiedApi.getTarget().getTypedID());
        Assert.assertEquals(vendorThingID2, ((TargetThing)copiedApi.getTarget()).getVendorThingID());
        Assert.assertEquals("thing-access-token-4321", copiedApi.getTarget().getAccessToken());
        Assert.assertEquals(api.getActionTypes(), copiedApi.getActionTypes());
        Assert.assertEquals(api.getStateTypes(), copiedApi.getStateTypes());
    }
    @Test(expected = IllegalArgumentException.class)
    public void copyWithTargetWithNullTargetTest() throws Exception {
        String vendorThingID = "v1234567890abcde";
        String thingPassword = "password";
        JSONObject thingProperties = new JSONObject();
        thingProperties.put("manufacturer", "Kii");
        String thingID = "th.1234567890";
        String accessToken = "thing-access-token-1234";
        this.addMockResponseForOnBoard(200, thingID, accessToken);

        ThingIFAPI api = this.createDefaultThingIFAPI(context, APP_ID, APP_KEY);
        Assert.assertFalse(api.onboarded());
        api.onboardWithVendorThingID(vendorThingID, thingPassword, null);
        api.copyWithTarget(null, "ThingB");
    }
}
