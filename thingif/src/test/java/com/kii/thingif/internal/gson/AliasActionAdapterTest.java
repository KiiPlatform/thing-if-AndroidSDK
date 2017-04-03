package com.kii.thingif.internal.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kii.thingif.SmallTestBase;
import com.kii.thingif.actions.SetPresetHumidity;
import com.kii.thingif.actions.SetPresetTemperature;
import com.kii.thingif.actions.TurnPower;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.AliasAction;
import com.kii.thingif.internal.utils.AliasUtils;
import com.kii.thingif.utils.JsonUtil;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
public class AliasActionAdapterTest extends SmallTestBase {
    private static final String alias1 = "AirConditionerAlias";
    private static final String alias2 = "HumidityAlias";

    private Gson gson;
    @Before
    public void before() {
        Map<String, Class<? extends Action>> actionTypes = new HashMap<>();

        actionTypes.put(AliasUtils.aliasActionKey(alias1, "turnPower"), TurnPower.class);
        actionTypes.put(AliasUtils.aliasActionKey(alias1, "setPresetTemperature"), SetPresetTemperature.class);
        actionTypes.put(AliasUtils.aliasActionKey(alias2, "setPresetHumidity"), SetPresetHumidity.class);

        gson = new GsonBuilder()
                .registerTypeAdapter(
                        AliasAction.class,
                        new AliasActionAdapter(actionTypes))
                .create();
    }

    @Test
    public void serializationTest() throws Exception{
        List<Action> actions1 = new ArrayList<>();
        actions1.add(new TurnPower(true));
        actions1.add(new SetPresetTemperature(23));
        AliasAction aliasAction1 = new AliasAction(
                alias1,
                actions1);
        String actualAction1 = gson.toJson(aliasAction1, AliasAction.class);
        assertJSONObject(
                JsonUtil.aliasActionToJson(aliasAction1),
                new JSONObject(actualAction1));

        List<Action> actions2 = new ArrayList<>();
        actions2.add(new SetPresetHumidity(45));
        AliasAction aliasActionc2 = new AliasAction(alias2, actions2);
        String actualAction2 = gson.toJson(aliasActionc2, AliasAction.class);
        assertJSONObject(
                JsonUtil.aliasActionToJson(aliasActionc2),
                new JSONObject(actualAction2));
    }

    @Test
    public void deserializationTest() {
        List<Action> actions = new ArrayList<>();
        actions.add(new TurnPower(true));
        actions.add(new SetPresetTemperature(23));
        AliasAction aa = new AliasAction(alias1, actions);

        String aaString = JsonUtil.aliasActionToJson(aa).toString();
        AliasAction deserializedAA = gson.fromJson(aaString, AliasAction.class);

        Assert.assertEquals(alias1, deserializedAA.getAlias());
        Assert.assertEquals(2, deserializedAA.getActions().size());

        Action action1  =deserializedAA.getActions().get(0);
        Assert.assertTrue(action1 instanceof TurnPower);
        Assert.assertTrue(((TurnPower)action1).getPower());

        Action action2 = deserializedAA.getActions().get(1);
        Assert.assertTrue(action2 instanceof SetPresetTemperature);
        Assert.assertEquals(23, ((SetPresetTemperature)action2).getTemperature().intValue());
    }
}
