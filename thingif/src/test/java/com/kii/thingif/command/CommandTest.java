package com.kii.thingif.command;

import android.os.Parcel;

import com.kii.thingif.TypedID;
import com.kii.thingif.actions.AirConditionerActions;
import com.kii.thingif.actions.HumidityActions;
import com.kii.thingif.actions.NewAction;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class CommandTest {
    @Test
    public void test() throws Exception{

        String alias1 = "AirConditionerAlias";
        String alias2 = "HumidityAlias";

        List<AliasAction<? extends Action>> aliasActions = new ArrayList<>();
        List<AliasActionResult> aliasActionResults = new ArrayList<>();

        AliasAction<AirConditionerActions> action1 =
                new AliasAction<>(
                        alias1,
                        new AirConditionerActions(true, 100));
        AliasAction<HumidityActions> action2 =
                new AliasAction<>(
                        alias2,
                        new HumidityActions(50));
        AliasAction<AirConditionerActions> action3 =
                new AliasAction<>(
                        alias1,
                        new AirConditionerActions(false, null));
        aliasActions.add(action1);
        aliasActions.add(action2);
        aliasActions.add(action3);

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

        Assert.assertEquals(alias1, command.getAliasActions().get(0).getAlias());
        Action actualAction1 = command.getAliasActions().get(0).getAction();
        Assert.assertTrue(actualAction1 instanceof AirConditionerActions);
        Assert.assertEquals(true,
                ((AirConditionerActions)actualAction1).isPower().booleanValue());
        Assert.assertEquals(
                100,
                ((AirConditionerActions)actualAction1).getPresetTemperature().intValue());

        Assert.assertEquals(alias2, command.getAliasActions().get(1).getAlias());
        Action actualAction2 = command.getAliasActions().get(1).getAction();
        Assert.assertTrue(actualAction2 instanceof HumidityActions);
        Assert.assertEquals(
                50,
                ((HumidityActions)actualAction2).getPresetHumidity().intValue());

        Assert.assertEquals(alias1, command.getAliasActions().get(2).getAlias());
        Action actualAction3 = command.getAliasActions().get(2).getAction();
        Assert.assertTrue(actualAction3 instanceof AirConditionerActions);
        Assert.assertEquals(
                false,
                ((AirConditionerActions)actualAction3).isPower().booleanValue());
        Assert.assertNull(((AirConditionerActions)actualAction3).getPresetTemperature());

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
        List<AliasAction<AirConditionerActions>> foundActions =
                command.getAction(alias1, AirConditionerActions.class);
        Assert.assertEquals(2, foundActions.size());
        Assert.assertEquals(alias1, foundActions.get(0).getAlias());
        Assert.assertEquals(true, foundActions.get(0).getAction().isPower().booleanValue());
        Assert.assertEquals(100, foundActions.get(0).getAction().getPresetTemperature().intValue());
        Assert.assertEquals(alias1, foundActions.get(1).getAlias());
        Assert.assertEquals(false, foundActions.get(1).getAction().isPower().booleanValue());
        Assert.assertNull(foundActions.get(1).getAction().getPresetTemperature());

        List<AliasAction<NewAction>> foundActions2 =
                command.getAction("NewAlias", NewAction.class);
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

        List<AliasAction<? extends Action>> aliasActions = new ArrayList<>();
        List<AliasActionResult> aliasActionResults = new ArrayList<>();

        AliasAction<AirConditionerActions> action1 =
                new AliasAction<>(
                        alias1,
                        new AirConditionerActions(true, null));
        AliasAction<HumidityActions> action2 =
                new AliasAction<>(
                        alias2,
                        new HumidityActions(50));
        aliasActions.add(action1);
        aliasActions.add(action2);

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
        Assert.assertEquals(
                alias1,
                deserializedCommand1.getAliasActions().get(0).getAlias());
        Action actualAction1 = deserializedCommand1.getAliasActions().get(0).getAction();
        Assert.assertTrue(actualAction1 instanceof AirConditionerActions);
        Assert.assertTrue(((AirConditionerActions)actualAction1).isPower());
        Assert.assertNull(((AirConditionerActions)actualAction1).getPresetTemperature());
        Assert.assertEquals(
                alias2,
                deserializedCommand1.getAliasActions().get(1).getAlias());
        Action actualAction2 = deserializedCommand1.getAliasActions().get(1).getAction();
        Assert.assertTrue(actualAction2 instanceof HumidityActions);
        Assert.assertEquals(50, ((HumidityActions)actualAction2).getPresetHumidity().intValue());

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
