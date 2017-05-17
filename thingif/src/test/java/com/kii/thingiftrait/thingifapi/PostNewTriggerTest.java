package com.kii.thingiftrait.thingifapi;

import android.content.Context;

import com.kii.thingiftrait.StandaloneThing;
import com.kii.thingiftrait.Target;
import com.kii.thingiftrait.ThingIFAPI;
import com.kii.thingiftrait.ThingIFAPITestBase;
import com.kii.thingiftrait.TypedID;
import com.kii.thingiftrait.actions.SetPresetHumidity;
import com.kii.thingiftrait.actions.SetPresetTemperature;
import com.kii.thingiftrait.actions.TurnPower;
import com.kii.thingiftrait.clause.trigger.EqualsClauseInTrigger;
import com.kii.thingiftrait.command.Action;
import com.kii.thingiftrait.command.AliasAction;
import com.kii.thingiftrait.command.Command;
import com.kii.thingiftrait.command.CommandFactory;
import com.kii.thingiftrait.exception.ForbiddenException;
import com.kii.thingiftrait.exception.NotFoundException;
import com.kii.thingiftrait.exception.ServiceUnavailableException;
import com.kii.thingiftrait.thingifapi.utils.ThingIFAPIUtils;
import com.kii.thingiftrait.trigger.Condition;
import com.kii.thingiftrait.trigger.Predicate;
import com.kii.thingiftrait.trigger.ScheduleOncePredicate;
import com.kii.thingiftrait.trigger.SchedulePredicate;
import com.kii.thingiftrait.trigger.ServerCode;
import com.kii.thingiftrait.trigger.StatePredicate;
import com.kii.thingiftrait.trigger.Trigger;
import com.kii.thingiftrait.trigger.TriggerOptions;
import com.kii.thingiftrait.trigger.TriggeredCommandForm;
import com.kii.thingiftrait.trigger.TriggersWhen;
import com.kii.thingiftrait.utils.JsonUtil;
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

        TriggerOptions expectedOpitons;
        if (options != null) {
            expectedOpitons = options;
        }else {
            expectedOpitons = TriggerOptions.Builder.newBuilder()
                    .setTitle("trigger title")
                    .setDescription("trigger description")
                    .setMetadata(new JSONObject().put("key", "value")).build();
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

        Trigger trigger;
        if (options != null) {
            trigger = api.postNewTrigger(form, predicate, options);
        }else {
            trigger = api.postNewTrigger(form, predicate);
        }
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

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
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

        Map<String, String> expectedRequestHeaders2 = new HashMap<>();
        expectedRequestHeaders2.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders2.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders2.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders2, request2);
    }
    @Test
    public void postNewCommandTrigger_FormOnlyHasActions_StatePredicate__NonNullOptions_Test() throws Exception {
        List<AliasAction> aliasActions = new ArrayList<>();
        List<Action> actions = new ArrayList<>();
        actions.add(new TurnPower(true));
        actions.add(new SetPresetTemperature(23));
        aliasActions.add(new AliasAction(ALIAS1, actions));
        TriggeredCommandForm form = TriggeredCommandForm.Builder.newBuilder(aliasActions).build();
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
        List<AliasAction> aliasActions = new ArrayList<>();
        List<Action> actions = new ArrayList<>();
        actions.add(new TurnPower(true));
        aliasActions.add(new AliasAction(
                ALIAS1,
                actions));
        TriggeredCommandForm form = TriggeredCommandForm.Builder.newBuilder(aliasActions)
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
        TypedID targetID = new TypedID(TypedID.Types.THING, "another thing");
        List<AliasAction> aliasActions = new ArrayList<>();
        List<Action> actions = new ArrayList<>();
        actions.add(new TurnPower(true));
        aliasActions.add(new AliasAction(
                ALIAS1,
                actions));
        TriggeredCommandForm form = TriggeredCommandForm.Builder.newBuilder(aliasActions)
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
        List<AliasAction> aliasActions = new ArrayList<>();
        List<Action> actions1 = new ArrayList<>();
        actions1.add(new TurnPower(true));
        aliasActions.add(new AliasAction(ALIAS1, actions1));
        List<Action> actions2 = new ArrayList<>();
        actions2.add(new SetPresetHumidity(45));
        aliasActions.add(new AliasAction(ALIAS2, actions2));

        TriggeredCommandForm form = TriggeredCommandForm.Builder.newBuilder(aliasActions).build();

        StatePredicate predicate = new StatePredicate(new Condition(
                new EqualsClauseInTrigger(ALIAS1, "power", true)), TriggersWhen.CONDITION_CHANGED);
        this.postNewTriggerWithCommandTest(form, predicate, null);
    }

    @Test
    public void postNewCommandTrigger403ErrorTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        List<AliasAction> aliasActions = new ArrayList<>();
        List<Action> actions = new ArrayList<>();
        actions.add(new TurnPower(true));
        actions.add(new SetPresetTemperature(23));
        aliasActions.add(new AliasAction(ALIAS1, actions));

        TriggeredCommandForm form = TriggeredCommandForm.Builder.newBuilder(aliasActions).build();

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

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
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

        List<AliasAction> aliasActions = new ArrayList<>();
        List<Action> actions = new ArrayList<>();
        actions.add(new TurnPower(true));
        actions.add(new SetPresetTemperature(23));
        aliasActions.add(new AliasAction(ALIAS1, actions));
        TriggeredCommandForm form = TriggeredCommandForm.Builder.newBuilder(aliasActions).build();

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

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
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

        List<AliasAction> aliasActions = new ArrayList<>();
        List<Action> actions = new ArrayList<>();
        actions.add(new TurnPower(true));
        actions.add(new SetPresetTemperature(23));
        aliasActions.add(new AliasAction(ALIAS1, actions));
        TriggeredCommandForm form = TriggeredCommandForm.Builder.newBuilder(aliasActions).build();

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

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
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

        List<AliasAction> aliasActions = new ArrayList<>();
        List<Action> actions = new ArrayList<>();
        actions.add(new TurnPower(true));
        actions.add(new SetPresetTemperature(23));
        aliasActions.add(new AliasAction(ALIAS1, actions));
        TriggeredCommandForm form = TriggeredCommandForm.Builder.newBuilder(aliasActions).build();

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

        List<AliasAction> aliasActions = new ArrayList<>();
        List<Action> actions = new ArrayList<>();
        actions.add(new TurnPower(true));
        actions.add(new SetPresetTemperature(23));
        aliasActions.add(new AliasAction(ALIAS1, actions));
        TriggeredCommandForm form = TriggeredCommandForm.Builder.newBuilder(aliasActions).build();

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);
        api.postNewTrigger(form, null, null);
    }

    private void postNewTriggerWithServerCode(ServerCode expectedServerCode, Predicate predicate, TriggerOptions options) throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);
        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);

        this.addMockResponseForPostNewTrigger(201, triggerID);
        this.addMockResponseForGetTriggerWithServerCode(200, triggerID, expectedServerCode, predicate, options, false, null);

        ThingIFAPIUtils.setTarget(api, target);
        Trigger trigger;
        if (options != null) {
            trigger = api.postNewTrigger(expectedServerCode, predicate, options);
        } else {
            trigger = api.postNewTrigger(expectedServerCode, predicate);
        }
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(false, trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertNull(trigger.getCommand());
        assertSamePredicate(predicate, trigger.getPredicate());
        this.assertServerCode(expectedServerCode, trigger.getServerCode());
        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers", request1.getPath());
        Assert.assertEquals("POST", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("serverCode", JsonUtil.serverCodeToJson(expectedServerCode));
        expectedRequestBody.put("predicate", JsonUtil.predicateToJson(predicate));
        expectedRequestBody.put("triggersWhat","SERVER_CODE");
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

        Map<String, String> expectedRequestHeaders2 = new HashMap<>();
        expectedRequestHeaders2.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders2.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders2.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders2, request2);
    }

    @Test
    public void postNewServerCodeTrigger_NonNullOptions_ServerCodeWithFullParams_StatePredicate_Test() throws Exception {
        SchedulePredicate predicate = new SchedulePredicate("1 * * * *");
        TriggerOptions options = TriggerOptions.Builder.newBuilder()
                .setTitle("trigger title")
                .setDescription("trigger description")
                .setMetadata(new JSONObject().put("key1", "value1"))
                .build();
        ServerCode serverCode = new ServerCode("function_name", "token12345", "app0001", new JSONObject("{\"param\":\"p0001\"}"));
        this.postNewTriggerWithServerCode(serverCode, predicate, options);
    }
    @Test
    public void postNewServerCodeTrigger_NonNullOptions_ServerCodeWithEndPointAndToken_SchedulePredicate_Test() throws Exception {
        StatePredicate predicate = new StatePredicate(new Condition(
                new EqualsClauseInTrigger(ALIAS1, "power", true)), TriggersWhen.CONDITION_CHANGED);
        TriggerOptions options = TriggerOptions.Builder.newBuilder()
                .setTitle("trigger title")
                .setDescription("trigger description")
                .setMetadata(new JSONObject().put("key1", "value1"))
                .build();
        ServerCode serverCode = new ServerCode("function_name", "token12345");
        this.postNewTriggerWithServerCode(serverCode, predicate, options);
    }
    @Test
    public void postNewServerCodeTrigger_NonNullOptions_ServerCodeWithEndPointAndNullToken_ScheduleOncePredicate_Test() throws Exception {
        ScheduleOncePredicate predicate = new ScheduleOncePredicate(System.currentTimeMillis());
        TriggerOptions options = TriggerOptions.Builder.newBuilder()
                .setTitle("trigger title")
                .setDescription("trigger description")
                .setMetadata(new JSONObject().put("key1", "value1"))
                .build();
        ServerCode serverCode = new ServerCode("function_name", null);
        this.postNewTriggerWithServerCode(serverCode, predicate, options);
    }
    @Test
    public void postNewServerCodeTrigger_NonNullOptions_ServerCodeWithEndPointAndToken_StatePredicate_Test() throws Exception {
        StatePredicate predicate = new StatePredicate(new Condition(
                new EqualsClauseInTrigger(ALIAS1, "power", true)), TriggersWhen.CONDITION_CHANGED);
        TriggerOptions options = TriggerOptions.Builder.newBuilder()
                .setTitle("trigger title")
                .setDescription("trigger description")
                .setMetadata(new JSONObject().put("key1", "value1"))
                .build();
        ServerCode serverCode = new ServerCode("function_name", "token12345");
        this.postNewTriggerWithServerCode(serverCode, predicate, options);
    }

    @Test
    public void postNewServerCodeTrigger_NullOptions_ServerCodeWithFullParams_StatePredicate_Test() throws Exception {
        SchedulePredicate predicate = new SchedulePredicate("1 * * * *");
        ServerCode serverCode = new ServerCode("function_name", "token12345", "app0001", new JSONObject("{\"param\":\"p0001\"}"));
        this.postNewTriggerWithServerCode(serverCode, predicate, null);
    }

    @Test(expected = IllegalStateException.class)
    public void postNewServerCodeTrigger_NullOptions_WithNullTargetTest() throws Exception {
        ServerCode serverCode = new ServerCode("function_name", "token12345", "app0001", new JSONObject("{\"param\":\"p0001\"}"));

        StatePredicate predicate = new StatePredicate(new Condition(
                new EqualsClauseInTrigger(ALIAS1, "power", true)), TriggersWhen.CONDITION_CHANGED);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        api.postNewTrigger(serverCode, predicate);
    }

    @Test(expected = IllegalStateException.class)
    public void postNewServerCodeTrigger_NonNullOptions_WithNullTargetTest() throws Exception {
        TriggerOptions options = TriggerOptions.Builder.newBuilder()
                .setTitle("trigger title")
                .setDescription("trigger description")
                .setMetadata(new JSONObject().put("k", "v")).build();
        ServerCode serverCode = new ServerCode("function_name", null);
        StatePredicate predicate = new StatePredicate(new Condition(
                new EqualsClauseInTrigger(ALIAS1, "power", true)), TriggersWhen.CONDITION_CHANGED);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        api.postNewTrigger(serverCode, predicate, options);
    }

    @Test(expected = IllegalArgumentException.class)
    public void postNewServerCodeTrigger_NullOptions_NullServerCode_Test() throws Exception{
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";

        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        StatePredicate predicate = new StatePredicate(new Condition(
                new EqualsClauseInTrigger(ALIAS1, "power", true)), TriggersWhen.CONDITION_CHANGED);
        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);
        api.postNewTrigger((ServerCode) null, predicate);
    }

    @Test(expected = IllegalArgumentException.class)
    public void postNewServerCodeTrigger_NonNullOptions_NullServerCode_Test() throws Exception{
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";

        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        StatePredicate predicate = new StatePredicate(new Condition(
                new EqualsClauseInTrigger(ALIAS1, "power", true)), TriggersWhen.CONDITION_CHANGED);
        TriggerOptions options = TriggerOptions.Builder.newBuilder()
                .setTitle("trigger title")
                .setDescription("trigger description")
                .setMetadata(new JSONObject().put("k", "v")).build();
        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);
        api.postNewTrigger((ServerCode) null, predicate, options);
    }

    @Test(expected = IllegalArgumentException.class)
    public void postNewServerCodeTrigger_NullOptions_NullPredicate_Test() throws Exception{
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";

        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        ServerCode serverCode = new ServerCode("function_name", "token12345");

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);
        api.postNewTrigger(serverCode, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void postNewServerCodeTrigger_NonNullOptions_NullPredicate_Test() throws Exception{
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";

        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        ServerCode serverCode = new ServerCode("function_name", "token12345");
        TriggerOptions options = TriggerOptions.Builder.newBuilder()
                .setTitle("trigger title")
                .setDescription("trigger description")
                .setMetadata(new JSONObject().put("k", "v")).build();

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);
        api.postNewTrigger(serverCode, null, options);
    }

    @Test
    public void postNewServerCodeTrigger404ErrorTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);
        ServerCode expectedServerCode = new ServerCode("function_name", "token12345");
        StatePredicate predicate = new StatePredicate(new Condition(
                new EqualsClauseInTrigger(ALIAS1, "power", true)), TriggersWhen.CONDITION_CHANGED);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);

        this.addEmptyMockResponse(404);

        try {
            ThingIFAPIUtils.setTarget(api, target);
            api.postNewTrigger(expectedServerCode, predicate);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (NotFoundException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("serverCode", JsonUtil.serverCodeToJson(expectedServerCode));
        expectedRequestBody.put("predicate",JsonUtil.predicateToJson(predicate));
        expectedRequestBody.put("triggersWhat","SERVER_CODE");
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void postNewServerCodeTrigger503ErrorTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);
        ServerCode expectedServerCode = new ServerCode("function_name", "token12345");
        StatePredicate predicate = new StatePredicate(new Condition(
                new EqualsClauseInTrigger(ALIAS1, "power", true)), TriggersWhen.CONDITION_CHANGED);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        this.addEmptyMockResponse(503);

        try {
            ThingIFAPIUtils.setTarget(api, target);
            api.postNewTrigger(expectedServerCode, predicate);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (ServiceUnavailableException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("serverCode", JsonUtil.serverCodeToJson(expectedServerCode));
        expectedRequestBody.put("predicate",JsonUtil.predicateToJson(predicate));
        expectedRequestBody.put("triggersWhat","SERVER_CODE");
        this.assertRequestBody(expectedRequestBody, request);
    }

    @Test
    public void postNewCommandTrigger_FormOnlyHasActions_StatePredicate_Test() throws Exception {
        List<AliasAction> aliasActions = new ArrayList<>();
        List<Action> actions = new ArrayList<>();
        actions.add(new TurnPower(true));
        actions.add(new SetPresetTemperature(23));
        aliasActions.add(new AliasAction(ALIAS1, actions));
        TriggeredCommandForm form = TriggeredCommandForm.Builder.newBuilder(aliasActions).build();
        StatePredicate predicate = new StatePredicate(new Condition(
                new EqualsClauseInTrigger(ALIAS1, "power", true)), TriggersWhen.CONDITION_CHANGED);
        this.postNewTriggerWithCommandTest(form, predicate, null);
    }
    @Test
    public void postNewCommandTrigger_FormOnlyHasActionsAndCommandOption_ScheduledOncePredicate_Test() throws Exception {
        List<AliasAction> aliasActions = new ArrayList<>();
        List<Action> actions = new ArrayList<>();
        actions.add(new TurnPower(true));
        actions.add(new SetPresetTemperature(23));
        aliasActions.add(new AliasAction(ALIAS1, actions));
        TriggeredCommandForm form = TriggeredCommandForm.Builder.newBuilder(aliasActions)
                .setTitle("command title")
                .setDescription("command description")
                .setMetadata(new JSONObject().put("k", "v"))
                .build();
        ScheduleOncePredicate predicate = new ScheduleOncePredicate(System.currentTimeMillis());
        this.postNewTriggerWithCommandTest(form, predicate, null);
    }

    @Test
    public void postNewCommandCrossThingTrigger_FormHasTarget_SchedulePredicate_Test() throws Exception {
        TypedID targetID = new TypedID(TypedID.Types.THING, "another thing");
        List<AliasAction> aliasActions = new ArrayList<>();
        List<Action> actions = new ArrayList<>();
        actions.add(new TurnPower(true));
        actions.add(new SetPresetTemperature(23));
        aliasActions.add(new AliasAction(ALIAS1, actions));
        TriggeredCommandForm form = TriggeredCommandForm.Builder.newBuilder(aliasActions)
                .setTargetID(targetID)
                .setTitle("command title")
                .setDescription("command description")
                .setMetadata(new JSONObject().put("k", "v"))
                .build();
        SchedulePredicate predicate = new SchedulePredicate("1 * * * *");
        this.postNewTriggerWithCommandTest(form, predicate, null);
    }

    @Test
    public void postNewCommandTrigger403ErrorTest2() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        List<AliasAction> aliasActions = new ArrayList<>();
        List<Action> actions = new ArrayList<>();
        actions.add(new TurnPower(true));
        actions.add(new SetPresetTemperature(23));
        aliasActions.add(new AliasAction(ALIAS1, actions));
        TriggeredCommandForm form = TriggeredCommandForm.Builder.newBuilder(aliasActions).build();

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
            api.postNewTrigger(form, predicate);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (ForbiddenException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
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
    public void postNewTrigger404ErrorTest2() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        List<AliasAction> aliasActions = new ArrayList<>();
        List<Action> actions = new ArrayList<>();
        actions.add(new TurnPower(true));
        actions.add(new SetPresetTemperature(23));
        aliasActions.add(new AliasAction(ALIAS1, actions));
        TriggeredCommandForm form = TriggeredCommandForm.Builder.newBuilder(aliasActions).build();

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
            api.postNewTrigger(form, predicate);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (NotFoundException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
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
    public void postNewTrigger503ErrorTest2() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        List<AliasAction> aliasActions = new ArrayList<>();
        List<Action> actions = new ArrayList<>();
        actions.add(new TurnPower(true));
        actions.add(new SetPresetTemperature(23));
        aliasActions.add(new AliasAction(ALIAS1, actions));
        TriggeredCommandForm form = TriggeredCommandForm.Builder.newBuilder(aliasActions).build();

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
            api.postNewTrigger(form, predicate);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (ServiceUnavailableException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
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
    public void postNewCommandTriggerWithNullTargetTest2() throws Exception {

        List<AliasAction> aliasActions = new ArrayList<>();
        List<Action> actions = new ArrayList<>();
        actions.add(new TurnPower(true));
        actions.add(new SetPresetTemperature(23));
        aliasActions.add(new AliasAction(ALIAS1, actions));
        TriggeredCommandForm form = TriggeredCommandForm.Builder.newBuilder(aliasActions).build();

        StatePredicate predicate = new StatePredicate(new Condition(
                new EqualsClauseInTrigger(ALIAS1, "power", true)), TriggersWhen.CONDITION_CHANGED);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        api.postNewTrigger(form, predicate);
    }

    @Test(expected = IllegalArgumentException.class)
    public void postNewCommandTrigger_NullForm_Test2() throws Exception{
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";

        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        StatePredicate predicate = new StatePredicate(new Condition(
                new EqualsClauseInTrigger(ALIAS1, "power", true)), TriggersWhen.CONDITION_CHANGED);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);
        api.postNewTrigger((TriggeredCommandForm) null, predicate);
    }

    @Test(expected = IllegalArgumentException.class)
    public void postNewCommandTrigger_NullPredicate2_Test() throws Exception{
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";

        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        List<AliasAction> aliasActions = new ArrayList<>();
        List<Action> actions = new ArrayList<>();
        actions.add(new TurnPower(true));
        actions.add(new SetPresetTemperature(23));
        aliasActions.add(new AliasAction(ALIAS1, actions));
        TriggeredCommandForm form = TriggeredCommandForm.Builder.newBuilder(aliasActions).build();

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);
        api.postNewTrigger(form, null);
    }

}
