package com.kii.thingif.largetests;

import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;
import android.util.Pair;

import com.kii.thingif.OnboardWithVendorThingIDOptions;
import com.kii.thingif.Target;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.TypedID;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.AliasAction;
import com.kii.thingif.command.Command;
import com.kii.thingif.command.CommandForm;
import com.kii.thingif.command.CommandState;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RunWith(AndroidJUnit4.class)
public class CommandTest extends LargeTestCaseBase {

    @Test
    public void baseTest() throws Exception{
        ThingIFAPI api = createDefaultThingIFAPI();
        String vendorThingID = UUID.randomUUID().toString();
        String thingPassword = "password";

        // on-boarding thing
        OnboardWithVendorThingIDOptions options =
                new OnboardWithVendorThingIDOptions.Builder()
                        .setThingType(DEFAULT_THING_TYPE)
                        .setFirmwareVersion(DEFAULT_FIRMWARE_VERSION).build();
        Target target = api.onboardWithVendorThingID(vendorThingID, thingPassword, options);
        Assert.assertEquals(TypedID.Types.THING, target.getTypedID().getType());
        Assert.assertNotNull(target.getAccessToken());

        // create new command
        String commandTitle = "title";
        String commandDescription = "description";
        JSONObject metaData = new JSONObject().put("k", "v");
        List<AliasAction<? extends Action>> aliasActions = new ArrayList<>();
        aliasActions.add(
                new AliasAction<>(
                        ALIAS1,
                        new AirConditionerActions(true, 25)));
        aliasActions.add(
                new AliasAction<Action>(
                        ALIAS2,
                        new HumidityActions(50)));

        CommandForm form = CommandForm
                .Builder
                .newBuilder(aliasActions)
                .setTitle(commandTitle)
                .setDescription(commandDescription)
                .setMetadata(metaData)
                .build();
        Command command1 = api.postNewCommand(form);
        Assert.assertNotNull(command1.getCommandID());
        Assert.assertEquals(commandTitle, command1.getTitle());
        Assert.assertEquals(commandDescription, command1.getDescription());
        Assert.assertNotNull(command1.getMetadata());
        Assert.assertEquals(metaData.toString(), command1.getMetadata().toString());
        Assert.assertEquals(CommandState.SENDING, command1.getCommandState());
        Assert.assertNotNull(command1.getCreated());
        Assert.assertNotNull(command1.getModified());
        Assert.assertNull(command1.getFiredByTriggerID());
        Assert.assertNull(command1.getAliasActionResults());

        Assert.assertEquals(2, command1.getAliasActions().size());

        Assert.assertEquals(ALIAS1, command1.getAliasActions().get(0).getAlias());
        Action action1 = command1.getAliasActions().get(0).getAction();
        Assert.assertTrue(action1 instanceof AirConditionerActions);
        Assert.assertEquals(
                true,
                ((AirConditionerActions)action1).isPower().booleanValue());
        Assert.assertEquals(
                25,
                ((AirConditionerActions)action1).getPresetTemperature().intValue());
        Assert.assertEquals(ALIAS2, command1.getAliasActions().get(1).getAlias());
        Action action2 = command1.getAliasActions().get(1).getAction();
        Assert.assertTrue(action2 instanceof HumidityActions);
        Assert.assertEquals(50, ((HumidityActions)action2).getPresetHumidity().intValue());

        // get created command
        Command command1Copy = api.getCommand(command1.getCommandID());
        Assert.assertNotNull(command1Copy.getCommandID());
        Assert.assertEquals(commandTitle, command1Copy.getTitle());
        Assert.assertEquals(commandDescription, command1Copy.getDescription());
        Assert.assertNotNull(command1Copy.getMetadata());
        Assert.assertEquals(metaData.toString(), command1Copy.getMetadata().toString());
        Assert.assertEquals(CommandState.SENDING, command1Copy.getCommandState());
        Assert.assertNotNull(command1Copy.getCreated());
        Assert.assertNotNull(command1Copy.getModified());
        Assert.assertNull(command1Copy.getFiredByTriggerID());
        Assert.assertNull(command1Copy.getAliasActionResults());

        Assert.assertEquals(2, command1Copy.getAliasActions().size());

        Assert.assertEquals(ALIAS1, command1Copy.getAliasActions().get(0).getAlias());
        Action action11 = command1Copy.getAliasActions().get(0).getAction();
        Assert.assertTrue(action11 instanceof AirConditionerActions);
        Assert.assertEquals(
                true,
                ((AirConditionerActions)action11).isPower().booleanValue());
        Assert.assertEquals(
                25,
                ((AirConditionerActions)action11).getPresetTemperature().intValue());
        Assert.assertEquals(ALIAS2, command1Copy.getAliasActions().get(1).getAlias());
        Action action12 = command1Copy.getAliasActions().get(1).getAction();
        Assert.assertTrue(action12 instanceof HumidityActions);
        Assert.assertEquals(50, ((HumidityActions)action12).getPresetHumidity().intValue());

        // create new command
        CommandForm form2 = CommandForm
                .Builder
                .newBuilder()
                .addAliasAction(new AliasAction<Action>(
                        ALIAS2,
                        new HumidityActions(50)))
                .build();
        Command command2 = api.postNewCommand(form2);

        Assert.assertNotNull(command2.getCommandID());
        Assert.assertNull(command2.getTitle());
        Assert.assertNull(command2.getDescription());
        Assert.assertNull(command2.getMetadata());
        Assert.assertNull(command2.getAliasActionResults());
        Assert.assertNull(command2.getFiredByTriggerID());
        Assert.assertNotNull(command2.getModified());
        Assert.assertNotNull(command2.getCreated());
        Assert.assertEquals(1, command2.getAliasActions().size());

        // create new command
        CommandForm form3 = CommandForm
                .Builder
                .newBuilder()
                .addAliasAction(new AliasAction<Action>(
                        ALIAS2,
                        new HumidityActions(51)))
                .build();
        Command command3 = api.postNewCommand(form2);

        Assert.assertNotNull(command3.getCommandID());
        Assert.assertNull(command3.getTitle());
        Assert.assertNull(command3.getDescription());
        Assert.assertNull(command3.getMetadata());
        Assert.assertNull(command3.getAliasActionResults());
        Assert.assertNull(command3.getFiredByTriggerID());
        Assert.assertNotNull(command3.getModified());
        Assert.assertNotNull(command3.getCreated());
        Assert.assertEquals(1, command3.getAliasActions().size());

        // list commands
        Pair<List<Command>, String> results = api.listCommands(10, null);
        Assert.assertNull(results.second);
        Assert.assertEquals(3, results.first.size());

        for (Command command: results.first) {
            if (TextUtils.equals(command1.getCommandID(), command.getCommandID())) {
                command1 = command;
            }else if(TextUtils.equals(command2.getCommandID(), command.getCommandID())) {
                command2 = command;
            }else if(TextUtils.equals(command3.getCommandID(), command.getCommandID())) {
                command3 = command;
            }
        }

        Assert.assertNotNull(command1.getCommandID());
        Assert.assertEquals(commandTitle, command1.getTitle());
        Assert.assertEquals(commandDescription, command1.getDescription());
        Assert.assertNotNull(command1.getMetadata());
        Assert.assertEquals(metaData.toString(), command1.getMetadata().toString());
        Assert.assertEquals(CommandState.SENDING, command1.getCommandState());
        Assert.assertNotNull(command1.getCreated());
        Assert.assertNotNull(command1.getModified());
        Assert.assertNull(command1.getFiredByTriggerID());
        Assert.assertNull(command1.getAliasActionResults());

        Assert.assertEquals(2, command1.getAliasActions().size());

        Assert.assertEquals(ALIAS1, command1.getAliasActions().get(0).getAlias());
        action1 = command1.getAliasActions().get(0).getAction();
        Assert.assertTrue(action1 instanceof AirConditionerActions);
        Assert.assertEquals(
                true,
                ((AirConditionerActions)action1).isPower().booleanValue());
        Assert.assertEquals(
                25,
                ((AirConditionerActions)action1).getPresetTemperature().intValue());
        Assert.assertEquals(ALIAS2, command1.getAliasActions().get(1).getAlias());
        action2 = command1.getAliasActions().get(1).getAction();
        Assert.assertTrue(action2 instanceof HumidityActions);
        Assert.assertEquals(50, ((HumidityActions)action2).getPresetHumidity().intValue());
        Assert.assertNotNull(command2.getCommandID());
        Assert.assertNull(command2.getTitle());
        Assert.assertNull(command2.getDescription());
        Assert.assertNull(command2.getMetadata());
        Assert.assertNull(command2.getAliasActionResults());
        Assert.assertNull(command2.getFiredByTriggerID());
        Assert.assertNotNull(command2.getModified());
        Assert.assertNotNull(command2.getCreated());
        Assert.assertEquals(1, command2.getAliasActions().size());
        Assert.assertNotNull(command3.getCommandID());
        Assert.assertNull(command3.getTitle());
        Assert.assertNull(command3.getDescription());
        Assert.assertNull(command3.getMetadata());
        Assert.assertNull(command3.getAliasActionResults());
        Assert.assertNull(command3.getFiredByTriggerID());
        Assert.assertNotNull(command3.getModified());
        Assert.assertNotNull(command3.getCreated());
        Assert.assertEquals(1, command3.getAliasActions().size());

        // list commands with pagination key
        Pair<List<Command>, String> results2 = api.listCommands(2, null);
        Assert.assertNotNull(results2.second);
        Assert.assertEquals(2, results2.first.size());
        Pair<List<Command>, String> results3 = api.listCommands(10, results2.second);
        Assert.assertEquals(1, results3.first.size());
        Assert.assertNull(results3.second);
    }

    @Test
    public void listCommandsEmptyResultTest() throws Exception {
        ThingIFAPI api = this.createDefaultThingIFAPI();
        String vendorThingID = UUID.randomUUID().toString();
        String thingPassword = "password";

        // on-boarding thing
        OnboardWithVendorThingIDOptions options =
                new OnboardWithVendorThingIDOptions.Builder()
                        .setThingType(DEFAULT_THING_TYPE)
                        .setFirmwareVersion(DEFAULT_FIRMWARE_VERSION).build();
        Target target = api.onboardWithVendorThingID(vendorThingID, thingPassword, options);
        Assert.assertEquals(TypedID.Types.THING, target.getTypedID().getType());
        Assert.assertNotNull(target.getAccessToken());

        Pair<List<Command>, String> results = api.listCommands(10, null);
        Assert.assertNull(results.second);
        List<Command> commands = results.first;
        Assert.assertEquals(0, commands.size());
    }
}
