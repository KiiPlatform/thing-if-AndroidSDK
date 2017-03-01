package com.kii.thingif.largetests;

import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;
import android.util.Pair;

import com.google.gson.JsonObject;
import com.kii.cloud.rest.client.KiiRest;
import com.kii.cloud.rest.client.model.KiiCredentials;
import com.kii.cloud.rest.client.model.storage.KiiThing;
import com.kii.thingif.OnboardWithVendorThingIDOptions;
import com.kii.thingif.Target;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.TypedID;
import com.kii.thingif.actions.AirConditionerActions;
import com.kii.thingif.actions.HumidityActions;
import com.kii.thingif.clause.trigger.EqualsClauseInTrigger;
import com.kii.thingif.clause.trigger.RangeClauseInTrigger;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.AliasAction;
import com.kii.thingif.command.Command;
import com.kii.thingif.exception.BadRequestException;
import com.kii.thingif.trigger.Condition;
import com.kii.thingif.trigger.EventSource;
import com.kii.thingif.trigger.ScheduleOncePredicate;
import com.kii.thingif.trigger.SchedulePredicate;
import com.kii.thingif.trigger.ServerCode;
import com.kii.thingif.trigger.StatePredicate;
import com.kii.thingif.trigger.Trigger;
import com.kii.thingif.trigger.TriggerOptions;
import com.kii.thingif.trigger.TriggeredCommandForm;
import com.kii.thingif.trigger.TriggeredServerCodeResult;
import com.kii.thingif.trigger.TriggersWhen;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RunWith(AndroidJUnit4.class)
public class TriggerTest extends LargeTestCaseBase{

    private  ThingIFAPI onboardedApi;
    @Before
    public void before() throws Exception{
        super.before();
        this.onboardedApi = this.createDefaultThingIFAPI();
        String vendorThingID = UUID.randomUUID().toString();
        String thingPassword = "password";
        // on-boarding thing
        OnboardWithVendorThingIDOptions options =
                new OnboardWithVendorThingIDOptions.Builder()
                        .setThingType(DEFAULT_THING_TYPE)
                        .setFirmwareVersion(DEFAULT_FIRMWARE_VERSION).build();
        Target target = this.onboardedApi.onboardWithVendorThingID(vendorThingID, thingPassword, options);
        Assert.assertEquals(TypedID.Types.THING, target.getTypedID().getType());
        Assert.assertNotNull(target.getAccessToken());
    }
    @Test
    public void commandTriggerTest() throws Exception {
        Target target = this.onboardedApi.getTarget();
        Assert.assertNotNull(target);

        // create trigger 1: command,  state predicate, null options
        List<AliasAction<? extends Action>> aliasActions = new ArrayList<>();
        aliasActions.add(
                new AliasAction<>(
                        ALIAS1,
                        new AirConditionerActions(true, 25)));
        Condition condition1 = new Condition(new EqualsClauseInTrigger(ALIAS1, "power", true));
        StatePredicate predicate1 = new StatePredicate(condition1, TriggersWhen.CONDITION_TRUE);

        TriggeredCommandForm form = TriggeredCommandForm.Builder.newBuilder(aliasActions).build();

        Trigger trigger1 = this.onboardedApi.postNewTrigger(form, predicate1);
        Assert.assertNotNull(trigger1.getTriggerID());
        Assert.assertFalse(trigger1.disabled());
        Assert.assertNull(trigger1.getDisabledReason());
        Assert.assertEquals(target.getTypedID(), trigger1.getTargetID());
        Assert.assertNull(trigger1.getServerCode());

        Command trigger1Command = trigger1.getCommand();
        Assert.assertNotNull(trigger1Command);
        Assert.assertNull(trigger1Command.getCommandID());
        Assert.assertEquals(target.getTypedID(), trigger1Command.getTargetID());
        Assert.assertEquals(this.onboardedApi.getOwner().getTypedID(), trigger1Command.getIssuerID());
        Assert.assertNull(trigger1Command.getCommandState());
        Assert.assertNull(trigger1Command.getFiredByTriggerID());
        Assert.assertNull(trigger1Command.getCreated());
        Assert.assertNull(trigger1Command.getModified());

        Assert.assertEquals(1, trigger1Command.getAliasActions().size());

        Assert.assertEquals(ALIAS1, trigger1Command.getAliasActions().get(0).getAlias());
        Action action1 = trigger1Command.getAliasActions().get(0).getAction();
        Assert.assertTrue(action1 instanceof AirConditionerActions);
        Assert.assertTrue(((AirConditionerActions)action1).isPower());
        Assert.assertEquals(25, ((AirConditionerActions)action1).getPresetTemperature().intValue());

        StatePredicate trigger1Predicate = (StatePredicate)trigger1.getPredicate();
        Assert.assertEquals(EventSource.STATES, trigger1Predicate.getEventSource());
        Assert.assertEquals(TriggersWhen.CONDITION_TRUE, trigger1Predicate.getTriggersWhen());
        Assert.assertEquals("power", ((EqualsClauseInTrigger)trigger1Predicate.getCondition().getClause()).getField());
        Assert.assertEquals(Boolean.TRUE, ((EqualsClauseInTrigger)trigger1Predicate.getCondition().getClause()).getValue());

        // disable/enable trigger 1
        trigger1 = this.onboardedApi.enableTrigger(trigger1.getTriggerID(), false);
        Assert.assertTrue(trigger1.disabled());
        trigger1 = this.onboardedApi.enableTrigger(trigger1.getTriggerID(), true);
        Assert.assertFalse(trigger1.disabled());

        // create trigger 2: command, schedule predicate, non null options
        List<AliasAction<? extends Action>> aliasActions2 = new ArrayList<>();
        aliasActions2.add(
                new AliasAction<Action>(
                        ALIAS2,
                        new HumidityActions(50)));
        SchedulePredicate predicate2 = new SchedulePredicate("5 * * * *");
        TriggerOptions options2 = TriggerOptions.Builder.newBuilder()
                .setTitle("trigger title")
                .setDescription("trigger description")
                .setMetadata(new JSONObject().put("key", "value")).build();
        Trigger trigger2 = this.onboardedApi.postNewTrigger(
                TriggeredCommandForm.Builder.newBuilder(aliasActions2).build(),
                predicate2,
                options2);

        Assert.assertNotNull(trigger2.getTriggerID());
        Assert.assertFalse(trigger2.disabled());
        Assert.assertNull(trigger2.getDisabledReason());
        Assert.assertEquals(target.getTypedID(), trigger2.getTargetID());
        Assert.assertNull(trigger2.getServerCode());
        Assert.assertEquals(options2.getTitle(), trigger2.getTitle());
        Assert.assertEquals(options2.getDescription(), trigger2.getDescription());
        Assert.assertNotNull(options2.getMetadata());
        Assert.assertNotNull(trigger2.getMetadata());
        Assert.assertEquals(options2.getMetadata().toString(), trigger2.getMetadata().toString());

        Command trigger2Command = trigger2.getCommand();
        Assert.assertNotNull(trigger2Command);
        Assert.assertNull(trigger2Command.getCommandID());
        Assert.assertEquals(target.getTypedID(), trigger2Command.getTargetID());
        Assert.assertEquals(this.onboardedApi.getOwner().getTypedID(), trigger2Command.getIssuerID());
        Assert.assertNull(trigger2Command.getCommandState());
        Assert.assertNull(trigger2Command.getFiredByTriggerID());
        Assert.assertNull(trigger2Command.getCreated());
        Assert.assertNull(trigger2Command.getModified());
        Assert.assertEquals(1, trigger2Command.getAliasActions().size());

        Assert.assertEquals(ALIAS2, trigger2Command.getAliasActions().get(0).getAlias());
        Action action2 = trigger2Command.getAliasActions().get(0).getAction();
        Assert.assertTrue(action2 instanceof HumidityActions);
        Assert.assertEquals(50, ((HumidityActions)action2).getPresetHumidity().intValue());
        Assert.assertNull(trigger2Command.getAliasActionResults());
        Assert.assertNull(trigger2Command.getAliasActionResults());

        SchedulePredicate trigger2Predicate = (SchedulePredicate) trigger2.getPredicate();
        Assert.assertEquals("5 * * * *", trigger2Predicate.getSchedule());

        // disable trigger 2
        trigger2 = this.onboardedApi.enableTrigger(trigger2.getTriggerID(), false);
        Assert.assertTrue(trigger2.disabled());

        // update trigger 2
        List<AliasAction<? extends Action>> aliasActions21 = new ArrayList<>();
        aliasActions21.add(new AliasAction<Action>(ALIAS1, new AirConditionerActions(false, null)));
        SchedulePredicate trigger2Predicate2 = new SchedulePredicate("7 * * * *");
        trigger2 = this.onboardedApi.patchCommandTrigger(
                trigger2.getTriggerID(),
                TriggeredCommandForm.Builder.newBuilder(aliasActions21).build(),
                trigger2Predicate2);

        Assert.assertNotNull(trigger2.getTriggerID());
        Assert.assertTrue(trigger2.disabled());
        Assert.assertNull(trigger2.getDisabledReason());
        Assert.assertEquals(target.getTypedID(), trigger2.getTargetID());
        Assert.assertNull(trigger2.getServerCode());
        Assert.assertEquals(options2.getTitle(), trigger2.getTitle());
        Assert.assertEquals(options2.getDescription(), trigger2.getDescription());
        Assert.assertNotNull(options2.getMetadata());
        Assert.assertNotNull(trigger2.getMetadata());
        Assert.assertEquals(options2.getMetadata().toString(), trigger2.getMetadata().toString());

        trigger2Command = trigger2.getCommand();
        Assert.assertNotNull(trigger2Command);
        Assert.assertNull(trigger2Command.getCommandID());
        Assert.assertEquals(target.getTypedID(), trigger2Command.getTargetID());
        Assert.assertEquals(this.onboardedApi.getOwner().getTypedID(), trigger2Command.getIssuerID());
        Assert.assertNull(trigger2Command.getCommandState());
        Assert.assertNull(trigger2Command.getFiredByTriggerID());
        Assert.assertNull(trigger2Command.getCreated());
        Assert.assertNull(trigger2Command.getModified());
        Assert.assertEquals(1, trigger2Command.getAliasActions().size());

        Assert.assertEquals(ALIAS1, trigger2Command.getAliasActions().get(0).getAlias());
        action2 = trigger2Command.getAliasActions().get(0).getAction();
        Assert.assertTrue(action2 instanceof AirConditionerActions);
        Assert.assertFalse(((AirConditionerActions)action2).isPower());
        Assert.assertNull(((AirConditionerActions) action2).getPresetTemperature());
        Assert.assertNull(trigger2Command.getAliasActionResults());
        Assert.assertNull(trigger2Command.getAliasActionResults());

        trigger2Predicate = (SchedulePredicate) trigger2.getPredicate();
        Assert.assertEquals(trigger2Predicate2.getSchedule(), trigger2Predicate.getSchedule());

        // create trigger 3: command, schedule once predicate, null options
        List<AliasAction<? extends Action>> aliasActions3 = new ArrayList<>();
        aliasActions3.add(
                new AliasAction<Action>(
                        ALIAS2,
                        new HumidityActions(50)));
        ScheduleOncePredicate predicate3 = new ScheduleOncePredicate(System.currentTimeMillis()+ 1000*1000);

        Trigger trigger3 = this.onboardedApi.postNewTrigger(
                TriggeredCommandForm.Builder.newBuilder(aliasActions3).build(),
                predicate3,
                null);
        Assert.assertNotNull(trigger3.getTriggerID());
        Assert.assertFalse(trigger3.disabled());
        Assert.assertNull(trigger3.getDisabledReason());
        Assert.assertEquals(target.getTypedID(), trigger3.getTargetID());
        Assert.assertNull(trigger3.getServerCode());

        Command trigger3Command = trigger3.getCommand();
        Assert.assertNotNull(trigger3Command);
        Assert.assertNull(trigger3Command.getCommandID());
        Assert.assertEquals(target.getTypedID(), trigger3Command.getTargetID());
        Assert.assertEquals(this.onboardedApi.getOwner().getTypedID(), trigger3Command.getIssuerID());
        Assert.assertNull(trigger3Command.getCommandState());
        Assert.assertNull(trigger3Command.getFiredByTriggerID());
        Assert.assertNull(trigger3Command.getCreated());
        Assert.assertNull(trigger3Command.getModified());
        Assert.assertEquals(1, trigger3Command.getAliasActions().size());

        Assert.assertEquals(ALIAS2, trigger3Command.getAliasActions().get(0).getAlias());
        Action action3 = trigger3Command.getAliasActions().get(0).getAction();
        Assert.assertTrue(action3 instanceof HumidityActions);
        Assert.assertEquals(50, ((HumidityActions)action3).getPresetHumidity().intValue());
        Assert.assertNull(trigger3Command.getAliasActionResults());
        Assert.assertNull(trigger3Command.getAliasActionResults());

        ScheduleOncePredicate trigger3Predicate = (ScheduleOncePredicate) trigger3.getPredicate();
        Assert.assertEquals(predicate3.getScheduleAt(), trigger3Predicate.getScheduleAt());


        // create trigger 4, same as trigger 1
        Trigger trigger4 = this.onboardedApi.postNewTrigger(form, predicate1);
        Assert.assertNotNull(trigger4.getTriggerID());
        Pair<List<Trigger>, String> results = this.onboardedApi.listTriggers(0, null);
        Assert.assertEquals(4, results.first.size());
        Assert.assertNull(results.second);

        // delete trigger 4
        this.onboardedApi.deleteTrigger(trigger4.getTriggerID());

        // list trigger again for the first 5 triggers
        Pair<List<Trigger>, String> results1 = this.onboardedApi.listTriggers(2, null);
        Assert.assertNotNull(results1.second);
        Assert.assertEquals(2, results1.first.size());

        // list triggers for the rest 1 trigger
        Pair<List<Trigger>, String> results2 = this.onboardedApi.listTriggers(0, results1.second);
        Assert.assertEquals(1, results2.first.size());

        List<Trigger> allTriggers = new ArrayList<>();
        allTriggers.addAll(results1.first);
        allTriggers.addAll(results2.first);


        // listing order is undefined
        for (Trigger trigger : allTriggers) {
            if (TextUtils.equals(trigger1.getTriggerID(), trigger.getTriggerID())) {
                trigger1 = trigger;
            } else if (TextUtils.equals(trigger2.getTriggerID(), trigger.getTriggerID())) {
                trigger2 = trigger;
            } else if (TextUtils.equals(trigger3.getTriggerID(), trigger.getTriggerID())) {
                trigger3 = trigger;
            }else if (TextUtils.equals(trigger4.getTriggerID(), trigger.getTriggerID())) {
                Assert.fail("delete trigger should not be in resutls");
            }
        }

        // trigger 1
        Assert.assertNotNull(trigger1.getTriggerID());
        Assert.assertFalse(trigger1.disabled());
        Assert.assertNull(trigger1.getDisabledReason());
        Assert.assertEquals(target.getTypedID(), trigger1.getTargetID());
        Assert.assertNull(trigger1.getServerCode());

        trigger1Command = trigger1.getCommand();
        Assert.assertNotNull(trigger1Command);
        Assert.assertNull(trigger1Command.getCommandID());
        Assert.assertEquals(target.getTypedID(), trigger1Command.getTargetID());
        Assert.assertEquals(this.onboardedApi.getOwner().getTypedID(), trigger1Command.getIssuerID());
        Assert.assertNull(trigger1Command.getCommandState());
        Assert.assertNull(trigger1Command.getFiredByTriggerID());
        Assert.assertNull(trigger1Command.getCreated());
        Assert.assertNull(trigger1Command.getModified());

        Assert.assertEquals(1, trigger1Command.getAliasActions().size());

        Assert.assertEquals(ALIAS1, trigger1Command.getAliasActions().get(0).getAlias());
        action1 = trigger1Command.getAliasActions().get(0).getAction();
        Assert.assertTrue(action1 instanceof AirConditionerActions);
        Assert.assertTrue(((AirConditionerActions)action1).isPower());
        Assert.assertEquals(25, ((AirConditionerActions)action1).getPresetTemperature().intValue());

        trigger1Predicate = (StatePredicate)trigger1.getPredicate();
        Assert.assertEquals(EventSource.STATES, trigger1Predicate.getEventSource());
        Assert.assertEquals(TriggersWhen.CONDITION_TRUE, trigger1Predicate.getTriggersWhen());
        Assert.assertEquals("power", ((EqualsClauseInTrigger)trigger1Predicate.getCondition().getClause()).getField());
        Assert.assertEquals(Boolean.TRUE, ((EqualsClauseInTrigger)trigger1Predicate.getCondition().getClause()).getValue());

        // trigger 2
        Assert.assertNotNull(trigger2.getTriggerID());
        Assert.assertTrue(trigger2.disabled());
        Assert.assertNull(trigger2.getDisabledReason());
        Assert.assertEquals(target.getTypedID(), trigger2.getTargetID());
        Assert.assertNull(trigger2.getServerCode());
        Assert.assertEquals(options2.getTitle(), trigger2.getTitle());
        Assert.assertEquals(options2.getDescription(), trigger2.getDescription());
        Assert.assertNotNull(options2.getMetadata());
        Assert.assertNotNull(trigger2.getMetadata());
        Assert.assertEquals(options2.getMetadata().toString(), trigger2.getMetadata().toString());

        trigger2Command = trigger2.getCommand();
        Assert.assertNotNull(trigger2Command);
        Assert.assertNull(trigger2Command.getCommandID());
        Assert.assertEquals(target.getTypedID(), trigger2Command.getTargetID());
        Assert.assertEquals(this.onboardedApi.getOwner().getTypedID(), trigger2Command.getIssuerID());
        Assert.assertNull(trigger2Command.getCommandState());
        Assert.assertNull(trigger2Command.getFiredByTriggerID());
        Assert.assertNull(trigger2Command.getCreated());
        Assert.assertNull(trigger2Command.getModified());
        Assert.assertEquals(1, trigger2Command.getAliasActions().size());

        Assert.assertEquals(ALIAS1, trigger2Command.getAliasActions().get(0).getAlias());
        action2 = trigger2Command.getAliasActions().get(0).getAction();
        Assert.assertTrue(action2 instanceof AirConditionerActions);
        Assert.assertFalse(((AirConditionerActions)action2).isPower());
        Assert.assertNull(((AirConditionerActions) action2).getPresetTemperature());
        Assert.assertNull(trigger2Command.getAliasActionResults());
        Assert.assertNull(trigger2Command.getAliasActionResults());

        trigger2Predicate = (SchedulePredicate) trigger2.getPredicate();
        Assert.assertEquals(trigger2Predicate2.getSchedule(), trigger2Predicate.getSchedule());

        // trigger 3
        Assert.assertNotNull(trigger3.getTriggerID());
        Assert.assertFalse(trigger3.disabled());
        Assert.assertNull(trigger3.getDisabledReason());
        Assert.assertEquals(target.getTypedID(), trigger3.getTargetID());
        Assert.assertNull(trigger3.getServerCode());

        trigger3Command = trigger3.getCommand();
        Assert.assertNotNull(trigger3Command);
        Assert.assertNull(trigger3Command.getCommandID());
        Assert.assertEquals(target.getTypedID(), trigger3Command.getTargetID());
        Assert.assertEquals(this.onboardedApi.getOwner().getTypedID(), trigger3Command.getIssuerID());
        Assert.assertNull(trigger3Command.getCommandState());
        Assert.assertNull(trigger3Command.getFiredByTriggerID());
        Assert.assertNull(trigger3Command.getCreated());
        Assert.assertNull(trigger3Command.getModified());
        Assert.assertEquals(1, trigger3Command.getAliasActions().size());

        Assert.assertEquals(ALIAS2, trigger3Command.getAliasActions().get(0).getAlias());
        action3 = trigger3Command.getAliasActions().get(0).getAction();
        Assert.assertTrue(action3 instanceof HumidityActions);
        Assert.assertEquals(50, ((HumidityActions)action3).getPresetHumidity().intValue());
        Assert.assertNull(trigger3Command.getAliasActionResults());
        Assert.assertNull(trigger3Command.getAliasActionResults());

        trigger3Predicate = (ScheduleOncePredicate) trigger3.getPredicate();
        Assert.assertEquals(predicate3.getScheduleAt(), trigger3Predicate.getScheduleAt());
    }

    @Test
    public void serverCodeTriggerTest() throws Exception{
        Target target = this.onboardedApi.getTarget();
        Assert.assertNotNull(target);
        Assert.assertNotNull(target.getAccessToken());

        // create trigger: server code, state predicate, non null options
        String endpoint1 = "my_function";
        String executorAccessToken1 = target.getAccessToken();
        String targetAppID1 = this.onboardedApi.getAppID();
        JSONObject parameters1 = new JSONObject("{\"doAction\":true}");
        ServerCode serverCode1 = new ServerCode(endpoint1, executorAccessToken1, targetAppID1, parameters1);
        Condition condition4 = new Condition(new EqualsClauseInTrigger(ALIAS1, "power", true));
        StatePredicate predicate4 = new StatePredicate(condition4, TriggersWhen.CONDITION_TRUE);
        TriggerOptions options4 = TriggerOptions.Builder.newBuilder()
                .setTitle("trigger title")
                .setMetadata(new JSONObject().put("k", "v"))
                .build();

        Trigger trigger4 = this.onboardedApi.postNewTrigger(serverCode1, predicate4, options4);
        Assert.assertNotNull(trigger4.getTriggerID());
        Assert.assertFalse(trigger4.disabled());
        Assert.assertNull(trigger4.getDisabledReason());
        Assert.assertNull(trigger4.getCommand());
        Assert.assertEquals(options4.getTitle(), trigger4.getTitle());
        Assert.assertNull(trigger4.getDescription());
        Assert.assertNotNull(options4.getMetadata());
        Assert.assertNotNull(trigger4.getMetadata());
        Assert.assertEquals(options4.getMetadata().toString(), trigger4.getMetadata().toString());

        ServerCode trigger4ServerCode = trigger4.getServerCode();

        Assert.assertNotNull(trigger4ServerCode);
        Assert.assertEquals(endpoint1, trigger4ServerCode.getEndpoint());
        Assert.assertEquals(executorAccessToken1, trigger4ServerCode.getExecutorAccessToken());
        Assert.assertEquals(targetAppID1, trigger4ServerCode.getTargetAppID());
        assertJSONObject(parameters1, trigger4ServerCode.getParameters());

        StatePredicate trigger4Predicate = (StatePredicate)trigger4.getPredicate();
        Assert.assertEquals(EventSource.STATES, trigger4Predicate.getEventSource());
        Assert.assertEquals(TriggersWhen.CONDITION_TRUE, trigger4Predicate.getTriggersWhen());
        Assert.assertEquals("power", ((EqualsClauseInTrigger)trigger4Predicate.getCondition().getClause()).getField());
        Assert.assertEquals(Boolean.TRUE, ((EqualsClauseInTrigger)trigger4Predicate.getCondition().getClause()).getValue());

        // update trigger
        ServerCode serverCode11 = new ServerCode("my_function2", null);
        StatePredicate predicate41 = new StatePredicate(
                new Condition(RangeClauseInTrigger.lessThan(ALIAS1, "currentTemperature", 23)),
                TriggersWhen.CONDITION_FALSE_TO_TRUE);
        trigger4 = this.onboardedApi.patchServerCodeTrigger(trigger4.getTriggerID(), serverCode11, predicate41);

        Assert.assertNotNull(trigger4.getTriggerID());
        Assert.assertFalse(trigger4.disabled());
        Assert.assertNull(trigger4.getDisabledReason());
        Assert.assertNull(trigger4.getCommand());
        Assert.assertEquals(options4.getTitle(), trigger4.getTitle());
        Assert.assertNull(trigger4.getDescription());
        Assert.assertNotNull(options4.getMetadata());
        Assert.assertNotNull(trigger4.getMetadata());
        Assert.assertEquals(options4.getMetadata().toString(), trigger4.getMetadata().toString());

        trigger4ServerCode = trigger4.getServerCode();
        Assert.assertNotNull(trigger4ServerCode);

        Assert.assertEquals(serverCode11.getEndpoint(), trigger4ServerCode.getEndpoint());
        Assert.assertNull(trigger4ServerCode.getExecutorAccessToken());
        Assert.assertEquals(targetAppID1, trigger4ServerCode.getTargetAppID());
        Assert.assertNull(trigger4ServerCode.getParameters());

        trigger4Predicate = (StatePredicate)trigger4.getPredicate();
        Assert.assertEquals(EventSource.STATES, trigger4Predicate.getEventSource());
        Assert.assertEquals(TriggersWhen.CONDITION_FALSE_TO_TRUE, trigger4Predicate.getTriggersWhen());
        Assert.assertTrue(trigger4Predicate.getCondition().getClause() instanceof RangeClauseInTrigger);
        RangeClauseInTrigger clause4 = (RangeClauseInTrigger)trigger4Predicate.getCondition().getClause();
        Assert.assertEquals("currentTemperature", clause4.getField());
        Assert.assertNotNull(clause4.getUpperLimit());
        Assert.assertEquals(23, clause4.getUpperLimit().intValue());
        Assert.assertNotNull(clause4.getUpperIncluded());
        Assert.assertFalse(clause4.getUpperIncluded());
        Assert.assertNull(clause4.getLowerLimit());
        Assert.assertNull(clause4.getLowerIncluded());

        // create trigger: server code, schedule predicate, null options
        String endpoint2 = "my_function";
        String executorAccessToken2 = target.getAccessToken();
        String targetAppID2 = this.onboardedApi.getAppID();
        JSONObject parameters2 = new JSONObject("{\"doAction\":false}");
        ServerCode serverCode2 = new ServerCode(endpoint2, executorAccessToken2, targetAppID2, parameters2);
        SchedulePredicate predicate5 = new SchedulePredicate("4 * * * *");

        Trigger trigger5 = this.onboardedApi.postNewTrigger(serverCode2, predicate5);
        Assert.assertNotNull(trigger5.getTriggerID());
        Assert.assertFalse(trigger5.disabled());
        Assert.assertNull(trigger5.getDisabledReason());
        Assert.assertNull(trigger5.getCommand());

        ServerCode trigger5ServerCode = trigger5.getServerCode();
        Assert.assertNotNull(trigger5ServerCode);
        Assert.assertEquals(endpoint2, trigger5ServerCode.getEndpoint());
        Assert.assertEquals(executorAccessToken2, trigger5ServerCode.getExecutorAccessToken());
        Assert.assertEquals(targetAppID2, trigger5ServerCode.getTargetAppID());
        assertJSONObject(parameters2, trigger5ServerCode.getParameters());

        SchedulePredicate trigger5Predicate = (SchedulePredicate)trigger5.getPredicate();
        Assert.assertEquals("4 * * * *", trigger5Predicate.getSchedule());

        trigger5 = this.onboardedApi.enableTrigger(trigger5.getTriggerID(), false);
        Assert.assertTrue(trigger5.disabled());

        // create trigger 6: server code, scheduleOnce predicate, null options
        String endpoint3 = "my_function";
        String executorAccessToken3 = target.getAccessToken();
        String targetAppID3 = this.onboardedApi.getAppID();
        JSONObject parameters3 = new JSONObject("{\"doAction3\":true}");
        ServerCode serverCode3 = new ServerCode(endpoint3, executorAccessToken3, targetAppID3, parameters3);
        ScheduleOncePredicate predicate6 = new ScheduleOncePredicate(System.currentTimeMillis()+2000*1000);

        Trigger trigger6 = this.onboardedApi.postNewTrigger(serverCode3, predicate6);
        Assert.assertNotNull(trigger6.getTriggerID());
        Assert.assertFalse(trigger6.disabled());
        Assert.assertNull(trigger6.getDisabledReason());
        Assert.assertNull(trigger6.getCommand());

        ServerCode trigger6ServerCode = trigger6.getServerCode();
        Assert.assertNotNull(trigger6ServerCode);

        Assert.assertEquals(endpoint3, trigger6ServerCode.getEndpoint());
        Assert.assertEquals(executorAccessToken3, trigger6ServerCode.getExecutorAccessToken());
        Assert.assertEquals(targetAppID3, trigger6ServerCode.getTargetAppID());
        assertJSONObject(parameters3, trigger6ServerCode.getParameters());

        ScheduleOncePredicate trigger6Predicate = (ScheduleOncePredicate)trigger6.getPredicate();
        Assert.assertEquals(predicate6.getScheduleAt(), trigger6Predicate.getScheduleAt());
    }

    @Test
    public void listTriggersEmptyResultTest() throws Exception {
        Target target = this.onboardedApi.getTarget();
        Assert.assertNotNull(target);

        Assert.assertEquals(TypedID.Types.THING, target.getTypedID().getType());
        Assert.assertNotNull(target.getAccessToken());

        Pair<List<Trigger>, String> results = this.onboardedApi.listTriggers(100, null);
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

        String javascript =
                "function server_code_for_trigger(params, context){" + "\n" +
                "    return 100;" + "\n"
                + "}" + "\n";
        String versionID = rest.api().servercode().deploy(javascript);
        rest.api().servercode().setCurrentVersion(versionID);

        Target target = this.onboardedApi.getTarget();
        Assert.assertNotNull(target);
        Assert.assertNotNull(target.getAccessToken());

        // create new server code trigger
        String endpoint = "server_code_for_trigger";
        String executorAccessToken = target.getAccessToken();
        String targetAppID = this.onboardedApi.getAppID();
        JSONObject parameters = new JSONObject("{\"arg1\":\"passed_parameter\"}");
        ServerCode serverCode = new ServerCode(endpoint, executorAccessToken, targetAppID, parameters);
        Condition condition = new Condition(new EqualsClauseInTrigger(ALIAS1, "power", true));
        StatePredicate predicate = new StatePredicate(condition, TriggersWhen.CONDITION_TRUE);

        Trigger trigger = this.onboardedApi.postNewTrigger(serverCode, predicate);
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

        Pair<List<TriggeredServerCodeResult>, String> triggerServerCodeResults =
                this.onboardedApi.listTriggeredServerCodeResults(trigger.getTriggerID(), 0, null);
        Assert.assertEquals(1, triggerServerCodeResults.first.size());
        Assert.assertNull(triggerServerCodeResults.second);
        TriggeredServerCodeResult triggeredServerCodeResult = triggerServerCodeResults.first.get(0);
        Assert.assertTrue(triggeredServerCodeResult.isSucceeded());
        Assert.assertEquals(100, (int) triggeredServerCodeResult.getReturnedValueAsInteger());
        Assert.assertTrue(triggeredServerCodeResult.getExecutedAt() > 0);
        Assert.assertEquals(endpoint, triggeredServerCodeResult.getEndpoint());
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

        String javascript =
                "function server_code_for_trigger(params, context){" + "\n"+
                        "    reference.error = 100;" + "\n" +
                        "    return 100;" + "\n" +
                        "}" + "\n";
        String versionID = rest.api().servercode().deploy(javascript);
        rest.api().servercode().setCurrentVersion(versionID);

        Target target = this.onboardedApi.getTarget();
        Assert.assertNotNull(target);
        Assert.assertNotNull(target.getAccessToken());

        // create new server code trigger
        String endpoint = "server_code_for_trigger";
        String executorAccessToken = target.getAccessToken();
        String targetAppID = this.onboardedApi.getAppID();
        JSONObject parameters = new JSONObject("{\"arg1\":\"passed_parameter\"}");
        ServerCode serverCode = new ServerCode(endpoint, executorAccessToken, targetAppID, parameters);
        Condition condition = new Condition(new EqualsClauseInTrigger(ALIAS1, "power", true));
        StatePredicate predicate = new StatePredicate(condition, TriggersWhen.CONDITION_TRUE);

        Trigger trigger = this.onboardedApi.postNewTrigger(serverCode, predicate);
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

        Pair<List<TriggeredServerCodeResult>, String> triggerServerCodeResults =
                this.onboardedApi.listTriggeredServerCodeResults(trigger.getTriggerID(), 0, null);
        Assert.assertEquals(1, triggerServerCodeResults.first.size());
        Assert.assertNull(triggerServerCodeResults.second);
        TriggeredServerCodeResult triggeredServerCodeResult = triggerServerCodeResults.first.get(0);
        Assert.assertFalse(triggeredServerCodeResult.isSucceeded());
        Assert.assertNull(triggeredServerCodeResult.getReturnedValue());
        Assert.assertTrue(triggeredServerCodeResult.getExecutedAt() > 0);
        Assert.assertEquals(endpoint, triggeredServerCodeResult.getEndpoint());
        Assert.assertNotNull(triggeredServerCodeResult.getError());
        Assert.assertEquals("Error found while executing the developer-defined code", triggeredServerCodeResult.getError().getErrorMessage());
        Assert.assertEquals("RUNTIME_ERROR", triggeredServerCodeResult.getError().getErrorCode());
        Assert.assertEquals("reference is not defined", triggeredServerCodeResult.getError().getDetailMessage());
    }

    @Test(expected = BadRequestException.class)
    public void basicInvalidSchedulePredicateTriggerTest() throws Exception {
        Target target = this.onboardedApi.getTarget();
        Assert.assertNotNull(target);

        // create new trigger
        List<AliasAction<? extends Action>> aliasActions = new ArrayList<>();
        aliasActions.add(
                new AliasAction<>(
                        ALIAS1,
                        new AirConditionerActions(true, 25)));

        TriggeredCommandForm form = TriggeredCommandForm.Builder.newBuilder(aliasActions).build();
        SchedulePredicate predicate1 = new SchedulePredicate("wrong format");
        this.onboardedApi.postNewTrigger(form, predicate1);
    }
}
