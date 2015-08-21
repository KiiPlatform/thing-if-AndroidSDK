package com.kii.iotcloud.command;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import com.kii.iotcloud.TypedID;
import com.kii.iotcloud.testmodel.SetColor;
import com.kii.iotcloud.testmodel.SetColorResult;
import com.kii.iotcloud.testmodel.SetColorTemperature;
import com.kii.iotcloud.testmodel.SetColorTemperatureResult;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class CommandParcelableTest {
    @Test
    public void test() throws Exception {
        String schemaName = "TestSchema";
        int schemaVersion = 10;
        TypedID target = new TypedID(TypedID.Types.THING, "thing1234");
        TypedID issuer = new TypedID(TypedID.Types.USER, "user1234");
        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        SetColorResult setColorResult = new SetColorResult(true);
        SetColorTemperatureResult setColorTemperatureResult = new SetColorTemperatureResult(false);

        String commandID = "command1234";
        CommandState commandState = CommandState.DELIVERED;
        String firedByTriggerID = "trigger1234";
        Long created = 123456789L;
        Long modified = 987654321L;

        Command command = new Command(schemaName, schemaVersion, target, issuer, actions);
        command.addActionResult(setColorResult);
        command.addActionResult(setColorTemperatureResult);
        command.setCommandID(commandID);
        command.setCommandState(commandState);
        command.setFiredByTriggerID(firedByTriggerID);
        command.setCreated(created);
        command.setModified(modified);

        Parcel parcel = Parcel.obtain();
        command.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        Command deserializedCommand = Command.CREATOR.createFromParcel(parcel);

        Assert.assertEquals(schemaName, deserializedCommand.getSchemaName());
        Assert.assertEquals(schemaVersion, deserializedCommand.getSchemaVersion());
        Assert.assertEquals(target, deserializedCommand.getTargetID());
        Assert.assertEquals(issuer, deserializedCommand.getIssuerID());
        Assert.assertEquals(2, deserializedCommand.getActions().size());
        Assert.assertArrayEquals(setColor.color, ((SetColor) deserializedCommand.getActions().get(0)).color);
        Assert.assertEquals(setColorTemperature.colorTemperature, ((SetColorTemperature) deserializedCommand.getActions().get(1)).colorTemperature);
        Assert.assertEquals(setColorResult.succeeded, ((SetColorResult)deserializedCommand.getActionResults().get(0)).succeeded);
        Assert.assertEquals(setColorTemperatureResult.succeeded, ((SetColorTemperatureResult)deserializedCommand.getActionResults().get(1)).succeeded);
        Assert.assertEquals(commandID, deserializedCommand.getCommandID());
        Assert.assertEquals(commandState, deserializedCommand.getCommandState());
        Assert.assertEquals(firedByTriggerID, deserializedCommand.getFiredByTriggerID());
        Assert.assertEquals(created, deserializedCommand.getCreated());
        Assert.assertEquals(modified, deserializedCommand.getModified());
    }
}
