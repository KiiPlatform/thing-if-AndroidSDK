package com.kii.iotcloud.largetests;

import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;
import android.util.Pair;

import com.kii.iotcloud.IoTCloudAPI;
import com.kii.iotcloud.Target;
import com.kii.iotcloud.TypedID;
import com.kii.iotcloud.command.Action;
import com.kii.iotcloud.command.Command;
import com.kii.iotcloud.schema.SetBrightness;
import com.kii.iotcloud.schema.SetColor;
import com.kii.iotcloud.schema.SetColorTemperature;
import com.kii.iotcloud.schema.TurnPower;

import org.junit.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RunWith(AndroidJUnit4.class)
public class CommandTest extends LargeTestCaseBase {
    @Test
    public void basicTest() throws Exception {
        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(TargetTestServer.DEV_SERVER_1);
        String vendorThingID = UUID.randomUUID().toString();
        String thingPassword = "password";

        // on-boarding thing
        Target target = api.onboard(vendorThingID, thingPassword, DEMO_THING_TYPE, null);
        Assert.assertEquals(TypedID.Types.THING, target.getTypedID().getType());
        Assert.assertNotNull(target.getAccessToken());

        // create new command
        List<Action> actions1 = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions1.add(setColor);
        actions1.add(setColorTemperature);
        Command command1 = api.postNewCommand(DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions1);
        Assert.assertNotNull(command1.getCommandID());
        Assert.assertEquals(DEMO_SCHEMA_NAME, command1.getSchemaName());
        Assert.assertEquals(DEMO_SCHEMA_VERSION, command1.getSchemaVersion());
        Assert.assertEquals(target.getTypedID(), command1.getTargetID());
        Assert.assertEquals(api.getOwner().getTypedID(), command1.getIssuerID());
        Assert.assertNull(command1.getCommandState());
        Assert.assertNull(command1.getFiredByTriggerID());
        Assert.assertTrue(command1.getCreated() > 0);
        Assert.assertTrue(command1.getModified() > 0);
        Assert.assertEquals(2, command1.getActions().size());
        Assert.assertEquals(setColor.getActionName(), command1.getActions().get(0).getActionName());
        Assert.assertArrayEquals(setColor.color, ((SetColor) command1.getActions().get(0)).color);
        Assert.assertEquals(setColorTemperature.getActionName(), command1.getActions().get(1).getActionName());
        Assert.assertEquals(setColorTemperature.colorTemperature, ((SetColorTemperature)command1.getActions().get(1)).colorTemperature);
        Assert.assertNull(command1.getActionResults());
        // create new command
        List<Action> actions2 = new ArrayList<Action>();
        SetBrightness setBrightness = new SetBrightness(50);
        TurnPower turnPower = new TurnPower(true);
        actions2.add(setBrightness);
        actions2.add(turnPower);
        Command command2 = api.postNewCommand(DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions2);
        Assert.assertNotNull(command2.getCommandID());
        Assert.assertEquals(DEMO_SCHEMA_NAME, command2.getSchemaName());
        Assert.assertEquals(DEMO_SCHEMA_VERSION, command2.getSchemaVersion());
        Assert.assertEquals(target.getTypedID(), command2.getTargetID());
        Assert.assertEquals(api.getOwner().getTypedID(), command2.getIssuerID());
        Assert.assertNull(command2.getCommandState());
        Assert.assertNull(command2.getFiredByTriggerID());
        Assert.assertTrue(command2.getCreated() > command1.getCreated());
        Assert.assertTrue(command2.getModified() > command1.getModified());
        Assert.assertEquals(2, command2.getActions().size());
        Assert.assertEquals(setBrightness.getActionName(), command2.getActions().get(0).getActionName());
        Assert.assertEquals(setBrightness.brightness, ((SetBrightness)command2.getActions().get(0)).brightness);
        Assert.assertEquals(turnPower.getActionName(), command2.getActions().get(1).getActionName());
        Assert.assertEquals(turnPower.power, ((TurnPower)command2.getActions().get(1)).power);
        Assert.assertNull(command2.getActionResults());
        // list commands
        Pair<List<Command>, String> results = api.listCommands(100, null);
        Assert.assertNull(results.second);
        List<Command> commands = results.first;
        Assert.assertEquals(2, commands.size());

        // listing order is undefined
        for (Command command : commands) {
            if (TextUtils.equals(command1.getCommandID(), command.getCommandID())) {
                command1 = command;
            } else if (TextUtils.equals(command2.getCommandID(), command.getCommandID())) {
                command2 = command;
            }
        }

        Assert.assertNull(command1.getCommandState());
        Assert.assertNull(command1.getFiredByTriggerID());
        Assert.assertTrue(command1.getCreated() > 0);
        Assert.assertTrue(command1.getModified() > 0);
        Assert.assertEquals(2, command1.getActions().size());
        Assert.assertEquals(setColor.getActionName(), command1.getActions().get(0).getActionName());
        Assert.assertArrayEquals(setColor.color, ((SetColor) command1.getActions().get(0)).color);
        Assert.assertEquals(setColorTemperature.getActionName(), command1.getActions().get(1).getActionName());
        Assert.assertEquals(setColorTemperature.colorTemperature, ((SetColorTemperature) command1.getActions().get(1)).colorTemperature);
        Assert.assertNull(command1.getActionResults());
        Assert.assertNull(command2.getCommandState());
        Assert.assertNull(command2.getFiredByTriggerID());
        Assert.assertTrue(command2.getCreated() > command1.getCreated());
        Assert.assertTrue(command2.getModified() > command1.getModified());
        Assert.assertEquals(2, command2.getActions().size());
        Assert.assertEquals(setBrightness.getActionName(), command2.getActions().get(0).getActionName());
        Assert.assertEquals(setBrightness.brightness, ((SetBrightness) command2.getActions().get(0)).brightness);
        Assert.assertEquals(turnPower.getActionName(), command2.getActions().get(1).getActionName());
        Assert.assertEquals(turnPower.power, ((TurnPower)command2.getActions().get(1)).power);
        Assert.assertNull(command2.getActionResults());
    }
    @Test
    public void listCommandsEmptyResultTest() throws Exception {
        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(TargetTestServer.DEV_SERVER_1);
        String vendorThingID = UUID.randomUUID().toString();
        String thingPassword = "password";

        // on-boarding thing
        Target target = api.onboard(vendorThingID, thingPassword, DEMO_THING_TYPE, null);
        Assert.assertEquals(TypedID.Types.THING, target.getTypedID().getType());
        Assert.assertNotNull(target.getAccessToken());

        Pair<List<Command>, String> results = api.listCommands(10, null);
        Assert.assertNull(results.second);
        List<Command> commands = results.first;
        Assert.assertEquals(0, commands.size());
    }
}
