package com.kii.thingiftrait.thingifapi;

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.kii.thingiftrait.KiiApp;
import com.kii.thingiftrait.Owner;
import com.kii.thingiftrait.Site;
import com.kii.thingiftrait.TargetState;
import com.kii.thingiftrait.ThingIFAPI;
import com.kii.thingiftrait.ThingIFAPITestBase;
import com.kii.thingiftrait.TypedID;
import com.kii.thingiftrait.actions.SetPresetHumidity;
import com.kii.thingiftrait.actions.SetPresetTemperature;
import com.kii.thingiftrait.actions.TurnPower;
import com.kii.thingiftrait.command.Action;
import com.kii.thingiftrait.internal.utils.AliasUtils;
import com.kii.thingiftrait.states.AirConditionerState;
import com.kii.thingiftrait.states.HumidityState;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.HashMap;
import java.util.Map;

class NFNAAction implements Action {
    private Boolean power;
    public NFNAAction(Boolean power) {
        this.power = power;
    }

    public Boolean getPower() {
        return this.power;
    }
}

// empty annotation
class ENAction implements Action {
    @SerializedName("")
    private Boolean power;
    public ENAction(Boolean power) {
        this.power = power;
    }

    public Boolean getPower() {
        return this.power;
    }
}

// field name same as actionName
class TurnPower2 implements Action {
    private Boolean turnPower;
    public TurnPower2(Boolean turnPower) {
        this.turnPower = turnPower;
    }

    public Boolean getPower() {
        return this.turnPower;
    }

}
@RunWith(RobolectricTestRunner.class)
public class BuilderTest extends ThingIFAPITestBase{

    class InnerTurnPower implements Action {
        @SerializedName("turnPower")
        private Boolean power;
        public InnerTurnPower(Boolean power) {
            this.power = power;
        }

        public Boolean getPower() {
            return this.power;
        }
    }

    static class InnerStaticTurnPower implements Action {
        @SerializedName("turnPower")
        private Boolean power;
        public InnerStaticTurnPower(Boolean power) {
            this.power = power;
        }
        public Boolean getPower() {
            return this.power;
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
                .registerAction("alias3", "turnPower", TurnPower2.class)
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
        expectedActionTypes.put(AliasUtils.aliasActionKey("alias3", "turnPower"), TurnPower2.class);
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
                .registerAction("alias1", "turnPower", NFNAAction.class);
    }


    @Test(expected = IllegalArgumentException.class)
    public void registerAction_emptyNameAction_throw_IllegalArgumentExceptionTest() {
        ThingIFAPI.Builder.newBuilder(
                this.context,
                new KiiApp("appid", "appkey", Site.JP),
                new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"))
                .registerAction("alias1", "turnPower", ENAction.class);
    }

    @Test
    public void registerAction_InnerNonEmptyAction_should_succeededTest() {
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
