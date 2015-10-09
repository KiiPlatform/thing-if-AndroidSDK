package com.kii.iotcloud;

import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;
import android.util.Pair;

import com.kii.iotcloud.command.Action;
import com.kii.iotcloud.command.Command;
import com.kii.iotcloud.schema.LightState;
import com.kii.iotcloud.schema.SetBrightness;
import com.kii.iotcloud.schema.SetColor;
import com.kii.iotcloud.schema.SetColorTemperature;
import com.kii.iotcloud.schema.TurnPower;
import com.kii.iotcloud.trigger.Condition;
import com.kii.iotcloud.trigger.EventSource;
import com.kii.iotcloud.trigger.StatePredicate;
import com.kii.iotcloud.trigger.Trigger;
import com.kii.iotcloud.trigger.TriggersWhen;
import com.kii.iotcloud.trigger.clause.Equals;
import com.kii.iotcloud.trigger.clause.Range;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RunWith(AndroidJUnit4.class)
public class TriggerTest extends LargeTestCaseBase {
    @Test
    public void basicStatePredicateTriggerTest() throws Exception {
        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(TargetTestServer.DEV_SERVER_1);
        String vendorThingID = UUID.randomUUID().toString();
        String thingPassword = "password";

        // on-boarding thing
        Target target = api.onboard(vendorThingID, thingPassword, DEMO_THING_TYPE, null);
        Assert.assertEquals(TypedID.Types.THING, target.getTypedID().getType());
        Assert.assertNotNull(target.getAccessToken());

        // create new trigger
        List<Action> actions1 = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions1.add(setColor);
        actions1.add(setColorTemperature);
        Condition condition1 = new Condition(new Equals("power", true));
        StatePredicate predicate1 = new StatePredicate(condition1, TriggersWhen.CONDITION_TRUE);

        Trigger trigger1 = api.postNewTrigger(DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions1, predicate1);
        Assert.assertNotNull(trigger1.getTriggerID());
        Assert.assertFalse(trigger1.disabled());
        Assert.assertNull(trigger1.getDisabledReason());
        Assert.assertEquals(target.getTypedID(), trigger1.getTargetID());

        Command trigger1Command = trigger1.getCommand();
        Assert.assertNull(trigger1Command.getCommandID());
        Assert.assertEquals(DEMO_SCHEMA_NAME, trigger1Command.getSchemaName());
        Assert.assertEquals(DEMO_SCHEMA_VERSION, trigger1Command.getSchemaVersion());
        Assert.assertEquals(target.getTypedID(), trigger1Command.getTargetID());
        Assert.assertEquals(api.getOwner().getTypedID(), trigger1Command.getIssuerID());
        Assert.assertNull(trigger1Command.getCommandState());
        Assert.assertNull(trigger1Command.getFiredByTriggerID());
        Assert.assertNull(trigger1Command.getCreated());
        Assert.assertNull(trigger1Command.getModified());
        Assert.assertEquals(2, trigger1Command.getActions().size());
        Assert.assertEquals(setColor.getActionName(), trigger1Command.getActions().get(0).getActionName());
        Assert.assertArrayEquals(setColor.color, ((SetColor) trigger1Command.getActions().get(0)).color);
        Assert.assertEquals(setColorTemperature.getActionName(), trigger1Command.getActions().get(1).getActionName());
        Assert.assertEquals(setColorTemperature.colorTemperature, ((SetColorTemperature)trigger1Command.getActions().get(1)).colorTemperature);
        Assert.assertNull(trigger1Command.getActionResults());

        StatePredicate trigger1Predicate = (StatePredicate)trigger1.getPredicate();
        Assert.assertEquals(EventSource.STATES, trigger1Predicate.getEventSource());
        Assert.assertEquals(TriggersWhen.CONDITION_TRUE, trigger1Predicate.getTriggersWhen());
        Assert.assertEquals("power", ((Equals)trigger1Predicate.getCondition().getClause()).getField());
        Assert.assertEquals(Boolean.TRUE, ((Equals)trigger1Predicate.getCondition().getClause()).getValue());

        // disable/enable trigger
        trigger1 = api.enableTrigger(trigger1.getTriggerID(), false);
        Assert.assertTrue(trigger1.disabled());
        trigger1 = api.enableTrigger(trigger1.getTriggerID(), true);
        Assert.assertFalse(trigger1.disabled());

        // get target state (empty)
        LightState lightState = api.getTargetState(LightState.class);

        // create new trigger
        List<Action> actions2 = new ArrayList<Action>();
        SetBrightness setBrightness = new SetBrightness(50);
        TurnPower turnPower = new TurnPower(true);
        actions2.add(setBrightness);
        actions2.add(turnPower);
        Condition condition2 = new Condition(new Equals("power", false));
        StatePredicate predicate2 = new StatePredicate(condition2, TriggersWhen.CONDITION_CHANGED);

        Trigger trigger2 = api.postNewTrigger(DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions2, predicate2);
        Assert.assertNotNull(trigger2.getTriggerID());
        Assert.assertFalse(trigger2.disabled());
        Assert.assertNull(trigger2.getDisabledReason());
        Assert.assertEquals(target.getTypedID(), trigger2.getTargetID());

        Command trigger2Command = trigger2.getCommand();
        Assert.assertNull(trigger2Command.getCommandID());
        Assert.assertEquals(DEMO_SCHEMA_NAME, trigger2Command.getSchemaName());
        Assert.assertEquals(DEMO_SCHEMA_VERSION, trigger2Command.getSchemaVersion());
        Assert.assertEquals(target.getTypedID(), trigger2Command.getTargetID());
        Assert.assertEquals(api.getOwner().getTypedID(), trigger2Command.getIssuerID());
        Assert.assertNull(trigger2Command.getCommandState());
        Assert.assertNull(trigger2Command.getFiredByTriggerID());
        Assert.assertNull(trigger2Command.getCreated());
        Assert.assertNull(trigger2Command.getModified());
        Assert.assertEquals(2, trigger2Command.getActions().size());
        Assert.assertEquals(setBrightness.getActionName(), trigger2Command.getActions().get(0).getActionName());
        Assert.assertEquals(setBrightness.brightness, ((SetBrightness) trigger2Command.getActions().get(0)).brightness);
        Assert.assertEquals(turnPower.getActionName(), trigger2Command.getActions().get(1).getActionName());
        Assert.assertEquals(turnPower.power, ((TurnPower)trigger2Command.getActions().get(1)).power);
        Assert.assertNull(trigger2Command.getActionResults());

        StatePredicate trigger2Predicate = (StatePredicate)trigger2.getPredicate();
        Assert.assertEquals(EventSource.STATES, trigger2Predicate.getEventSource());
        Assert.assertEquals(TriggersWhen.CONDITION_CHANGED, trigger2Predicate.getTriggersWhen());
        Assert.assertEquals("power", ((Equals)trigger2Predicate.getCondition().getClause()).getField());
        Assert.assertEquals(Boolean.FALSE, ((Equals)trigger2Predicate.getCondition().getClause()).getValue());

        // list triggers
        Pair<List<Trigger>, String> results = api.listTriggers(100, null);
        Assert.assertNull(results.second);
        List<Trigger> triggers = results.first;
        Assert.assertEquals(2, triggers.size());

        // listing order is undefined
        for (Trigger trigger : triggers) {
            if (TextUtils.equals(trigger1.getTriggerID(), trigger.getTriggerID())) {
                trigger1 = trigger;
            } else if (TextUtils.equals(trigger2.getTriggerID(), trigger.getTriggerID())) {
                trigger2 = trigger;
            }
        }
        // assert trigger1
        Assert.assertNotNull(trigger1.getTriggerID());
        Assert.assertFalse(trigger1.disabled());
        Assert.assertNull(trigger1.getDisabledReason());
        Assert.assertEquals(target.getTypedID(), trigger1.getTargetID());

        trigger1Command = trigger1.getCommand();
        Assert.assertNull(trigger1Command.getCommandID());
        Assert.assertEquals(DEMO_SCHEMA_NAME, trigger1Command.getSchemaName());
        Assert.assertEquals(DEMO_SCHEMA_VERSION, trigger1Command.getSchemaVersion());
        Assert.assertEquals(target.getTypedID(), trigger1Command.getTargetID());
        Assert.assertEquals(api.getOwner().getTypedID(), trigger1Command.getIssuerID());
        Assert.assertNull(trigger1Command.getCommandState());
        Assert.assertNull(trigger1Command.getFiredByTriggerID());
        Assert.assertNull(trigger1Command.getCreated());
        Assert.assertNull(trigger1Command.getModified());
        Assert.assertEquals(2, trigger1Command.getActions().size());
        Assert.assertEquals(setColor.getActionName(), trigger1Command.getActions().get(0).getActionName());
        Assert.assertArrayEquals(setColor.color, ((SetColor) trigger1Command.getActions().get(0)).color);
        Assert.assertEquals(setColorTemperature.getActionName(), trigger1Command.getActions().get(1).getActionName());
        Assert.assertEquals(setColorTemperature.colorTemperature, ((SetColorTemperature)trigger1Command.getActions().get(1)).colorTemperature);
        Assert.assertNull(trigger1Command.getActionResults());

        trigger1Predicate = (StatePredicate)trigger1.getPredicate();
        Assert.assertEquals(EventSource.STATES, trigger1Predicate.getEventSource());
        Assert.assertEquals(TriggersWhen.CONDITION_TRUE, trigger1Predicate.getTriggersWhen());
        Assert.assertEquals("power", ((Equals)trigger1Predicate.getCondition().getClause()).getField());
        Assert.assertEquals(Boolean.TRUE, ((Equals)trigger1Predicate.getCondition().getClause()).getValue());

        // assert trigger2
        Assert.assertNotNull(trigger2.getTriggerID());
        Assert.assertFalse(trigger2.disabled());
        Assert.assertNull(trigger2.getDisabledReason());
        Assert.assertEquals(target.getTypedID(), trigger2.getTargetID());

        trigger2Command = trigger2.getCommand();
        Assert.assertNull(trigger2Command.getCommandID());
        Assert.assertEquals(DEMO_SCHEMA_NAME, trigger2Command.getSchemaName());
        Assert.assertEquals(DEMO_SCHEMA_VERSION, trigger2Command.getSchemaVersion());
        Assert.assertEquals(target.getTypedID(), trigger2Command.getTargetID());
        Assert.assertEquals(api.getOwner().getTypedID(), trigger2Command.getIssuerID());
        Assert.assertNull(trigger2Command.getCommandState());
        Assert.assertNull(trigger2Command.getFiredByTriggerID());
        Assert.assertNull(trigger2Command.getCreated());
        Assert.assertNull(trigger2Command.getModified());
        Assert.assertEquals(2, trigger2Command.getActions().size());
        Assert.assertEquals(setBrightness.getActionName(), trigger2Command.getActions().get(0).getActionName());
        Assert.assertEquals(setBrightness.brightness, ((SetBrightness) trigger2Command.getActions().get(0)).brightness);
        Assert.assertEquals(turnPower.getActionName(), trigger2Command.getActions().get(1).getActionName());
        Assert.assertEquals(turnPower.power, ((TurnPower)trigger2Command.getActions().get(1)).power);
        Assert.assertNull(trigger2Command.getActionResults());

        trigger2Predicate = (StatePredicate)trigger2.getPredicate();
        Assert.assertEquals(EventSource.STATES, trigger2Predicate.getEventSource());
        Assert.assertEquals(TriggersWhen.CONDITION_CHANGED, trigger2Predicate.getTriggersWhen());
        Assert.assertEquals("power", ((Equals)trigger2Predicate.getCondition().getClause()).getField());
        Assert.assertEquals(Boolean.FALSE, ((Equals)trigger2Predicate.getCondition().getClause()).getValue());

        // delete triiger
        api.deleteTrigger(trigger1.getTriggerID());

        // update trigger
        List<Action> actions3 = new ArrayList<Action>();
        SetBrightness setBrightness3 = new SetBrightness(100);
        TurnPower turnPower3 = new TurnPower(false);
        actions3.add(setBrightness3);
        actions3.add(turnPower3);
        Condition condition3 = new Condition(Range.greaterThan("brightness", 100));
        StatePredicate predicate3 = new StatePredicate(condition3, TriggersWhen.CONDITION_FALSE_TO_TRUE);
        api.patchTrigger(trigger2.getTriggerID(), DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions3, predicate3);

        // list triggers
        results = api.listTriggers(100, null);
        Assert.assertNull(results.second);
        triggers = results.first;
        Assert.assertEquals(1, triggers.size());
        Trigger updatedTriger2 = triggers.get(0);

        // assert updated trigger1
        Assert.assertEquals(trigger2.getTriggerID(), updatedTriger2.getTriggerID());
        Assert.assertFalse(updatedTriger2.disabled());
        Assert.assertNull(updatedTriger2.getDisabledReason());
        Assert.assertEquals(target.getTypedID(), updatedTriger2.getTargetID());

        Command updatedTrigger2Command = updatedTriger2.getCommand();
        Assert.assertNull(updatedTrigger2Command.getCommandID());
        Assert.assertEquals(DEMO_SCHEMA_NAME, updatedTrigger2Command.getSchemaName());
        Assert.assertEquals(DEMO_SCHEMA_VERSION, updatedTrigger2Command.getSchemaVersion());
        Assert.assertEquals(target.getTypedID(), updatedTrigger2Command.getTargetID());
        Assert.assertEquals(api.getOwner().getTypedID(), updatedTrigger2Command.getIssuerID());
        Assert.assertNull(updatedTrigger2Command.getCommandState());
        Assert.assertNull(updatedTrigger2Command.getFiredByTriggerID());
        Assert.assertNull(updatedTrigger2Command.getCreated());
        Assert.assertNull(updatedTrigger2Command.getModified());
        Assert.assertEquals(2, updatedTrigger2Command.getActions().size());
        Assert.assertEquals(setBrightness3.getActionName(), updatedTrigger2Command.getActions().get(0).getActionName());
        Assert.assertEquals(setBrightness3.brightness, ((SetBrightness) updatedTrigger2Command.getActions().get(0)).brightness);
        Assert.assertEquals(turnPower3.getActionName(), updatedTrigger2Command.getActions().get(1).getActionName());
        Assert.assertEquals(turnPower3.power, ((TurnPower)updatedTrigger2Command.getActions().get(1)).power);
        Assert.assertNull(updatedTrigger2Command.getActionResults());

        StatePredicate updatedTrigger2Predicate = (StatePredicate)updatedTriger2.getPredicate();
        Assert.assertEquals(EventSource.STATES, updatedTrigger2Predicate.getEventSource());
        Assert.assertEquals(TriggersWhen.CONDITION_FALSE_TO_TRUE, updatedTrigger2Predicate.getTriggersWhen());
        Assert.assertEquals("brightness", ((Range)updatedTrigger2Predicate.getCondition().getClause()).getField());
        Assert.assertEquals(100, (long)((Range)updatedTrigger2Predicate.getCondition().getClause()).getUpperLimit());
    }
    @Test
    public void listTriggersEmptyResultTest() throws Exception {
        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(TargetTestServer.DEV_SERVER_1);
        String vendorThingID = UUID.randomUUID().toString();
        String thingPassword = "password";

        // on-boarding thing
        Target target = api.onboard(vendorThingID, thingPassword, DEMO_THING_TYPE, null);
        Assert.assertEquals(TypedID.Types.THING, target.getTypedID().getType());
        Assert.assertNotNull(target.getAccessToken());

        Pair<List<Trigger>, String> results = api.listTriggers(100, null);
        Assert.assertNull(results.second);
        List<Trigger> triggers = results.first;
        Assert.assertEquals(0, triggers.size());
    }
}