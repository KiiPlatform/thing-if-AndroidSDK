package com.kii.thingif.internal.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.kii.thingif.SmallTestBase;
import com.kii.thingif.actions.AirConditionerActions;
import com.kii.thingif.actions.HumidityActions;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.AliasAction;
import com.kii.thingif.utils.JsonUtil;

import junit.framework.Assert;

import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
public class AliasActionListAdapterTest extends SmallTestBase {
    private final String alias1 = "AirConditionerAlias";
    private final String alias2 = "HumidityAlias";
    private Gson gson;
    private final Type listType = new TypeToken<List<AliasAction>>(){}.getType();

    @Before
    public void before() {
        Map<String, Class<? extends Action>> actionTypes = new HashMap<>();
        actionTypes.put(this.alias1, AirConditionerActions.class);
        actionTypes.put(this.alias2, HumidityActions.class);
        this.gson = new GsonBuilder()
                .registerTypeAdapter(
                        this.listType,
                        new AliasActionListAdapter(actionTypes))
                .create();
    }
    @Test
    public void serializationTest() {
        //test with only one non-null field action, all elements are same alias
        List<AliasAction> aliasActions1 = new ArrayList<>();
        aliasActions1.add(new AliasAction<>(alias1, new AirConditionerActions(true, null)));
        aliasActions1.add(new AliasAction<>(alias1, new AirConditionerActions(null, 23)));
        JSONArray expectedAliasActions1 = new JSONArray();
        expectedAliasActions1.put(JsonUtil.combineAliasActionsJson(alias1, aliasActions1.subList(0, 2)));
        Assert.assertEquals(expectedAliasActions1.toString(), gson.toJson(aliasActions1, listType));

        //test with only one non-null field action, all elements are diff alias
        List<AliasAction> aliasActions2 = new ArrayList<>();
        aliasActions2.add(new AliasAction<>(alias1, new AirConditionerActions(true, null)));
        aliasActions2.add(new AliasAction<>(alias2, new HumidityActions(45)));
        JSONArray expectedAliasActions2 = new JSONArray();
        expectedAliasActions2.put(JsonUtil.singleAliasActionToJson(aliasActions2.get(0)));
        expectedAliasActions2.put(JsonUtil.singleAliasActionToJson(aliasActions2.get(1)));
        Assert.assertEquals(expectedAliasActions2.toString(), gson.toJson(aliasActions2, listType));

        //test with only one non-null field action, with a diff alias
        List<AliasAction> aliasActions3 = new ArrayList<>();
        aliasActions3.add(new AliasAction<>(alias1, new AirConditionerActions(true, null)));
        aliasActions3.add(new AliasAction<>(alias1, new AirConditionerActions(null, 23)));
        aliasActions3.add(new AliasAction<>(alias2, new HumidityActions(45)));
        aliasActions3.add(new AliasAction<>(alias1, new AirConditionerActions(false, null)));
        JSONArray expectedAliasActions3 = new JSONArray();
        expectedAliasActions3.put(JsonUtil.combineAliasActionsJson(alias1, aliasActions3.subList(0, 2)));
        expectedAliasActions3.put(JsonUtil.singleAliasActionToJson(aliasActions3.get(2)));
        expectedAliasActions3.put(JsonUtil.singleAliasActionToJson(aliasActions3.get(3)));
        Assert.assertEquals(expectedAliasActions3.toString(), gson.toJson(aliasActions3, listType));

        //test with only one non-null field action, with a diff alias, and same alias at the end
        List<AliasAction> aliasActions4 = new ArrayList<>();
        aliasActions4.add(new AliasAction<>(alias1, new AirConditionerActions(true, null)));
        aliasActions4.add(new AliasAction<>(alias1, new AirConditionerActions(null, 23)));
        aliasActions4.add(new AliasAction<>(alias2, new HumidityActions(45)));
        aliasActions4.add(new AliasAction<>(alias1, new AirConditionerActions(null, 24)));
        aliasActions4.add(new AliasAction<>(alias1, new AirConditionerActions(false, null)));
        JSONArray expectedAliasActions4 = new JSONArray();
        expectedAliasActions4.put(JsonUtil.combineAliasActionsJson(alias1, aliasActions4.subList(0, 2)));
        expectedAliasActions4.put(JsonUtil.singleAliasActionToJson(aliasActions4.get(2)));
        expectedAliasActions4.put(JsonUtil.combineAliasActionsJson(alias1, aliasActions4.subList(3, 5)));
        Assert.assertEquals(expectedAliasActions4.toString(), gson.toJson(aliasActions4, listType));

        // test with more more one non-null field action
        // TODO
    }

    @Test
    public void deserializationTest() {

        // alias actons combined
        List<AliasAction> aliasActions1 = new ArrayList<>();
        aliasActions1.add(new AliasAction<>(alias1, new AirConditionerActions(true, null)));
        aliasActions1.add(new AliasAction<>(alias1, new AirConditionerActions(null, 23)));
        aliasActions1.add(new AliasAction<>(alias2, new HumidityActions(45)));
        aliasActions1.add(new AliasAction<>(alias1, new AirConditionerActions(null, 24)));
        aliasActions1.add(new AliasAction<>(alias1, new AirConditionerActions(false, null)));

        JSONArray aliasActionsArray1 = new JSONArray();
        aliasActionsArray1.put(JsonUtil.combineAliasActionsJson(alias1, aliasActions1.subList(0, 2)));
        aliasActionsArray1.put(JsonUtil.singleAliasActionToJson(aliasActions1.get(2)));
        aliasActionsArray1.put(JsonUtil.combineAliasActionsJson(alias1, aliasActions1.subList(3, 5)));

        List<AliasAction> deserializedAliasActoins = this.gson.fromJson(aliasActionsArray1.toString(), listType);
        Assert.assertEquals(aliasActions1.size(), deserializedAliasActoins.size());
        for (int i=0; i<deserializedAliasActoins.size(); i++) {
            AliasAction expectedAliasAction = aliasActions1.get(i);
            AliasAction actualAliasAction = deserializedAliasActoins.get(i);
            assertJSONObject(
                    "failed on ["+i+"]",
                    JsonUtil.singleAliasActionToJson(expectedAliasAction),
                    JsonUtil.singleAliasActionToJson(actualAliasAction));
        }

        // alias actions is not combine
        List<AliasAction> aliasActions2 = new ArrayList<>();
        aliasActions2.add(new AliasAction<>(alias1, new AirConditionerActions(true, null)));
        aliasActions2.add(new AliasAction<>(alias1, new AirConditionerActions(null, 23)));
        aliasActions2.add(new AliasAction<>(alias2, new HumidityActions(45)));
        aliasActions2.add(new AliasAction<>(alias1, new AirConditionerActions(null, 24)));
        aliasActions2.add(new AliasAction<>(alias1, new AirConditionerActions(false, null)));

        JSONArray aliasActionsArray2 = new JSONArray();
        aliasActionsArray2.put(JsonUtil.singleAliasActionToJson(aliasActions1.get(0)));
        aliasActionsArray2.put(JsonUtil.singleAliasActionToJson(aliasActions1.get(1)));
        aliasActionsArray2.put(JsonUtil.singleAliasActionToJson(aliasActions1.get(2)));
        aliasActionsArray2.put(JsonUtil.singleAliasActionToJson(aliasActions1.get(3)));
        aliasActionsArray2.put(JsonUtil.singleAliasActionToJson(aliasActions1.get(4)));

        List<AliasAction> deserializedAliasActoins2 = this.gson.fromJson(aliasActionsArray2.toString(), listType);
        Assert.assertEquals(aliasActions2.size(), deserializedAliasActoins2.size());
        for (int i=0; i<deserializedAliasActoins2.size(); i++) {
            AliasAction expectedAliasAction = aliasActions2.get(i);
            AliasAction actualAliasAction = deserializedAliasActoins2.get(i);
            assertJSONObject(
                    "failed on ["+i+"]",
                    JsonUtil.singleAliasActionToJson(expectedAliasAction),
                    JsonUtil.singleAliasActionToJson(actualAliasAction));
        }

    }
}
