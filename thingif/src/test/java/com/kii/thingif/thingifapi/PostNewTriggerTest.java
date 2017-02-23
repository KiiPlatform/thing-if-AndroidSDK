package com.kii.thingif.thingifapi;

import android.content.Context;

import com.kii.thingif.StandaloneThing;
import com.kii.thingif.Target;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.ThingIFAPITestBase;
import com.kii.thingif.TypedID;
import com.kii.thingif.actions.AirConditionerActions;
import com.kii.thingif.actions.HumidityActions;
import com.kii.thingif.clause.trigger.EqualsClauseInTrigger;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.AliasAction;
import com.kii.thingif.command.Command;
import com.kii.thingif.command.CommandFactory;
import com.kii.thingif.exception.ForbiddenException;
import com.kii.thingif.exception.NotFoundException;
import com.kii.thingif.exception.ServiceUnavailableException;
import com.kii.thingif.thingifapi.utils.ThingIFAPIUtils;
import com.kii.thingif.trigger.Condition;
import com.kii.thingif.trigger.Predicate;
import com.kii.thingif.trigger.ScheduleOncePredicate;
import com.kii.thingif.trigger.SchedulePredicate;
import com.kii.thingif.trigger.ServerCode;
import com.kii.thingif.trigger.StatePredicate;
import com.kii.thingif.trigger.Trigger;
import com.kii.thingif.trigger.TriggerOptions;
import com.kii.thingif.trigger.TriggeredCommandForm;
import com.kii.thingif.trigger.TriggersWhen;
import com.kii.thingif.utils.JsonUtil;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RunWith(RobolectricTestRunner.class)
public class PostNewTriggerTest extends ThingIFAPITestBase{
    private Context context;

    @Before
    public void before() throws Exception {
        context = RuntimeEnvironment.application.getApplicationContext();
        this.server = new MockWebServer();
        this.server.start();
    }

    @After
    public void after() throws Exception {
        this.server.shutdown();
    }

    private void postNewTriggerWithCommandTest(TriggeredCommandForm form, Predicate predicate, TriggerOptions options) throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";

        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);

        TypedID commandTargetID;
        if (form.getTargetID() != null) {
            commandTargetID = form.getTargetID();
        }else{
            commandTargetID = target.getTypedID();
        }
        Command expectedCommand = CommandFactory.newCommand(
                api.getOwner().getTypedID(),
                form.getAliasActions(),
                null,
                commandTargetID,
                null,
                null,
                null,
                null,
                null,
                form.getTitle(),
                form.getDescription(),
                form.getMetadata());
        this.addMockResponseForPostNewTrigger(201, triggerID);
        this.addMockResponseForGetTriggerWithCommand(
                200,
                triggerID,
                expectedCommand,
                predicate,
                options,
                false,
                null);

        ThingIFAPIUtils.setTarget(api, target);

        Trigger trigger = api.postNewTrigger(form, predicate, options);
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(false, trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertNull(trigger.getServerCode());
        assertSamePredicate(predicate, trigger.getPredicate());
        assertSameCommands(expectedCommand, trigger.getCommand());
        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers", request1.getPath());
        Assert.assertEquals("POST", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<String, String>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("command", JsonUtil.commandToJson(expectedCommand));
        expectedRequestBody.put("predicate", JsonUtil.predicateToJson(predicate));
        expectedRequestBody.put("triggersWhat","COMMAND");
        if (options != null) {
            expectedRequestBody.putOpt("title", options.getTitle());
            expectedRequestBody.putOpt("description", options.getDescription());
            expectedRequestBody.putOpt("metadata", options.getMetadata());
        }
        this.assertRequestBody(expectedRequestBody, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());

        Map<String, String> expectedRequestHeaders2 = new HashMap<String, String>();
        expectedRequestHeaders2.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders2.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders2.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders2, request2);
    }
    @Test
    public void postNewCommandTrigger_FormOnlyHasActions_StatePredicate__NonNullOptions_Test() throws Exception {
        List<AliasAction<? extends Action>> actions = new ArrayList<>();
        actions.add(new AliasAction<Action>(
                ALIAS1,
                new AirConditionerActions(true, null)));
        actions.add(new AliasAction<Action>(
                ALIAS2,
                new HumidityActions(45)));
        TriggeredCommandForm form = TriggeredCommandForm.Builder.newBuilder(actions).build();
        StatePredicate predicate = new StatePredicate(new Condition(
                new EqualsClauseInTrigger(ALIAS1, "power", true)), TriggersWhen.CONDITION_CHANGED);
        TriggerOptions options = TriggerOptions.Builder.newBuilder()
                .setTitle("trigger title")
                .setDescription("trigger description")
                .setMetadata(new JSONObject().put("key1", "value1"))
                .build();
        this.postNewTriggerWithCommandTest(form, predicate, options);
    }
    @Test
    public void postNewCommandTrigger_FormOnlyHasActionsAndCommandOption_ScheduledOncePredicate_NonNullOptions_Test() throws Exception {
        List<AliasAction<? extends Action>> actions = new ArrayList<>();
        actions.add(new AliasAction<Action>(
                ALIAS1,
                new AirConditionerActions(true, null)));
        TriggeredCommandForm form = TriggeredCommandForm.Builder.newBuilder(actions)
                .setTitle("command title")
                .setDescription("command description")
                .setMetadata(new JSONObject().put("k", "v"))
                .build();
        ScheduleOncePredicate predicate = new ScheduleOncePredicate(System.currentTimeMillis());
        TriggerOptions options = TriggerOptions.Builder.newBuilder()
                .setTitle("trigger title")
                .setMetadata(new JSONObject().put("key1", "value1"))
                .build();
        this.postNewTriggerWithCommandTest(form, predicate, options);
    }

    @Test
    public void postNewCommandCrossThingTrigger_FormHasTarget_SchedulePredicate_NonNullOptions_Test() throws Exception {
        List<AliasAction<? extends Action>> actions = new ArrayList<>();
        TypedID targetID = new TypedID(TypedID.Types.THING, "another thing");
        actions.add(new AliasAction<Action>(
                ALIAS1,
                new AirConditionerActions(true, null)));
        TriggeredCommandForm form = TriggeredCommandForm.Builder.newBuilder(actions)
                .setTargetID(targetID)
                .setTitle("command title")
                .setDescription("command description")
                .setMetadata(new JSONObject().put("k", "v"))
                .build();
        SchedulePredicate predicate = new SchedulePredicate("1 * * * *");
        TriggerOptions options = TriggerOptions.Builder.newBuilder()
                .setTitle("cross thing trigger title")
                .setDescription("cross thing trigger description")
                .build();
        this.postNewTriggerWithCommandTest(form, predicate, options);
    }
    @Test
    public void postNewCommandTrigger_StatePredicate__NullOptions_Test() throws Exception {
        List<AliasAction<? extends Action>> actions = new ArrayList<>();
        actions.add(new AliasAction<Action>(
                ALIAS1,
                new AirConditionerActions(true, null)));
        actions.add(new AliasAction<Action>(
                ALIAS2,
                new HumidityActions(45)));
        TriggeredCommandForm form = TriggeredCommandForm.Builder.newBuilder(actions).build();

        StatePredicate predicate = new StatePredicate(new Condition(
                new EqualsClauseInTrigger(ALIAS1, "power", true)), TriggersWhen.CONDITION_CHANGED);
        this.postNewTriggerWithCommandTest(form, predicate, null);
    }

    @Test
    public void postNewCommandTrigger403ErrorTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        List<AliasAction<? extends Action>> actions = new ArrayList<>();
        actions.add(new AliasAction<Action>(
                ALIAS1,
                new AirConditionerActions(true, null)));
        actions.add(new AliasAction<Action>(
                ALIAS2,
                new HumidityActions(45)));
        TriggeredCommandForm form = TriggeredCommandForm.Builder.newBuilder(actions).build();

        StatePredicate predicate = new StatePredicate(new Condition(
                new EqualsClauseInTrigger(ALIAS1, "power", true)), TriggersWhen.CONDITION_CHANGED);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);

        Command expectedCommand = CommandFactory.newCommand(
                api.getOwner().getTypedID(),
                form.getAliasActions(),
                null,
                target.getTypedID(),
                null,
                null,
                null,
                null,
                null,
                form.getTitle(),
                form.getDescription(),
                form.getMetadata());
        this.addEmptyMockResponse(403);

        try {
            ThingIFAPIUtils.setTarget(api, target);
            api.postNewTrigger(form, predicate, null);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (ForbiddenException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<String, String>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("command", JsonUtil.commandToJson(expectedCommand));
        expectedRequestBody.put("predicate",JsonUtil.predicateToJson(predicate));
        expectedRequestBody.put("triggersWhat","COMMAND");
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void postNewTrigger404ErrorTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        List<AliasAction<? extends Action>> actions = new ArrayList<>();
        actions.add(new AliasAction<Action>(
                ALIAS1,
                new AirConditionerActions(true, null)));
        actions.add(new AliasAction<Action>(
                ALIAS2,
                new HumidityActions(45)));
        TriggeredCommandForm form = TriggeredCommandForm.Builder.newBuilder(actions).build();

        StatePredicate predicate = new StatePredicate(new Condition(
                new EqualsClauseInTrigger(ALIAS1, "power", true)), TriggersWhen.CONDITION_CHANGED);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);

        Command expectedCommand = CommandFactory.newCommand(
                api.getOwner().getTypedID(),
                form.getAliasActions(),
                null,
                target.getTypedID(),
                null,
                null,
                null,
                null,
                null,
                form.getTitle(),
                form.getDescription(),
                form.getMetadata());
        this.addEmptyMockResponse(404);

        try {
            ThingIFAPIUtils.setTarget(api, target);
            api.postNewTrigger(form, predicate, null);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (NotFoundException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<String, String>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("command", JsonUtil.commandToJson(expectedCommand));
        expectedRequestBody.put("predicate",JsonUtil.predicateToJson(predicate));
        expectedRequestBody.put("triggersWhat","COMMAND");
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void postNewTrigger503ErrorTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        List<AliasAction<? extends Action>> actions = new ArrayList<>();
        actions.add(new AliasAction<Action>(
                ALIAS1,
                new AirConditionerActions(true, null)));
        actions.add(new AliasAction<Action>(
                ALIAS2,
                new HumidityActions(45)));
        TriggeredCommandForm form = TriggeredCommandForm.Builder.newBuilder(actions).build();

        StatePredicate predicate = new StatePredicate(new Condition(
                new EqualsClauseInTrigger(ALIAS1, "power", true)), TriggersWhen.CONDITION_CHANGED);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);

        Command expectedCommand = CommandFactory.newCommand(
                api.getOwner().getTypedID(),
                form.getAliasActions(),
                null,
                target.getTypedID(),
                null,
                null,
                null,
                null,
                null,
                form.getTitle(),
                form.getDescription(),
                form.getMetadata());

        this.addEmptyMockResponse(503);

        try {
            ThingIFAPIUtils.setTarget(api, target);
            api.postNewTrigger(form, predicate, null);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (ServiceUnavailableException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<String, String>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("command", JsonUtil.commandToJson(expectedCommand));
        expectedRequestBody.put("predicate",JsonUtil.predicateToJson(predicate));
        expectedRequestBody.put("triggersWhat","COMMAND");
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test(expected = IllegalStateException.class)
    public void postNewCommandTriggerWithNullTargetTest() throws Exception {

        List<AliasAction<? extends Action>> actions = new ArrayList<>();
        actions.add(new AliasAction<Action>(
                ALIAS1,
                new AirConditionerActions(true, null)));
        actions.add(new AliasAction<Action>(
                ALIAS2,
                new HumidityActions(45)));
        TriggeredCommandForm form = TriggeredCommandForm.Builder.newBuilder(actions).build();

        StatePredicate predicate = new StatePredicate(new Condition(
                new EqualsClauseInTrigger(ALIAS1, "power", true)), TriggersWhen.CONDITION_CHANGED);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        api.postNewTrigger(form, predicate, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void postNewCommandTrigger_NullForm_Test() throws Exception{
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";

        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        StatePredicate predicate = new StatePredicate(new Condition(
                new EqualsClauseInTrigger(ALIAS1, "power", true)), TriggersWhen.CONDITION_CHANGED);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);
        api.postNewTrigger((TriggeredCommandForm) null, predicate, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void postNewCommandTrigger_NullPredicate_Test() throws Exception{
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";

        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        List<AliasAction<? extends Action>> actions = new ArrayList<>();
        actions.add(new AliasAction<Action>(
                ALIAS1,
                new AirConditionerActions(true, null)));
        actions.add(new AliasAction<Action>(
                ALIAS2,
                new HumidityActions(45)));
        TriggeredCommandForm form = TriggeredCommandForm.Builder.newBuilder(actions).build();

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);
        api.postNewTrigger(form, null, null);
    }
}
