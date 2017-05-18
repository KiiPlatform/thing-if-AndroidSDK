package com.kii.thing_if.largetests;

import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;
import android.util.Pair;

import com.kii.thing_if.OnboardWithVendorThingIDOptions;
import com.kii.thing_if.Target;
import com.kii.thing_if.TargetState;
import com.kii.thing_if.ThingIFAPI;
import com.kii.thing_if.TypedID;
import com.kii.thing_if.actions.SetPresetHumidity;
import com.kii.thing_if.actions.SetPresetTemperature;
import com.kii.thing_if.actions.TurnPower;
import com.kii.thing_if.clause.trigger.EqualsClauseInTrigger;
import com.kii.thing_if.clause.trigger.RangeClauseInTrigger;
import com.kii.thing_if.command.Action;
import com.kii.thing_if.command.AliasAction;
import com.kii.thing_if.command.Command;
import com.kii.thing_if.exception.BadRequestException;
import com.kii.thing_if.states.AirConditionerState;
import com.kii.thing_if.trigger.Condition;
import com.kii.thing_if.trigger.EventSource;
import com.kii.thing_if.trigger.ScheduleOncePredicate;
import com.kii.thing_if.trigger.SchedulePredicate;
import com.kii.thing_if.trigger.ServerCode;
import com.kii.thing_if.trigger.StatePredicate;
import com.kii.thing_if.trigger.Trigger;
import com.kii.thing_if.trigger.TriggerOptions;
import com.kii.thing_if.trigger.TriggeredCommandForm;
import com.kii.thing_if.trigger.TriggeredServerCodeResult;
import com.kii.thing_if.trigger.TriggersWhen;

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
        List<AliasAction> aliasActions = new ArrayList<>();
        List<Action> actions = new ArrayList<>();
        actions.add(new TurnPower(true));
        actions.add(new SetPresetTemperature(25));
        aliasActions.add(new AliasAction(ALIAS1, actions));
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
        Assert.assertEquals(2, trigger1Command.getAliasActions().get(0).getActions().size());
        Action action11 = trigger1Command.getAliasActions().get(0).getActions().get(0);
        Assert.assertTrue(action11 instanceof TurnPower);
        Assert.assertTrue(((TurnPower)action11).getPower());
        Action action12 = trigger1Command.getAliasActions().get(0).getActions().get(1);
        Assert.assertTrue(action12 instanceof SetPresetTemperature);
        Assert.assertEquals(25, ((SetPresetTemperature)action12).getTemperature().intValue());

        StatePredicate trigger1Predicate = (StatePredicate)trigger1.getPredicate();
        Assert.assertEquals(EventSource.STATES, trigger1Predicate.getEventSource());
        Assert.assertEquals(TriggersWhen.CONDITION_TRUE, trigger1Predicate.getTriggersWhen());
        Assert.assertEquals("power", ((EqualsClauseInTrigger)trigger1Predicate.getCondition().getClause()).getField());
        Assert.assertEquals(Boolean.TRUE, ((EqualsClauseInTrigger)trigger1Predicate.getCondition().getClause()).getValue());

        // get trigger
        Trigger gotTrigger = this.onboardedApi.getTrigger(trigger1.getTriggerID());
        Assert.assertEquals(trigger1.getTriggerID(), gotTrigger.getTriggerID());
        Assert.assertEquals(trigger1.getTargetID(), gotTrigger.getTargetID());
        StatePredicate gotPredicate = (StatePredicate)gotTrigger.getPredicate();
        Assert.assertEquals(trigger1Predicate.getEventSource(), gotPredicate.getEventSource());
        Assert.assertEquals(trigger1Predicate.getTriggersWhen(), gotPredicate.getTriggersWhen());
        Assert.assertEquals(((EqualsClauseInTrigger) trigger1Predicate.getCondition().getClause()).getField(),
                ((EqualsClauseInTrigger)gotPredicate.getCondition().getClause()).getField());
        Assert.assertEquals(((EqualsClauseInTrigger) trigger1Predicate.getCondition().getClause()).getValue(),
                ((EqualsClauseInTrigger)gotPredicate.getCondition().getClause()).getValue());
        Command gotCommand = gotTrigger.getCommand();
        Assert.assertNotNull(gotCommand);
        Assert.assertEquals(trigger1Command.getCommandID(), gotCommand.getCommandID());
        Assert.assertEquals(trigger1Command.getTargetID(), gotCommand.getTargetID());
        Assert.assertEquals(trigger1Command.getIssuerID(), gotCommand.getIssuerID());
        Assert.assertEquals(trigger1Command.getCommandState(), gotCommand.getCommandState());
        Assert.assertEquals(trigger1Command.getFiredByTriggerID(), gotCommand.getFiredByTriggerID());
        Assert.assertEquals(trigger1Command.getCreated(), gotCommand.getCreated());
        Assert.assertEquals(trigger1Command.getModified(), gotCommand.getModified());
        Assert.assertEquals(1, gotCommand.getAliasActions().size());
        Assert.assertEquals(ALIAS1, gotCommand.getAliasActions().get(0).getAlias());
        Assert.assertEquals(2, gotCommand.getAliasActions().get(0).getActions().size());
        Action gotAction1 = trigger1Command.getAliasActions().get(0).getActions().get(0);
        Assert.assertTrue(gotAction1 instanceof TurnPower);
        Assert.assertTrue(((TurnPower)gotAction1).getPower());
        Action gotAction2 = trigger1Command.getAliasActions().get(0).getActions().get(1);
        Assert.assertTrue(gotAction2 instanceof SetPresetTemperature);
        Assert.assertEquals(25, ((SetPresetTemperature)gotAction2).getTemperature().intValue());
        Assert.assertEquals(trigger1.getServerCode(), gotTrigger.getServerCode());
        Assert.assertEquals(trigger1.disabled(), gotTrigger.disabled());
        Assert.assertEquals(trigger1.getDisabledReason(), gotTrigger.getDisabledReason());
        Assert.assertEquals(trigger1.getTitle(), gotTrigger.getTitle());
        Assert.assertEquals(trigger1.getDescription(), gotTrigger.getDescription());
        Assert.assertEquals(trigger1.getMetadata(), gotTrigger.getMetadata());

        // disable/enable trigger 1
        trigger1 = this.onboardedApi.enableTrigger(trigger1.getTriggerID(), false);
        Assert.assertTrue(trigger1.disabled());
        trigger1 = this.onboardedApi.enableTrigger(trigger1.getTriggerID(), true);
        Assert.assertFalse(trigger1.disabled());

        // create trigger 2: command, schedule predicate, non null options
        List<AliasAction> aliasActions2 = new ArrayList<>();
        List<Action> actions2 = new ArrayList<>();
        actions2.add(new SetPresetHumidity(50));
        aliasActions2.add(new AliasAction(ALIAS2, actions2));
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
        Assert.assertEquals(1, trigger2Command.getAliasActions().get(0).getActions().size());
        Action action21 = trigger2Command.getAliasActions().get(0).getActions().get(0);
        Assert.assertTrue(action21 instanceof SetPresetHumidity);
        Assert.assertEquals(50, ((SetPresetHumidity)action21).getHumidity().intValue());
        Assert.assertNull(trigger2Command.getAliasActionResults());
        Assert.assertNull(trigger2Command.getAliasActionResults());

        SchedulePredicate trigger2Predicate = (SchedulePredicate) trigger2.getPredicate();
        Assert.assertEquals("5 * * * *", trigger2Predicate.getSchedule());

        // disable trigger 2
        trigger2 = this.onboardedApi.enableTrigger(trigger2.getTriggerID(), false);
        Assert.assertTrue(trigger2.disabled());

        // update trigger 2
        List<AliasAction> aliasActions21 = new ArrayList<>();
        List<Action> actions21 = new ArrayList<>();
        actions21.add(new TurnPower(false));
        aliasActions21.add(new AliasAction(ALIAS1, actions21));
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
        Assert.assertEquals(1, trigger2Command.getAliasActions().get(0).getActions().size());
        action21 = trigger2Command.getAliasActions().get(0).getActions().get(0);
        Assert.assertTrue(action21 instanceof TurnPower);
        Assert.assertFalse(((TurnPower)action21).getPower());
        Assert.assertNull(trigger2Command.getAliasActionResults());
        Assert.assertNull(trigger2Command.getAliasActionResults());

        trigger2Predicate = (SchedulePredicate) trigger2.getPredicate();
        Assert.assertEquals(trigger2Predicate2.getSchedule(), trigger2Predicate.getSchedule());

        // create trigger 3: command, schedule once predicate, null options
        List<AliasAction> aliasActions3 = new ArrayList<>();
        List<Action> actions3 = new ArrayList<>();
        actions3.add(new SetPresetHumidity(50));
        aliasActions3.add(new AliasAction(ALIAS2, actions3));
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
        Assert.assertEquals(1, trigger3Command.getAliasActions().get(0).getActions().size());
        Action action3 = trigger3Command.getAliasActions().get(0).getActions().get(0);
        Assert.assertTrue(action3 instanceof SetPresetHumidity);
        Assert.assertEquals(50, ((SetPresetHumidity)action3).getHumidity().intValue());
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
        Assert.assertEquals(2, trigger1Command.getAliasActions().get(0).getActions().size());
        action11 = trigger1Command.getAliasActions().get(0).getActions().get(0);
        Assert.assertTrue(action11 instanceof TurnPower);
        Assert.assertTrue(((TurnPower)action11).getPower());
        action12 = trigger1Command.getAliasActions().get(0).getActions().get(1);
        Assert.assertEquals(25, ((SetPresetTemperature)action12).getTemperature().intValue());

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
        Assert.assertEquals(1, trigger2Command.getAliasActions().get(0).getActions().size());
        action21 = trigger2Command.getAliasActions().get(0).getActions().get(0);
        Assert.assertTrue(action21 instanceof TurnPower);
        Assert.assertFalse(((TurnPower)action21).getPower());
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
        Assert.assertEquals(1, trigger3Command.getAliasActions().get(0).getActions().size());
        action3 = trigger3Command.getAliasActions().get(0).getActions().get(0);
        Assert.assertTrue(action3 instanceof SetPresetHumidity);
        Assert.assertEquals(50, ((SetPresetHumidity)action3).getHumidity().intValue());
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

        // get trigger
        Trigger gotTrigger = this.onboardedApi.getTrigger(trigger4.getTriggerID());
        Assert.assertEquals(trigger4.getTriggerID(), gotTrigger.getTriggerID());
        Assert.assertEquals(trigger4.getTargetID(), gotTrigger.getTargetID());
        StatePredicate gotPredicate = (StatePredicate)gotTrigger.getPredicate();
        Assert.assertEquals(trigger4Predicate.getEventSource(), gotPredicate.getEventSource());
        Assert.assertEquals(trigger4Predicate.getTriggersWhen(), gotPredicate.getTriggersWhen());
        Assert.assertEquals(((EqualsClauseInTrigger) trigger4Predicate.getCondition().getClause()).getField(),
                ((EqualsClauseInTrigger)gotPredicate.getCondition().getClause()).getField());
        Assert.assertEquals(((EqualsClauseInTrigger) trigger4Predicate.getCondition().getClause()).getValue(),
                ((EqualsClauseInTrigger)gotPredicate.getCondition().getClause()).getValue());
        Assert.assertNull(gotTrigger.getCommand());
        ServerCode gotServerCode = gotTrigger.getServerCode();
        Assert.assertNotNull(gotServerCode);
        Assert.assertEquals(trigger4ServerCode.getEndpoint(), gotServerCode.getEndpoint());
        Assert.assertEquals(trigger4ServerCode.getExecutorAccessToken(), gotServerCode.getExecutorAccessToken());
        Assert.assertEquals(trigger4ServerCode.getTargetAppID(), gotServerCode.getTargetAppID());
        assertJSONObject(trigger4ServerCode.getParameters(), gotServerCode.getParameters());
        Assert.assertEquals(trigger4.disabled(), gotTrigger.disabled());
        Assert.assertEquals(trigger4.getDisabledReason(), gotTrigger.getDisabledReason());
        Assert.assertEquals(trigger4.getTitle(), gotTrigger.getTitle());
        Assert.assertEquals(trigger4.getDescription(), gotTrigger.getDescription());
        assertJSONObject(trigger4.getMetadata(), gotTrigger.getMetadata());

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

    /*
      listTriggerServerCodeResultsTest and
      listTriggerServerCodeResultsWithErrorTest requires deployed server
      code. We already deployed server code for these tests in current
      application.

      If you change application, you need to deploy server code before
      execute these tests.

      The server codes which we deploy are followings:

      function server_code_for_trigger(params, context) {
        return 100;
      }

      function server_code_for_trigger_error(params, context) {
        reference.error = 100;
        return 100;
      }
     */
    @Test
    public void listTriggerServerCodeResultsTest() throws Exception {
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
        Assert.assertNotNull(trigger.getTargetID());
        Assert.assertNull(trigger.getCommand());

        Thread.sleep(3000);

        // update thing state in order to trigger the server code
        updateTargetState(
            this.onboardedApi.getTarget(),
            new TargetState[] { new AirConditionerState(false, null) });

        Thread.sleep(3000);

        updateTargetState(
            this.onboardedApi.getTarget(),
            new TargetState[] { new AirConditionerState(true, null) });

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
        Target target = this.onboardedApi.getTarget();
        Assert.assertNotNull(target);
        Assert.assertNotNull(target.getAccessToken());

        // create new server code trigger
        String endpoint = "server_code_for_trigger_error";
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
        Assert.assertNotNull(trigger.getTargetID());
        Assert.assertNull(trigger.getCommand());

        Thread.sleep(3000);

        // update thing state in order to trigger the server code
        updateTargetState(
            this.onboardedApi.getTarget(),
            new TargetState[] { new AirConditionerState(false, null) });

        Thread.sleep(3000);

        updateTargetState(
            this.onboardedApi.getTarget(),
            new TargetState[] { new AirConditionerState(true, null) });

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
        List<AliasAction> aliasActions = new ArrayList<>();
        List<Action> actions = new ArrayList<>();
        actions.add(new TurnPower(true));
        actions.add(new SetPresetTemperature(25));
        aliasActions.add(new AliasAction(ALIAS1, actions));

        TriggeredCommandForm form = TriggeredCommandForm.Builder.newBuilder(aliasActions).build();
        SchedulePredicate predicate1 = new SchedulePredicate("wrong format");
        this.onboardedApi.postNewTrigger(form, predicate1);
    }
}
