package com.kii.thingif.command;

import android.os.Parcel;

import com.kii.thingif.TypedID;
import com.kii.thingif.actions.AirConditionerActions;
import com.kii.thingif.actions.HumidityActions;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class CommandParcelableTest {

    @Test
    public void test() throws Exception{

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
