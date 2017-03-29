package com.kii.thingif.command;

import android.os.Parcel;

import com.kii.thingif.SmallTestBase;
import com.kii.thingif.TypedID;
import com.kii.thingif.actions.SetPresetHumidity;
import com.kii.thingif.actions.SetPresetTemperature;
import com.kii.thingif.actions.TurnPower;
import com.kii.thingif.utils.JsonUtil;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class CommandTest extends SmallTestBase {
    @Test
    public void test() throws Exception{

        String alias1 = "AirConditionerAlias";
        String alias2 = "HumidityAlias";

        List<AliasAction> aliasActions = new ArrayList<>();
        List<AliasActionResult> aliasActionResults = new ArrayList<>();
        
        List<Action> actions1 = new ArrayList<>();
        actions1.add(new TurnPower(true));
        actions1.add(new SetPresetTemperature(100));
        AliasAction aliasAction1 =
                new AliasAction(
                        alias1,
                        actions1);

        List<Action> actions2 = new ArrayList<>();
        actions2.add(new SetPresetHumidity(50));
        AliasAction aliasAction2 =
                new AliasAction(
                        alias2,
                        actions2);

        List<Action> actions3 = new ArrayList<>();
        actions3.add(new TurnPower(false));
        AliasAction aliasAction3 =
                new AliasAction(
                        alias1,
                        actions3);
        aliasActions.add(aliasAction1);
        aliasActions.add(aliasAction2);
        aliasActions.add(aliasAction3);

        List<ActionResult> results1 = new ArrayList<>();
        results1.add(new ActionResult("turnPower", true, null, null));
        results1.add(new ActionResult(
                "setPresetTemperature",
                false,
                "invalid value",
                new JSONObject().put("k", "v")));
        aliasActionResults.add(new AliasActionResult(alias1, results1));

        List<ActionResult> results2 = new ArrayList<>();
        results2.add(new ActionResult("setPresetHumidity", true, null, null));
        aliasActionResults.add(new AliasActionResult(alias2, results2));

        List<ActionResult> results3 = new ArrayList<>();
        results3.add(new ActionResult(
                "turnPower",
                false,
                "invalid value",
                new JSONObject().put("k2", "v2")));
        aliasActionResults.add(new AliasActionResult(alias1, results3));

        Long date1 = new Date().getTime();
        Long date2 = date1+1000;

        Command command = new Command(
                new TypedID(TypedID.Types.USER, "user1"),
                aliasActions,
                "command1",
                new TypedID(TypedID.Types.THING, "thing1"),
                aliasActionResults,
                CommandState.SENDING,
                "trigger1",
                date1,
                date2,
                "command title",
                "command description",
                new JSONObject().put("k", "v"));

        Assert.assertNotNull(command.getCommandID());
        Assert.assertEquals("command1", command.getCommandID());
        Assert.assertEquals("user:user1", command.getIssuerID().toString());
        Assert.assertNotNull(command.getTargetID());
        Assert.assertEquals("thing:thing1", command.getTargetID().toString());
        Assert.assertEquals(CommandState.SENDING, command.getCommandState());
        Assert.assertNotNull(command.getFiredByTriggerID());
        Assert.assertEquals(date1, command.getCreated());
        Assert.assertEquals(date2, command.getModified());
        Assert.assertEquals("command title", command.getTitle());
        Assert.assertEquals("command description", command.getDescription());
        Assert.assertNotNull(command.getMetadata());
        Assert.assertEquals(
                new JSONObject().put("k", "v").toString(),
                command.getMetadata().toString()
        );

        Assert.assertEquals(3, command.getAliasActions().size());


        assertJSONObject(
                JsonUtil.aliasActionToJson(aliasAction1),
                JsonUtil.aliasActionToJson(command.getAliasActions().get(0)));

        assertJSONObject(
                JsonUtil.aliasActionToJson(aliasAction2),
                JsonUtil.aliasActionToJson(command.getAliasActions().get(1)));

        assertJSONObject(
                JsonUtil.aliasActionToJson(aliasAction3),
                JsonUtil.aliasActionToJson(command.getAliasActions().get(2)));

        Assert.assertNotNull(command.getAliasActionResults());
        Assert.assertEquals(3, command.getAliasActionResults().size());

        Assert.assertEquals(
                alias1,
                command.getAliasActionResults().get(0).getAlias());
        Assert.assertEquals(
                "turnPower",
                command.getAliasActionResults().get(0).getResults().get(0).getActionName());
        Assert.assertEquals(
                true,
                command.getAliasActionResults().get(0).getResults().get(0).isSucceeded());
        Assert.assertNull(
                command.getAliasActionResults().get(0).getResults().get(0).getErrorMessage());
        Assert.assertNull(
                command.getAliasActionResults().get(0).getResults().get(0).getData());
        Assert.assertEquals(
                "setPresetTemperature",
                command.getAliasActionResults().get(0).getResults().get(1).getActionName());
        Assert.assertEquals(
                false,
                command.getAliasActionResults().get(0).getResults().get(1).isSucceeded());
        Assert.assertEquals(
                "invalid value",
                command.getAliasActionResults().get(0).getResults().get(1).getErrorMessage());
        Assert.assertNotNull(command.getAliasActionResults().get(0).getResults().get(1).getData());
        Assert.assertEquals(
                new JSONObject().put("k", "v").toString(),
                command.getAliasActionResults().get(0).getResults().get(1).getData().toString());

        Assert.assertEquals(
                alias2,
                command.getAliasActionResults().get(1).getAlias());
        Assert.assertEquals(
                "setPresetHumidity",
                command.getAliasActionResults().get(1).getResults().get(0).getActionName());
        Assert.assertEquals(
                true,
                command.getAliasActionResults().get(1).getResults().get(0).isSucceeded());
        Assert.assertNull(
                command.getAliasActionResults().get(1).getResults().get(0).getErrorMessage());
        Assert.assertNull(
                command.getAliasActionResults().get(1).getResults().get(0).getData());

        Assert.assertEquals(
                alias1,
                command.getAliasActionResults().get(2).getAlias());
        Assert.assertEquals(
                "turnPower",
                command.getAliasActionResults().get(2).getResults().get(0).getActionName());
        Assert.assertEquals(
                false,
                command.getAliasActionResults().get(2).getResults().get(0).isSucceeded());
        Assert.assertEquals(
                "invalid value",
                command.getAliasActionResults().get(2).getResults().get(0).getErrorMessage());
        Assert.assertNotNull(
                command.getAliasActionResults().get(2).getResults().get(0).getData());
        Assert.assertEquals(
                new JSONObject().put("k2", "v2").toString(),
                command.getAliasActionResults().get(2).getResults().get(0).getData().toString());

        // test retrieve action by alias
        List<AliasAction> foundActions =
                command.getAliasAction(alias1);
        Assert.assertEquals(2, foundActions.size());

        assertJSONObject(
                JsonUtil.aliasActionToJson(aliasAction1),
                JsonUtil.aliasActionToJson(foundActions.get(0)));

        assertJSONObject(
                JsonUtil.aliasActionToJson(aliasAction3),
                JsonUtil.aliasActionToJson(foundActions.get(1)));

        List<AliasAction> foundActions2 =
                command.getAliasAction("NewAlias");
        Assert.assertEquals(0, foundActions2.size());

        // test retrieve action result by alias and acton name
        List<ActionResult> foundResults =
                command.getActionResult(alias1, "turnPower");
        Assert.assertEquals(2, foundResults.size());
        Assert.assertEquals("turnPower", foundResults.get(0).getActionName());
        Assert.assertEquals(true, foundResults.get(0).isSucceeded());
        Assert.assertNull(foundResults.get(0).getErrorMessage());
        Assert.assertNull(foundResults.get(0).getData());
        Assert.assertEquals("turnPower", foundResults.get(1).getActionName());
        Assert.assertEquals(false, foundResults.get(1).isSucceeded());
        Assert.assertEquals("invalid value", foundResults.get(1).getErrorMessage());
        Assert.assertNotNull(foundResults.get(1).getData());
        Assert.assertEquals(
                new JSONObject().put("k2", "v2").toString(),
                foundResults.get(1).getData().toString());

        List<ActionResult> foundResults2 =
                command.getActionResult("NewAlias", "newAction");
        Assert.assertEquals(0, foundResults2.size());
    }

    @Test
    public void parcelableTest() throws Exception{

        String alias1 = "AirConditionerAlias";
        String alias2 = "HumidityAlias";

        List<AliasAction> aliasActions = new ArrayList<>();
        List<AliasActionResult> aliasActionResults = new ArrayList<>();

        List<Action> actions1 = new ArrayList<>();
        actions1.add(new TurnPower(true));
        AliasAction aliasAction1 =
                new AliasAction(
                        alias1,
                        actions1);

        List<Action> actions2 = new ArrayList<>();
        AliasAction aliasAction2 =
                new AliasAction(
                        alias2,
                        actions2);
        aliasActions.add(aliasAction1);
        aliasActions.add(aliasAction2);

        List<ActionResult> results1 = new ArrayList<>();
        results1.add(new ActionResult("turnPower", true, null, null));
        results1.add(new ActionResult("setPresetTemperature", false, "invalid value", null));
        aliasActionResults.add(new AliasActionResult(alias1, results1));

        List<ActionResult> results2 = new ArrayList<>();
        results2.add(new ActionResult("setPresetHumidity", true, null, null));
        aliasActionResults.add(new AliasActionResult(alias2, results2));

        Long date1 = new Date().getTime();
        Long date2 = date1 + 1000;

        Command command = new Command(
                new TypedID(TypedID.Types.USER, "user1"),
                aliasActions,
                "command",
                new TypedID(TypedID.Types.THING, "thing1"),
                aliasActionResults,
                CommandState.SENDING,
                "trigger1",
                date1,
                date2,
                "command title",
                "command description",
                new JSONObject().put("k", "v"));

        Parcel parcel = Parcel.obtain();
        command.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        Command deserializedCommand1 = new Command(parcel);

        Assert.assertEquals("command", deserializedCommand1.getCommandID());
        Assert.assertEquals("user:user1",
                deserializedCommand1.getIssuerID().toString());
        Assert.assertNotNull(deserializedCommand1.getTargetID());
        Assert.assertEquals(
                "thing:thing1",
                deserializedCommand1.getTargetID().toString());
        Assert.assertNotNull(deserializedCommand1.getCommandState());
        Assert.assertEquals(
                "SENDING",
                deserializedCommand1.getCommandState().toString());
        Assert.assertEquals(
                "trigger1",
                deserializedCommand1.getFiredByTriggerID());

        Assert.assertEquals("command title", deserializedCommand1.getTitle());
        Assert.assertEquals("command description", deserializedCommand1.getDescription());
        Assert.assertEquals(date1, deserializedCommand1.getCreated());
        Assert.assertEquals(date2, deserializedCommand1.getModified());
        Assert.assertNotNull(deserializedCommand1.getMetadata());
        Assert.assertEquals(
                new JSONObject().put("k", "v").toString(),
                deserializedCommand1.getMetadata().toString());

        // check aliasActions
        Assert.assertEquals(
                2,
                deserializedCommand1.getAliasActions().size());

        assertJSONObject(
                JsonUtil.aliasActionToJson(aliasAction1),
                JsonUtil.aliasActionToJson(deserializedCommand1.getAliasActions().get(0)));

        assertJSONObject(
                JsonUtil.aliasActionToJson(aliasAction2),
                JsonUtil.aliasActionToJson(deserializedCommand1.getAliasActions().get(1)));

        // check actionResults
        Assert.assertNotNull(deserializedCommand1.getAliasActionResults());
        Assert.assertEquals(
                2,
                deserializedCommand1.getAliasActionResults().size());
        AliasActionResult actualResults1 = deserializedCommand1.getAliasActionResults().get(0);
        Assert.assertEquals(alias1, actualResults1.getAlias());
        Assert.assertEquals(2, actualResults1.getResults().size());
        Assert.assertEquals(
                "turnPower",
                actualResults1.getResults().get(0).getActionName());
        Assert.assertEquals(true,
                actualResults1.getResults().get(0).isSucceeded());
        Assert.assertNull(actualResults1.getResults().get(0).getErrorMessage());
        Assert.assertNull(actualResults1.getResults().get(0).getData());
        Assert.assertEquals("setPresetTemperature",
                actualResults1.getResults().get(1).getActionName());
        Assert.assertEquals(false,
                actualResults1.getResults().get(1).isSucceeded());
        Assert.assertNull(actualResults1.getResults().get(1).getData());
        Assert.assertEquals("invalid value",
                actualResults1.getResults().get(1).getErrorMessage());
    }
}
