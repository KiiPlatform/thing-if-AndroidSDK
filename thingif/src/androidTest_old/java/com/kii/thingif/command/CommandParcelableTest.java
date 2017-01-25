package com.kii.thingif.command;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import com.kii.thingif.SmallTestBase;
import com.kii.thingif.TypedID;
import com.kii.thingif.testschemas.SetColor;
import com.kii.thingif.testschemas.SetColorResult;
import com.kii.thingif.testschemas.SetColorTemperature;
import com.kii.thingif.testschemas.SetColorTemperatureResult;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class CommandParcelableTest extends SmallTestBase {
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
        String title = "Title of Command";
        String description = "Description of Command";
        JSONObject metadata = new JSONObject();
        metadata.put("sound", "noisy.mp3");

        Command command = new Command(schemaName, schemaVersion, target, issuer, actions);
        command.addActionResult(setColorResult);
        command.addActionResult(setColorTemperatureResult);
        Whitebox.setInternalState(command, "commandID", commandID);
        Whitebox.setInternalState(command, "commandState", commandState);
        Whitebox.setInternalState(command, "firedByTriggerID", firedByTriggerID);
        Whitebox.setInternalState(command, "created", created);
        Whitebox.setInternalState(command, "modified", modified);
        Whitebox.setInternalState(command, "title", title);
        Whitebox.setInternalState(command, "description", description);
        Whitebox.setInternalState(command, "metadata", metadata);

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
        Assert.assertEquals(title, deserializedCommand.getTitle());
        Assert.assertEquals(description, deserializedCommand.getDescription());
        assertJSONObject(metadata, deserializedCommand.getMetadata());
    }
}
