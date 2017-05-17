package com.kii.thingiftrait.largetests;

import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;
import android.util.Pair;

import com.kii.thingiftrait.OnboardWithVendorThingIDOptions;
import com.kii.thingiftrait.Target;
import com.kii.thingiftrait.ThingIFAPI;
import com.kii.thingiftrait.TypedID;
import com.kii.thingiftrait.actions.SetPresetHumidity;
import com.kii.thingiftrait.actions.SetPresetTemperature;
import com.kii.thingiftrait.actions.TurnPower;
import com.kii.thingiftrait.command.Action;
import com.kii.thingiftrait.command.AliasAction;
import com.kii.thingiftrait.command.Command;
import com.kii.thingiftrait.command.CommandForm;
import com.kii.thingiftrait.command.CommandState;

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
        List<AliasAction> aliasActions = new ArrayList<>();
        List<Action> actions = new ArrayList<>();
        actions.add(new TurnPower(true));
        actions.add(new SetPresetTemperature(25));
        aliasActions.add(new AliasAction(ALIAS1, actions));
        List<Action> actions2 = new ArrayList<>();
        actions2.add(new SetPresetHumidity(50));
        aliasActions.add(new AliasAction(ALIAS2, actions2));

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
        Assert.assertEquals(2, command1.getAliasActions().get(0).getActions().size());
        Action action11 = command1.getAliasActions().get(0).getActions().get(0);
        Assert.assertTrue(action11 instanceof TurnPower);
        Assert.assertEquals(
                true,
                ((TurnPower)action11).getPower().booleanValue());
        Action action12 = command1.getAliasActions().get(0).getActions().get(1);
        Assert.assertEquals(
                25,
                ((SetPresetTemperature)action12).getTemperature().intValue());
        Assert.assertEquals(ALIAS2, command1.getAliasActions().get(1).getAlias());

        Assert.assertEquals(1, command1.getAliasActions().get(1).getActions().size());
        Action action21 = command1.getAliasActions().get(1).getActions().get(0);
        Assert.assertTrue(action21 instanceof SetPresetHumidity);
        Assert.assertEquals(50, ((SetPresetHumidity)action21).getHumidity().intValue());

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
        Assert.assertEquals(2, command1Copy.getAliasActions().get(0).getActions().size());
        Action action31 = command1Copy.getAliasActions().get(0).getActions().get(0);
        Assert.assertTrue(action31 instanceof TurnPower);
        Assert.assertEquals(
                true,
                ((TurnPower)action31).getPower().booleanValue());
        Action action32 = command1Copy.getAliasActions().get(0).getActions().get(1);
        Assert.assertTrue(action32 instanceof SetPresetTemperature);
        Assert.assertEquals(
                25,
                ((SetPresetTemperature)action32).getTemperature().intValue());
        
        Assert.assertEquals(ALIAS2, command1Copy.getAliasActions().get(1).getAlias());
        Assert.assertEquals(1, command1Copy.getAliasActions().get(1).getActions().size());
        Action action41 = command1Copy.getAliasActions().get(1).getActions().get(0);
        Assert.assertTrue(action41 instanceof SetPresetHumidity);
        Assert.assertEquals(50, ((SetPresetHumidity)action41).getHumidity().intValue());

        // create new command
        List<Action> actions21 = new ArrayList<>();
        actions21.add(new SetPresetHumidity(50));

        CommandForm form2 = CommandForm
                .Builder
                .newBuilder()
                .addAliasAction(new AliasAction(ALIAS2, actions21))
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
        List<Action> actions31 = new ArrayList<>();
        actions31.add(new SetPresetHumidity(51));
        CommandForm form3 = CommandForm
                .Builder
                .newBuilder()
                .addAliasAction(new AliasAction(ALIAS2, actions31))
                .build();
        Command command3 = api.postNewCommand(form3);

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

        // verify command1
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
        Assert.assertEquals(2, command1.getAliasActions().get(0).getActions().size());
        action11 = command1.getAliasActions().get(0).getActions().get(0);
        Assert.assertTrue(action11 instanceof TurnPower);
        Assert.assertEquals(
                true,
                ((TurnPower)action11).getPower().booleanValue());
        action12 = command1.getAliasActions().get(0).getActions().get(1);
        Assert.assertEquals(
                25,
                ((SetPresetTemperature)action12).getTemperature().intValue());
        Assert.assertEquals(ALIAS2, command1.getAliasActions().get(1).getAlias());
        Assert.assertEquals(1, command1.getAliasActions().get(1).getActions().size());
        action21 = command1.getAliasActions().get(1).getActions().get(0);
        Assert.assertTrue(action21 instanceof SetPresetHumidity);
        Assert.assertEquals(50, ((SetPresetHumidity)action21).getHumidity().intValue());

        // verify command2
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
