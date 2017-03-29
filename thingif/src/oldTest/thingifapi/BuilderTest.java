package com.kii.thingif.thingifapi;

import android.content.Context;

import com.kii.thingif.KiiApp;
import com.kii.thingif.Owner;
import com.kii.thingif.Site;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.ThingIFAPITestBase;
import com.kii.thingif.TypedID;
import com.kii.thingif.actions.AirConditionerActions;
import com.kii.thingif.states.AirConditionerState;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public class BuilderTest extends ThingIFAPITestBase{
    private Context context;

    @Before
    public void before() throws Exception{
        this.context = RuntimeEnvironment.application.getApplicationContext();
    }
    @Test
    public void basicTest() throws Exception {
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
    public void newBuilderWithNullAppTest() throws Exception {
        ThingIFAPI.Builder.newBuilder(
                this.context,
                null,
                new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void newBuilderWithNullOwnerTest() throws Exception {
        ThingIFAPI.Builder.newBuilder(
                this.context,
                new KiiApp("appid", "appkey", Site.JP),
                null);
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
