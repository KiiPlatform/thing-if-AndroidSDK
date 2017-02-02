package com.kii.thingif.thingifapi;

import android.content.Context;

import com.kii.thingif.KiiApp;
import com.kii.thingif.Owner;
import com.kii.thingif.Site;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.ThingIFAPITestBase;
import com.kii.thingif.TypedID;
import com.kii.thingif.actions.AirConditionerActions;
import com.kii.thingif.actions.HumidityActions;
import com.kii.thingif.command.Action;
import com.kii.thingif.states.AirConditionerState;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.HashMap;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
public class BuilderTest extends ThingIFAPITestBase{
    private Context context;

    @Before
    public void before() throws Exception{
        this.context = RuntimeEnvironment.application.getApplicationContext();
    }
    @Test
    public void basicTest() throws Exception {
        Map<String, Class<? extends Action>> actionTypes = new HashMap<>();
        actionTypes.put("airConditionerAlias", AirConditionerActions.class);
        actionTypes.put("humidityAlias", HumidityActions.class);

        ThingIFAPI.Builder builder = ThingIFAPI.Builder.newBuilder(
                this.context,
                new KiiApp("appid", "appkey", Site.JP),
                new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"),
                getDefaultActionTypes(),
                getDefaultStateTypes());
        ThingIFAPI api = builder.build();

        Assert.assertEquals("appid", api.getAppID());
        Assert.assertEquals("appkey", api.getAppKey());
        Assert.assertEquals(Site.JP.getBaseUrl(), api.getBaseUrl());
        Assert.assertEquals(new TypedID(TypedID.Types.USER, "user1234"), api.getOwner().getTypedID());
        Assert.assertEquals("token", api.getOwner().getAccessToken());
        Assert.assertEquals(api.getActionTypes(), getDefaultActionTypes());
        Assert.assertEquals(api.getStateTypes(), getDefaultStateTypes());
    }

    @Test(expected = IllegalArgumentException.class)
    public void newBuilderWithNullContextTest() throws Exception {
        ThingIFAPI.Builder.newBuilder(
                null, 
                new KiiApp("appid", "appkey", Site.JP), 
                new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"));
    }
    @Test(expected = IllegalArgumentException.class)
    public void newBuilderWithNullAppIDTest() throws Exception {
        ThingIFAPI.Builder.newBuilder(
                this.context,
                new KiiApp(null, "appkey", Site.JP),
                new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"));
    }
    @Test(expected = IllegalArgumentException.class)
    public void newBuilderWithEmptyAppIDTest() throws Exception {
        ThingIFAPI.Builder.newBuilder(
                this.context,
                new KiiApp("", "appkey", Site.JP),
                new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"));
    }
    @Test(expected = IllegalArgumentException.class)
    public void newBuilderWithNullAppKeyTest() throws Exception {
        ThingIFAPI.Builder.newBuilder(
                this.context,
                new KiiApp("appid", null, Site.JP),
                new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"));
    }
    @Test(expected = IllegalArgumentException.class)
    public void newBuilderWithEmptyAppKeyTest() throws Exception {
        ThingIFAPI.Builder.newBuilder(
                this.context,
                new KiiApp("appid", "", Site.JP),
                new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"));
    }
    @Test(expected = IllegalArgumentException.class)
    public void newBuilderWithNullSiteTest() throws Exception {
        ThingIFAPI.Builder.newBuilder(
                this.context,
                new KiiApp("appid", "appkey", (Site) null),
                new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"));
    }
    @Test(expected = IllegalArgumentException.class)
    public void newBuilderWithNullOwnerTest() throws Exception {
        ThingIFAPI.Builder.newBuilder(
                this.context,
                new KiiApp("appid", "appkey", Site.JP), null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void newBuilderWithNullBaseUrlTest() throws Exception {
        ThingIFAPI.Builder.newBuilder(
                this.context,
                new KiiApp("appid", "appkey", (String)null),
                new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"));
    }
    @Test(expected = IllegalArgumentException.class)
    public void newBuilderWithEmptyBaseUrlTest() throws Exception {
        ThingIFAPI.Builder.newBuilder(
                this.context,
                new KiiApp("appid", "appkey", ""),
                new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"));
    }

    @Test(expected = IllegalStateException.class)
    public void buildWithoutActionTypesAndStateTypesTest() throws Exception {
        ThingIFAPI.Builder.newBuilder(
                this.context,
                new KiiApp("appid", "appkey", Site.JP),
                new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"))
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void buildWithoutStateTypesTest() throws Exception {
        ThingIFAPI.Builder.newBuilder(
                this.context,
                new KiiApp("appid", "appkey", Site.JP),
                new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"))
                .registerActions("airConditionerAlias", AirConditionerActions.class)
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void buildWithoutActionTypesTest() throws Exception {
        ThingIFAPI.Builder.newBuilder(
                this.context,
                new KiiApp("appid", "appkey", Site.JP),
                new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"))
                .registerTargetState("airConditionerAlias", AirConditionerState.class)
                .build();
    }

}
