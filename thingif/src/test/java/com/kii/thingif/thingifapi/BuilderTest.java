package com.kii.thingif.thingifapi;

import android.content.Context;

import com.kii.thingif.KiiApp;
import com.kii.thingif.Owner;
import com.kii.thingif.Site;
import com.kii.thingif.TargetState;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.ThingIFAPITestBase;
import com.kii.thingif.TypedID;
import com.kii.thingif.actions.SetPresetHumidity;
import com.kii.thingif.actions.SetPresetTemperature;
import com.kii.thingif.actions.TurnPower;
import com.kii.thingif.command.Action;
import com.kii.thingif.internal.utils.AliasUtils;
import com.kii.thingif.states.AirConditionerState;
import com.kii.thingif.states.HumidityState;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.HashMap;
import java.util.Map;

class EmptyNameAction implements Action {
    private Boolean power;
    public EmptyNameAction(Boolean power) {
        this.power = power;
    }
    @Override
    public String getActionName() {
        return "";
    }

    public Boolean getPower() {
        return this.power;
    }
}

class NullNameAction implements Action {
    private Boolean power;
    public NullNameAction(Boolean power) {
        this.power = power;
    }
    @Override
    public String getActionName() {
        return null;
    }

    public Boolean getPower() {
        return this.power;
    }
}

class EmptyAction implements Action {
    @Override
    public String getActionName() {
        return "turnPower";
    }
}

@RunWith(RobolectricTestRunner.class)
public class BuilderTest extends ThingIFAPITestBase{

    class InnerEmptyAction implements Action {
        @Override
        public String getActionName() {
            return "turnPower";
        }
    }

    static class InnerStaticEmptyAction implements Action {
        @Override
        public String getActionName() {
            return "turnPower";
        }
    }

    class InnerTurnPower implements Action {
        private Boolean power;
        public InnerTurnPower(Boolean power) {
            this.power = power;
        }

        public Boolean getPower() {
            return this.power;
        }

        @Override
        public String getActionName() {
            return "turnPower";
        }
    }

    static class InnerStaticTurnPower implements Action {
        private Boolean power;
        public InnerStaticTurnPower(Boolean power) {
            this.power = power;
        }
        public Boolean getPower() {
            return this.power;
        }
        @Override
        public String getActionName() {
            return "turnPower";
        }
    }

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
                new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"))
                .registerAction("alias1", "turnPower", TurnPower.class)
                .registerAction("alias1", "setPresetTemperature", SetPresetTemperature.class)
                .registerAction("alias2", "setPresetHumidity", SetPresetHumidity.class)
                .registerTargetState("alias1", AirConditionerState.class)
                .registerTargetState("alias2", HumidityState.class);
        ThingIFAPI api = builder.build();

        Assert.assertEquals("appid", api.getAppID());
        Assert.assertEquals("appkey", api.getAppKey());
        Assert.assertEquals(Site.JP.getBaseUrl(), api.getBaseUrl());
        Assert.assertEquals(new TypedID(TypedID.Types.USER, "user1234"), api.getOwner().getTypedID());
        Assert.assertEquals("token", api.getOwner().getAccessToken());

        Map<String, Class<? extends Action>> expectedActionTypes = new HashMap<>();
        expectedActionTypes.put(AliasUtils.aliasActionKey("alias1", "turnPower"), TurnPower.class);
        expectedActionTypes.put(AliasUtils.aliasActionKey("alias1", "setPresetTemperature"), SetPresetTemperature.class);
        expectedActionTypes.put(AliasUtils.aliasActionKey("alias2", "setPresetHumidity"), SetPresetHumidity.class);
        Assert.assertEquals(expectedActionTypes, api.getActionTypes());

        Map<String, Class<? extends TargetState>> expectedStateTypes = new HashMap<>();
        expectedStateTypes.put("alias1", AirConditionerState.class);
        expectedStateTypes.put("alias2", HumidityState.class);
        Assert.assertEquals(expectedStateTypes, api.getStateTypes());
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
                .registerAction("airConditionerAlias", "turnPower", TurnPower.class)
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

    @Test(expected = IllegalArgumentException.class)
    public void registerAction_emptyAction_throw_IllegalArgumentExceptionTest() {
        ThingIFAPI.Builder.newBuilder(
                this.context,
                new KiiApp("appid", "appkey", Site.JP),
                new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"))
                .registerAction("alias1", "turnPower", EmptyAction.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void registerAction_emptyInnerAction_throw_IllegalArgumentExceptionTest() {
        ThingIFAPI.Builder.newBuilder(
                this.context,
                new KiiApp("appid", "appkey", Site.JP),
                new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"))
                .registerAction("alias1", "turnPower", InnerEmptyAction.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void registerAction_emptyInnerStaticAction_throw_IllegalArgumentExceptionTest() {
        ThingIFAPI.Builder.newBuilder(
                this.context,
                new KiiApp("appid", "appkey", Site.JP),
                new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"))
                .registerAction("alias1", "turnPower", InnerStaticEmptyAction.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void registerAction_emptyNameAction_throw_IllegalArgumentExceptionTest() {
        ThingIFAPI.Builder.newBuilder(
                this.context,
                new KiiApp("appid", "appkey", Site.JP),
                new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"))
                .registerAction("alias1", "turnPower", EmptyNameAction.class);
    }
    @Test(expected = IllegalArgumentException.class)
    public void registerAction_nullNameAction_throw_IllegalArgumentExceptionTest() {
        ThingIFAPI.Builder.newBuilder(
                this.context,
                new KiiApp("appid", "appkey", Site.JP),
                new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"))
                .registerAction("alias1", "turnPower", NullNameAction.class);
    }

    @Test
    public void regiesterAction_InnerNonEmptyAction_should_succeededTest() {
        ThingIFAPI api = ThingIFAPI.Builder.newBuilder(
                this.context,
                new KiiApp("appid", "appkey", Site.JP),
                new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"))
                .registerAction("alias2", "turnPower", InnerStaticTurnPower.class)
                .registerTargetState("alias1", AirConditionerState.class)
                .build();

        Map<String, Class<? extends Action>> actionTypes = new HashMap<>();
        actionTypes.put(AliasUtils.aliasActionKey("alias2", "turnPower"), InnerStaticTurnPower.class);

        Assert.assertEquals(actionTypes, api.getActionTypes());
    }

    @Test(expected = IllegalArgumentException.class)
    public void registerAction_NonStaticInnerClass_should_throw_exceptionTest() {
        ThingIFAPI.Builder.newBuilder(
                this.context,
                new KiiApp("appid", "appkey", Site.JP),
                new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"))
                .registerAction("alias1", "turnPower", InnerTurnPower.class)
                .registerTargetState("alias1", AirConditionerState.class)
                .build();
    }
}
