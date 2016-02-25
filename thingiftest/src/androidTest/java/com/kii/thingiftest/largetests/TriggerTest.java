package com.kii.thingiftest.largetests;

import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;
import android.util.Pair;

import com.google.gson.JsonObject;
import com.kii.cloud.rest.client.KiiRest;
import com.kii.cloud.rest.client.model.KiiCredentials;
import com.kii.cloud.rest.client.model.storage.KiiThing;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.Target;
import com.kii.thingif.TypedID;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.Command;
import com.kii.thingif.trigger.Condition;
import com.kii.thingif.trigger.EventSource;
import com.kii.thingif.trigger.ServerCode;
import com.kii.thingif.trigger.StatePredicate;
import com.kii.thingif.trigger.Trigger;
import com.kii.thingif.trigger.TriggeredServerCodeResult;
import com.kii.thingif.trigger.TriggersWhen;
import com.kii.thingif.trigger.clause.Equals;
import com.kii.thingif.trigger.clause.Range;
import com.kii.thingiftest.schema.LightState;
import com.kii.thingiftest.schema.SetBrightness;
import com.kii.thingiftest.schema.SetColor;
import com.kii.thingiftest.schema.SetColorTemperature;
import com.kii.thingiftest.schema.TurnPower;

import org.json.JSONObject;
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
        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema();
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
        Assert.assertNull(trigger1.getServerCode());

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
        Assert.assertNull(trigger2.getServerCode());

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
        Assert.assertNull(trigger2.getServerCode());

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
        Assert.assertNull(trigger2.getServerCode());

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
        Assert.assertEquals(100, (long)((Range)updatedTrigger2Predicate.getCondition().getClause()).getLowerLimit());
    }
    @Test
    public void basicServerCodeTriggerTest() throws Exception {
        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema();
        String vendorThingID = UUID.randomUUID().toString();
        String thingPassword = "password";

        // on-boarding thing
        Target target = api.onboard(vendorThingID, thingPassword, DEMO_THING_TYPE, null);
        Assert.assertEquals(TypedID.Types.THING, target.getTypedID().getType());
        Assert.assertNotNull(target.getAccessToken());

        // create new trigger
        String endpoint1 = "my_function";
        String executorAccessToken1 = target.getAccessToken();
        String targetAppID1 = api.getAppID();
        JSONObject parameters1 = new JSONObject("{\"doAction\":true}");
        ServerCode serverCode1 = new ServerCode(endpoint1, executorAccessToken1, targetAppID1, parameters1);
        Condition condition1 = new Condition(new Equals("power", true));
        StatePredicate predicate1 = new StatePredicate(condition1, TriggersWhen.CONDITION_TRUE);

        Trigger trigger1 = api.postNewTrigger(serverCode1, predicate1);
        Assert.assertNotNull(trigger1.getTriggerID());
        Assert.assertFalse(trigger1.disabled());
        Assert.assertNull(trigger1.getDisabledReason());
        Assert.assertNull(trigger1.getTargetID());
        Assert.assertNull(trigger1.getCommand());

        ServerCode trigger1ServerCode = trigger1.getServerCode();

        Assert.assertEquals(endpoint1, trigger1ServerCode.getEndpoint());
        Assert.assertEquals(executorAccessToken1, trigger1ServerCode.getExecutorAccessToken());
        Assert.assertEquals(targetAppID1, trigger1ServerCode.getTargetAppID());
        assertJSONObject(parameters1, trigger1ServerCode.getParameters());

        StatePredicate trigger1Predicate = (StatePredicate)trigger1.getPredicate();
        Assert.assertEquals(EventSource.STATES, trigger1Predicate.getEventSource());
        Assert.assertEquals(TriggersWhen.CONDITION_TRUE, trigger1Predicate.getTriggersWhen());
        Assert.assertEquals("power", ((Equals)trigger1Predicate.getCondition().getClause()).getField());
        Assert.assertEquals(Boolean.TRUE, ((Equals)trigger1Predicate.getCondition().getClause()).getValue());

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
        Assert.assertNull(trigger1.getTargetID());
        Assert.assertNull(trigger1.getCommand());

        trigger1ServerCode = trigger1.getServerCode();

        Assert.assertEquals(endpoint1, trigger1ServerCode.getEndpoint());
        Assert.assertEquals(executorAccessToken1, trigger1ServerCode.getExecutorAccessToken());
        Assert.assertEquals(targetAppID1, trigger1ServerCode.getTargetAppID());
        assertJSONObject(parameters1, trigger1ServerCode.getParameters());

        trigger1Predicate = (StatePredicate)trigger1.getPredicate();
        Assert.assertEquals(EventSource.STATES, trigger1Predicate.getEventSource());
        Assert.assertEquals(TriggersWhen.CONDITION_TRUE, trigger1Predicate.getTriggersWhen());
        Assert.assertEquals("power", ((Equals)trigger1Predicate.getCondition().getClause()).getField());
        Assert.assertEquals(Boolean.TRUE, ((Equals) trigger1Predicate.getCondition().getClause()).getValue());

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
        Assert.assertEquals(turnPower.power, ((TurnPower) trigger2Command.getActions().get(1)).power);
        Assert.assertNull(trigger2Command.getActionResults());

        trigger2Predicate = (StatePredicate)trigger2.getPredicate();
        Assert.assertEquals(EventSource.STATES, trigger2Predicate.getEventSource());
        Assert.assertEquals(TriggersWhen.CONDITION_CHANGED, trigger2Predicate.getTriggersWhen());
        Assert.assertEquals("power", ((Equals)trigger2Predicate.getCondition().getClause()).getField());
        Assert.assertEquals(Boolean.FALSE, ((Equals) trigger2Predicate.getCondition().getClause()).getValue());

        // delete triiger
        api.deleteTrigger(trigger2.getTriggerID());

        // update trigger
        String endpoint2 = "my_function2";
        String executorAccessToken2 = target.getAccessToken() + "2";
        String targetAppID2 = api.getAppID() + "2";
        JSONObject parameters2 = new JSONObject("{\"doAction\":false}");
        ServerCode serverCode2 = new ServerCode(endpoint2, executorAccessToken2, targetAppID2, parameters2);
        Condition condition3 = new Condition(Range.greaterThan("brightness", 100));
        StatePredicate predicate3 = new StatePredicate(condition3, TriggersWhen.CONDITION_FALSE_TO_TRUE);
        api.patchTrigger(trigger1.getTriggerID(), serverCode2, predicate3);

        // list triggers
        results = api.listTriggers(100, null);
        Assert.assertNull(results.second);
        triggers = results.first;
        Assert.assertEquals(1, triggers.size());
        Trigger updatedTriger1 = triggers.get(0);

        // assert updated trigger1
        Assert.assertEquals(trigger1.getTriggerID(), updatedTriger1.getTriggerID());
        Assert.assertFalse(updatedTriger1.disabled());
        Assert.assertNull(updatedTriger1.getDisabledReason());
        Assert.assertNull(updatedTriger1.getTargetID());
        Assert.assertNull(updatedTriger1.getCommand());

        ServerCode updatedTrigger1ServerCode = updatedTriger1.getServerCode();

        Assert.assertEquals(endpoint2, updatedTrigger1ServerCode.getEndpoint());
        Assert.assertEquals(executorAccessToken2, updatedTrigger1ServerCode.getExecutorAccessToken());
        Assert.assertEquals(targetAppID2, updatedTrigger1ServerCode.getTargetAppID());
        assertJSONObject(parameters2, updatedTrigger1ServerCode.getParameters());

        StatePredicate updatedTrigger2Predicate = (StatePredicate)updatedTriger1.getPredicate();
        Assert.assertEquals(EventSource.STATES, updatedTrigger2Predicate.getEventSource());
        Assert.assertEquals(TriggersWhen.CONDITION_FALSE_TO_TRUE, updatedTrigger2Predicate.getTriggersWhen());
        Assert.assertEquals("brightness", ((Range)updatedTrigger2Predicate.getCondition().getClause()).getField());
        Assert.assertEquals(100, (long)((Range)updatedTrigger2Predicate.getCondition().getClause()).getLowerLimit());
    }
    @Test
    public void listTriggersEmptyResultTest() throws Exception {
        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema();
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
    @Test
    public void listTriggerServerCodeResultsTest() throws Exception {
        if (!this.server.hasAdminCredential()) {
            return;
        }
        // Deploy server code
        KiiRest rest = new KiiRest(this.server.getAppID(), this.server.getAppKey(), this.server.getBaseUrl() + "/api", this.server.getBaseUrl() + "/thing-if", this.server.getBaseUrl() + ":443/logs");
        KiiCredentials admin = rest.api().oauth().getAdminAccessToken(this.server.getClientId(), this.server.getClientSecret());
        rest.setCredentials(admin);

        StringBuilder javascript = new StringBuilder();
        javascript.append("function server_code_for_trigger(params, context){" + "\n");
        javascript.append("    return 100;" + "\n");
        javascript.append("}" + "\n");
        String versionID = rest.api().servercode().deploy(javascript.toString());
        rest.api().servercode().setCurrentVersion(versionID);

        // initialize ThingIFAPI
        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema();
        String vendorThingID = UUID.randomUUID().toString();
        String thingPassword = "password";

        // on-boarding thing
        Target target = api.onboard(vendorThingID, thingPassword, DEMO_THING_TYPE, null);
        Assert.assertEquals(TypedID.Types.THING, target.getTypedID().getType());
        Assert.assertNotNull(target.getAccessToken());

        // create new server code trigger
        String endpoint = "server_code_for_trigger";
        String executorAccessToken = target.getAccessToken();
        String targetAppID = api.getAppID();
        JSONObject parameters = new JSONObject("{\"arg1\":\"passed_parameter\"}");
        ServerCode serverCode = new ServerCode(endpoint, executorAccessToken, targetAppID, parameters);
        Condition condition = new Condition(new Equals("power", true));
        StatePredicate predicate = new StatePredicate(condition, TriggersWhen.CONDITION_TRUE);

        Trigger trigger = api.postNewTrigger(serverCode, predicate);
        Assert.assertNotNull(trigger.getTriggerID());
        Assert.assertFalse(trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertNull(trigger.getTargetID());
        Assert.assertNull(trigger.getCommand());

        Thread.sleep(3000);

        rest.setCredentials(new KiiCredentials(target.getAccessToken()));
        // update thing state in order to trigger the server code
        KiiThing targetThing = new KiiThing();
        targetThing.setThingID(target.getTypedID().getID());
        JsonObject thingState = new JsonObject();
        thingState.addProperty("power", false);
        rest.thingif().targets(targetThing).states().save(thingState);

        Thread.sleep(3000);

        thingState = new JsonObject();
        thingState.addProperty("power", true);
        rest.thingif().targets(targetThing).states().save(thingState);

        Thread.sleep(3000);

        Pair<List<TriggeredServerCodeResult>, String> triggerServerCodeResults = api.listTriggeredServerCodeResults(trigger.getTriggerID(), 0, null);
        Assert.assertEquals(1, triggerServerCodeResults.first.size());
        Assert.assertNull(triggerServerCodeResults.second);
        TriggeredServerCodeResult triggeredServerCodeResult = triggerServerCodeResults.first.get(0);
        Assert.assertTrue(triggeredServerCodeResult.isSucceeded());
        Assert.assertEquals(100, (int) triggeredServerCodeResult.getReturnedValueAsInteger());
        Assert.assertTrue(triggeredServerCodeResult.getExecutedAt() > 0);
        Assert.assertNull(triggeredServerCodeResult.getError());
    }
    @Test
    public void listTriggerServerCodeResultsWithErrorTest() throws Exception {
        if (!this.server.hasAdminCredential()) {
            return;
        }
        // Deploy server code
        KiiRest rest = new KiiRest(this.server.getAppID(), this.server.getAppKey(), this.server.getBaseUrl() + "/api", this.server.getBaseUrl() + "/thing-if", this.server.getBaseUrl() + ":443/logs");
        KiiCredentials admin = rest.api().oauth().getAdminAccessToken(this.server.getClientId(), this.server.getClientSecret());
        rest.setCredentials(admin);

        StringBuilder javascript = new StringBuilder();
        javascript.append("function server_code_for_trigger(params, context){" + "\n");
        javascript.append("    reference.error = 100;" + "\n");
        javascript.append("    return 100;" + "\n");
        javascript.append("}" + "\n");
        String versionID = rest.api().servercode().deploy(javascript.toString());
        rest.api().servercode().setCurrentVersion(versionID);

        // initialize ThingIFAPI
        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema();
        String vendorThingID = UUID.randomUUID().toString();
        String thingPassword = "password";

        // on-boarding thing
        Target target = api.onboard(vendorThingID, thingPassword, DEMO_THING_TYPE, null);
        Assert.assertEquals(TypedID.Types.THING, target.getTypedID().getType());
        Assert.assertNotNull(target.getAccessToken());

        // create new server code trigger
        String endpoint = "server_code_for_trigger";
        String executorAccessToken = target.getAccessToken();
        String targetAppID = api.getAppID();
        JSONObject parameters = new JSONObject("{\"arg1\":\"passed_parameter\"}");
        ServerCode serverCode = new ServerCode(endpoint, executorAccessToken, targetAppID, parameters);
        Condition condition = new Condition(new Equals("power", true));
        StatePredicate predicate = new StatePredicate(condition, TriggersWhen.CONDITION_TRUE);

        Trigger trigger = api.postNewTrigger(serverCode, predicate);
        Assert.assertNotNull(trigger.getTriggerID());
        Assert.assertFalse(trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertNull(trigger.getTargetID());
        Assert.assertNull(trigger.getCommand());

        Thread.sleep(3000);

        rest.setCredentials(new KiiCredentials(target.getAccessToken()));
        // update thing state in order to trigger the server code
        KiiThing targetThing = new KiiThing();
        targetThing.setThingID(target.getTypedID().getID());
        JsonObject thingState = new JsonObject();
        thingState.addProperty("power", false);
        rest.thingif().targets(targetThing).states().save(thingState);

        Thread.sleep(3000);

        thingState = new JsonObject();
        thingState.addProperty("power", true);
        rest.thingif().targets(targetThing).states().save(thingState);

        Thread.sleep(3000);

        Pair<List<TriggeredServerCodeResult>, String> triggerServerCodeResults = api.listTriggeredServerCodeResults(trigger.getTriggerID(), 0, null);
        Assert.assertEquals(1, triggerServerCodeResults.first.size());
        Assert.assertNull(triggerServerCodeResults.second);
        TriggeredServerCodeResult triggeredServerCodeResult = triggerServerCodeResults.first.get(0);
        Assert.assertFalse(triggeredServerCodeResult.isSucceeded());
        Assert.assertNull(triggeredServerCodeResult.getReturnedValue());
        Assert.assertTrue(triggeredServerCodeResult.getExecutedAt() > 0);
        Assert.assertNotNull(triggeredServerCodeResult.getError());
        Assert.assertEquals("Error found while executing the developer-defined code", triggeredServerCodeResult.getError().getErrorMessage());
        Assert.assertEquals("RUNTIME_ERROR", triggeredServerCodeResult.getError().getErrorCode());
        Assert.assertEquals("reference is not defined", triggeredServerCodeResult.getError().getDetailMessage());
    }
}