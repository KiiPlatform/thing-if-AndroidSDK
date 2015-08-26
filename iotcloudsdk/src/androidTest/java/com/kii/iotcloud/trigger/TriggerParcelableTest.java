package com.kii.iotcloud.trigger;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import com.kii.iotcloud.SmallTestBase;
import com.kii.iotcloud.TypedID;
import com.kii.iotcloud.command.Action;
import com.kii.iotcloud.command.Command;
import com.kii.iotcloud.testschemas.SetColor;
import com.kii.iotcloud.testschemas.SetColorTemperature;
import com.kii.iotcloud.trigger.clause.Equals;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class TriggerParcelableTest extends SmallTestBase {
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
        Command command = new Command(schemaName, schemaVersion, target, issuer, actions);

        Equals equals = new Equals("power", true);
        Condition condition = new Condition(equals);
        StatePredicate predicate = new StatePredicate(condition, TriggersWhen.CONDITION_TRUE);

        String triggerID = "trigger1234";
        boolean disabled = true;
        String disabledReason = "reasonXXXX";

        Trigger trigger = new Trigger(predicate, command);
        trigger.setTriggerID(triggerID);
        trigger.setDisabled(disabled);
        trigger.setDisabledReason(disabledReason);

        Parcel parcel = Parcel.obtain();
        trigger.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        Trigger deserializedTrigger = Trigger.CREATOR.createFromParcel(parcel);

        Assert.assertEquals(schemaName, deserializedTrigger.getCommand().getSchemaName());
        Assert.assertEquals(schemaVersion, deserializedTrigger.getCommand().getSchemaVersion());
        Assert.assertEquals(target, deserializedTrigger.getCommand().getTargetID());
        Assert.assertEquals(issuer, deserializedTrigger.getCommand().getIssuerID());
        Assert.assertEquals(2, deserializedTrigger.getCommand().getActions().size());
        Assert.assertArrayEquals(setColor.color, ((SetColor) deserializedTrigger.getCommand().getActions().get(0)).color);
        Assert.assertEquals(setColorTemperature.colorTemperature, ((SetColorTemperature) deserializedTrigger.getCommand().getActions().get(1)).colorTemperature);

        Assert.assertEquals(equals, ((StatePredicate)deserializedTrigger.getPredicate()).getCondition().getClause());
        Assert.assertEquals(TriggersWhen.CONDITION_TRUE, ((StatePredicate)deserializedTrigger.getPredicate()).getTriggersWhen());

        Assert.assertEquals(triggerID, deserializedTrigger.getTriggerID());
        Assert.assertEquals(disabled, deserializedTrigger.disabled());
        Assert.assertEquals(disabledReason, deserializedTrigger.getDisabledReason());
    }
}
