package com.kii.thingif.trigger;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import com.kii.thingif.SmallTestBase;
import com.kii.thingif.TypedID;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.Command;
import com.kii.thingif.testschemas.SetColor;
import com.kii.thingif.testschemas.SetColorTemperature;
import com.kii.thingif.trigger.clause.Equals;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RunWith(AndroidJUnit4.class)
public class TriggerParcelableTest extends SmallTestBase {
    @Test
    public void commandTriggerTest() throws Exception {
        String schemaName = "TestSchema";
        int schemaVersion = 10;
        TypedID target = new TypedID(TypedID.Types.THING, "thing1234");
        TypedID issuer = new TypedID(TypedID.Types.USER, "user1234");
        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        Command command = new Command(schemaName, schemaVersion, target, issuer, actions);

        Equals equals = new Equals("power", true);
        Condition condition = new Condition(equals);
        StatePredicate predicate = new StatePredicate(condition, TriggersWhen.CONDITION_TRUE);

        String triggerID = "trigger1234";
        boolean disabled = true;
        String disabledReason = "reasonXXXX";
        String title = "Title of Trigger";
        String description = "Description of Trigger";
        JSONObject metadata = new JSONObject();
        metadata.put("sound", "phone.mp3");

        Trigger trigger = new Trigger(predicate, command);
        trigger.setTriggerID(triggerID);
        Whitebox.setInternalState(trigger, "disabled", disabled);
        trigger.setDisabledReason(disabledReason);
        Whitebox.setInternalState(trigger, "title", title);
        Whitebox.setInternalState(trigger, "description", description);
        Whitebox.setInternalState(trigger, "metadata", metadata);

        Parcel parcel = Parcel.obtain();
        trigger.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        Trigger deserializedTrigger = Trigger.CREATOR.createFromParcel(parcel);

        Assert.assertNull(deserializedTrigger.getServerCode());
        Assert.assertEquals(schemaName, deserializedTrigger.getCommand().getSchemaName());
        Assert.assertEquals(schemaVersion, deserializedTrigger.getCommand().getSchemaVersion());
        Assert.assertEquals(target, deserializedTrigger.getCommand().getTargetID());
        Assert.assertEquals(issuer, deserializedTrigger.getCommand().getIssuerID());
        Assert.assertEquals(2, deserializedTrigger.getCommand().getActions().size());
        Assert.assertArrayEquals(setColor.color, ((SetColor) deserializedTrigger.getCommand().getActions().get(0)).color);
        Assert.assertEquals(setColorTemperature.colorTemperature, ((SetColorTemperature) deserializedTrigger.getCommand().getActions().get(1)).colorTemperature);

        Assert.assertEquals(equals, ((StatePredicate)deserializedTrigger.getPredicate()).getCondition().getClause());
        Assert.assertEquals(TriggersWhen.CONDITION_TRUE, ((StatePredicate) deserializedTrigger.getPredicate()).getTriggersWhen());

        Assert.assertEquals(triggerID, deserializedTrigger.getTriggerID());
        Assert.assertEquals(disabled, deserializedTrigger.disabled());
        Assert.assertEquals(disabledReason, deserializedTrigger.getDisabledReason());
        Assert.assertEquals(title, deserializedTrigger.getTitle());
        Assert.assertEquals(description, deserializedTrigger.getDescription());
        assertJSONObject(metadata, deserializedTrigger.getMetadata());
    }
    @Test
    public void serverCodeTriggerTest() throws Exception {
        String schemaName = "TestSchema";
        int schemaVersion = 10;
        TypedID target = new TypedID(TypedID.Types.THING, "thing1234");
        TypedID issuer = new TypedID(TypedID.Types.USER, "user1234");

        String endpoint = "function_name";
        String executorAccessToken = UUID.randomUUID().toString();
        String targetAppID = UUID.randomUUID().toString().substring(0, 8);
        JSONObject parameters = new JSONObject("{\"name\":\"kii\", \"age\":30, \"enabled\":true}");
        ServerCode serverCode = new ServerCode(endpoint, executorAccessToken, targetAppID, parameters);

        Equals equals = new Equals("power", true);
        Condition condition = new Condition(equals);
        StatePredicate predicate = new StatePredicate(condition, TriggersWhen.CONDITION_TRUE);

        String triggerID = "trigger1234";
        boolean disabled = true;
        String disabledReason = "reasonXXXX";
        String title = "Title of Trigger";
        String description = "Description of Trigger";
        JSONObject metadata = new JSONObject();
        metadata.put("sound", "phone.mp3");

        Trigger trigger = new Trigger(predicate, serverCode);
        trigger.setTriggerID(triggerID);
        Whitebox.setInternalState(trigger, "disabled", disabled);
        trigger.setDisabledReason(disabledReason);
        Whitebox.setInternalState(trigger, "title", title);
        Whitebox.setInternalState(trigger, "description", description);
        Whitebox.setInternalState(trigger, "metadata", metadata);

        Parcel parcel = Parcel.obtain();
        trigger.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        Trigger deserializedTrigger = Trigger.CREATOR.createFromParcel(parcel);

        Assert.assertNull(deserializedTrigger.getCommand());
        Assert.assertEquals(endpoint, deserializedTrigger.getServerCode().getEndpoint());
        Assert.assertEquals(executorAccessToken, deserializedTrigger.getServerCode().getExecutorAccessToken());
        Assert.assertEquals(targetAppID, deserializedTrigger.getServerCode().getTargetAppID());
        assertJSONObject(parameters, deserializedTrigger.getServerCode().getParameters());

        Assert.assertEquals(equals, ((StatePredicate)deserializedTrigger.getPredicate()).getCondition().getClause());
        Assert.assertEquals(TriggersWhen.CONDITION_TRUE, ((StatePredicate) deserializedTrigger.getPredicate()).getTriggersWhen());

        Assert.assertEquals(triggerID, deserializedTrigger.getTriggerID());
        Assert.assertEquals(disabled, deserializedTrigger.disabled());
        Assert.assertEquals(disabledReason, deserializedTrigger.getDisabledReason());
        Assert.assertEquals(title, deserializedTrigger.getTitle());
        Assert.assertEquals(description, deserializedTrigger.getDescription());
        assertJSONObject(metadata, deserializedTrigger.getMetadata());
    }
}
