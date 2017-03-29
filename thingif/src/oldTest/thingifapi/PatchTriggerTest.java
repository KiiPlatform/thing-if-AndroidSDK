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
public class PatchTriggerTest extends ThingIFAPITestBase {
    private Context context;
    private ThingIFAPI defaultApi;
    @Before
    public void before() throws Exception {
        context = RuntimeEnvironment.application.getApplicationContext();
        this.server = new MockWebServer();
        this.server.start();
        this.defaultApi = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        Target target = new StandaloneThing("thing1", "vendor thing id 1", "dummyToken");
        ThingIFAPIUtils.setTarget(this.defaultApi, target);
    }

    @After
    public void after() throws Exception {
        this.server.shutdown();
    }

    private List<AliasAction<? extends Action>> getDefaultActions() {
        List<AliasAction<? extends Action>> actions = new ArrayList<>();
        actions.add(new AliasAction<Action>(
                ALIAS1,
                new AirConditionerActions(true, null)));
        actions.add(new AliasAction<Action>(
                ALIAS2,
                new HumidityActions(45)));
        return actions;
    }

    private Predicate getDefaultPredicate() {
      return new SchedulePredicate("1 * * * *");
    }

    private TriggerOptions getDefaultOptions() throws Exception{
        return TriggerOptions.Builder.newBuilder()
                .setTitle("trigger title")
                .setDescription("trigger description")
                .setMetadata(new JSONObject().put("key", "value"))
                .build();
    }

    private Command getDefaultCommand() throws Exception {
        return CommandFactory.newTriggeredCommand(
                this.defaultApi.getOwner().getTypedID(),
                getDefaultActions(),
                this.defaultApi.getTarget().getTypedID(),
                "command title",
                "command description",
                new JSONObject().put("key", "value"));
    }

    private TriggeredCommandForm getDefaultForm() throws Exception{
        return TriggeredCommandForm.Builder.newBuilder(getDefaultActions())
                .setTitle(getDefaultCommand().getTitle())
                .setDescription(getDefaultCommand().getDescription())
                .setMetadata(getDefaultCommand().getMetadata()).build();
    }

    private ServerCode getDefaultServerCode() throws Exception {
        return new ServerCode(
                "function_name",
                "token12345",
                "app0001",
                new JSONObject("{\"param\":\"p0001\"}"));
    }

    @Test
    //call patchTrigger(String, TriggeredCommandForm, StatePredicate, TriggerOptions)
    public void patchCommandTrigger_FormOnlyHasActions_StatePredicate_NonNullOptions_Test() throws Exception {

        List<AliasAction<? extends Action>> actions = getDefaultActions();
        TriggeredCommandForm form = TriggeredCommandForm.Builder.newBuilder(actions).build();
        StatePredicate predicate = new StatePredicate(new Condition(
                new EqualsClauseInTrigger(ALIAS1, "power", true)), TriggersWhen.CONDITION_CHANGED);
        TriggerOptions options = TriggerOptions.Builder.newBuilder()
                .setTitle("trigger title")
                .setDescription("trigger description")
                .setMetadata(new JSONObject().put("key1", "value1"))
                .build();

        String triggerID = "trigger-1234";

        Assert.assertNotNull(this.defaultApi.getTarget());
        Command expectedCommand = CommandFactory.newTriggeredCommand(
                this.defaultApi.getOwner().getTypedID(),
                actions,
                this.defaultApi.getTarget().getTypedID(),
                null,
                null,
                null);

        this.addEmptyMockResponse(204);
        this.addMockResponseForGetTriggerWithCommand(
                200,
                triggerID,
                expectedCommand,
                predicate,
                options,
                false,
                null);

        Trigger trigger = this.defaultApi.patchCommandTrigger(triggerID, form, predicate, options);
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(false, trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertNull(trigger.getServerCode());
        assertSamePredicate(predicate, trigger.getPredicate());
        assertSameCommands(expectedCommand, trigger.getCommand());
        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        String thingID = this.defaultApi.getTarget().getTypedID().toString();
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/"+ triggerID, request1.getPath());
        Assert.assertEquals("PATCH", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("command", JsonUtil.commandToJson(expectedCommand));
        expectedRequestBody.put("predicate", JsonUtil.predicateToJson(predicate));
        expectedRequestBody.put("triggersWhat","COMMAND");
        expectedRequestBody.putOpt("title", options.getTitle());
        expectedRequestBody.putOpt("description", options.getDescription());
        expectedRequestBody.putOpt("metadata", options.getMetadata());
        this.assertRequestBody(expectedRequestBody, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/" + triggerID, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());

        Map<String, String> expectedRequestHeaders2 = new HashMap<>();
        expectedRequestHeaders2.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders2.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders2.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders2, request2);
    }
    @Test
    // call patchTrigger(String, TriggeredCommandForm, ScheduleOncePredicate, TriggerOptions)
    public void patchCommandTrigger_FormHasActionsAndCommandOption_ScheduledOncePredicate_NonNullOptions_Test() throws Exception {
        List<AliasAction<? extends Action>> actions = getDefaultActions();
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

        String triggerID = "trigger-1234";

        Assert.assertNotNull(this.defaultApi.getTarget());
        Command expectedCommand = CommandFactory.newTriggeredCommand(
                this.defaultApi.getOwner().getTypedID(),
                actions,
                this.defaultApi.getTarget().getTypedID(),
                form.getTitle(),
                form.getDescription(),
                form.getMetadata());

        this.addEmptyMockResponse(204);
        this.addMockResponseForGetTriggerWithCommand(
                200,
                triggerID,
                expectedCommand,
                predicate,
                options,
                false,
                null);

        Trigger trigger = this.defaultApi.patchCommandTrigger(triggerID, form, predicate, options);
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(false, trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertNull(trigger.getServerCode());
        assertSamePredicate(predicate, trigger.getPredicate());
        assertSameCommands(expectedCommand, trigger.getCommand());
        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        String thingID = this.defaultApi.getTarget().getTypedID().toString();
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/"+ triggerID, request1.getPath());
        Assert.assertEquals("PATCH", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("command", JsonUtil.commandToJson(expectedCommand));
        expectedRequestBody.put("predicate", JsonUtil.predicateToJson(predicate));
        expectedRequestBody.put("triggersWhat","COMMAND");
        expectedRequestBody.putOpt("title", options.getTitle());
        expectedRequestBody.putOpt("description", options.getDescription());
        expectedRequestBody.putOpt("metadata", options.getMetadata());
        this.assertRequestBody(expectedRequestBody, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/" + triggerID, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());

        Map<String, String> expectedRequestHeaders2 = new HashMap<>();
        expectedRequestHeaders2.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders2.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders2.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders2, request2);
    }

    @Test
    // call patchTrigger(String, TriggeredCommandForm, SchedulePredicate, TriggerOptions)
    public void patchCommandTrigger_FormHasTarget_SchedulePredicate_NonnullOptions_Test() throws Exception {
        List<AliasAction<? extends Action>> actions = new ArrayList<>();
        TypedID targetID = new TypedID(TypedID.Types.THING, "another thing");
        actions.add(new AliasAction<Action>(
                ALIAS1,
                new AirConditionerActions(true, null)));
        TriggeredCommandForm form = TriggeredCommandForm.Builder.newBuilder(actions)
                .setTargetID(targetID)
                .build();
        SchedulePredicate predicate = new SchedulePredicate("1 * * * *");
        TriggerOptions options = TriggerOptions.Builder.newBuilder()
                .setTitle("trigger title")
                .setMetadata(new JSONObject().put("key1", "value1"))
                .build();

        String triggerID = "trigger-1234";

        Assert.assertNotNull(this.defaultApi.getTarget());
        Command expectedCommand = CommandFactory.newTriggeredCommand(
                this.defaultApi.getOwner().getTypedID(),
                actions,
                targetID,
                null,
                null,
                null);

        this.addEmptyMockResponse(204);
        this.addMockResponseForGetTriggerWithCommand(
                200,
                triggerID,
                expectedCommand,
                predicate,
                options,
                false,
                null);

        Trigger trigger = this.defaultApi.patchCommandTrigger(triggerID, form, predicate, options);
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(false, trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertNull(trigger.getServerCode());
        assertSamePredicate(predicate, trigger.getPredicate());
        assertSameCommands(expectedCommand, trigger.getCommand());
        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        String thingID = this.defaultApi.getTarget().getTypedID().toString();
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/"+ triggerID, request1.getPath());
        Assert.assertEquals("PATCH", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("command", JsonUtil.commandToJson(expectedCommand));
        expectedRequestBody.put("predicate", JsonUtil.predicateToJson(predicate));
        expectedRequestBody.put("triggersWhat","COMMAND");
        expectedRequestBody.putOpt("title", options.getTitle());
        expectedRequestBody.putOpt("description", options.getDescription());
        expectedRequestBody.putOpt("metadata", options.getMetadata());
        this.assertRequestBody(expectedRequestBody, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/" + triggerID, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());

        Map<String, String> expectedRequestHeaders2 = new HashMap<>();
        expectedRequestHeaders2.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders2.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders2.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders2, request2);
    }

    @Test
    // call patchTrigger(String, TriggeredCommandForm, null, TriggerOptions)
    public void patchCommandTrigger_Form_NullPredicate_NonNullOptions_Test() throws Exception {
        List<AliasAction<? extends Action>> actions = new ArrayList<>();
        actions.add(new AliasAction<Action>(
                ALIAS1,
                new AirConditionerActions(true, null)));
        TriggeredCommandForm form = TriggeredCommandForm.Builder.newBuilder(actions).build();
        TriggerOptions options = TriggerOptions.Builder.newBuilder()
                .setTitle("cross thing trigger title")
                .setDescription("cross thing trigger description")
                .build();

        String triggerID = "trigger-1234";

        Assert.assertNotNull(this.defaultApi.getTarget());
        Command expectedCommand = CommandFactory.newTriggeredCommand(
                this.defaultApi.getOwner().getTypedID(),
                actions,
                this.defaultApi.getTarget().getTypedID(),
                null,
                null,
                null);

        this.addEmptyMockResponse(204);
        this.addMockResponseForGetTriggerWithCommand(
                200,
                triggerID,
                expectedCommand,
                getDefaultPredicate(),
                options,
                false,
                null);

        Trigger trigger = this.defaultApi.patchCommandTrigger(triggerID, form, null, options);
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(false, trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertNull(trigger.getServerCode());
        assertSameCommands(expectedCommand, trigger.getCommand());
        assertSamePredicate(getDefaultPredicate(), trigger.getPredicate());
        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        String thingID = this.defaultApi.getTarget().getTypedID().toString();
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/"+ triggerID, request1.getPath());
        Assert.assertEquals("PATCH", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("command", JsonUtil.commandToJson(expectedCommand));
        expectedRequestBody.put("triggersWhat","COMMAND");
        expectedRequestBody.putOpt("title", options.getTitle());
        expectedRequestBody.putOpt("description", options.getDescription());
        expectedRequestBody.putOpt("metadata", options.getMetadata());
        this.assertRequestBody(expectedRequestBody, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/" + triggerID, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());

        Map<String, String> expectedRequestHeaders2 = new HashMap<>();
        expectedRequestHeaders2.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders2.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders2.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders2, request2);
    }

    @Test
    // call patchTrigger(String, null, SchedulePredicate, TriggerOptions)
    public void patchCommandTrigger_NullForm_SchedulePredicate_NonNullOptions_Test() throws Exception {
        SchedulePredicate predicate = new SchedulePredicate("1 * * * *");
        TriggerOptions options = TriggerOptions.Builder.newBuilder()
                .setTitle("cross thing trigger title")
                .setDescription("cross thing trigger description")
                .build();
        String triggerID = "trigger-1234";

        Assert.assertNotNull(this.defaultApi.getTarget());

        this.addEmptyMockResponse(204);
        this.addMockResponseForGetTriggerWithCommand(
                200,
                triggerID,
                getDefaultCommand(),
                getDefaultPredicate(),
                options,
                false,
                null);

        Trigger trigger = this.defaultApi.patchCommandTrigger(triggerID, null, predicate, options);
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(false, trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertNull(trigger.getServerCode());
        assertSameCommands(getDefaultCommand(), trigger.getCommand());
        assertSamePredicate(getDefaultPredicate(), trigger.getPredicate());
        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        String thingID = this.defaultApi.getTarget().getTypedID().toString();
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/"+ triggerID, request1.getPath());
        Assert.assertEquals("PATCH", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("triggersWhat","COMMAND");
        expectedRequestBody.putOpt("predicate", JsonUtil.predicateToJson(predicate));
        expectedRequestBody.putOpt("title", options.getTitle());
        expectedRequestBody.putOpt("description", options.getDescription());
        expectedRequestBody.putOpt("metadata", options.getMetadata());
        this.assertRequestBody(expectedRequestBody, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/" + triggerID, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());

        Map<String, String> expectedRequestHeaders2 = new HashMap<>();
        expectedRequestHeaders2.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders2.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders2.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders2, request2);
    }
    @Test
    // call patchTrigger(String, TriggeredCommandForm, StatePredicate, null)
    public void patchCommandTrigger_StatePredicate_NullOptions_Test() throws Exception {
        List<AliasAction<? extends Action>> actions = getDefaultActions();
        TriggeredCommandForm form = TriggeredCommandForm.Builder.newBuilder(actions).build();

        StatePredicate predicate = new StatePredicate(new Condition(
                new EqualsClauseInTrigger(ALIAS1, "power", true)), TriggersWhen.CONDITION_CHANGED);

        String triggerID = "trigger-1234";

        Assert.assertNotNull(this.defaultApi.getTarget());
        Command expectedCommand = CommandFactory.newTriggeredCommand(
                this.defaultApi.getOwner().getTypedID(),
                actions,
                this.defaultApi.getTarget().getTypedID(),
                null,
                null,
                null);

        this.addEmptyMockResponse(204);
        this.addMockResponseForGetTriggerWithCommand(
                200,
                triggerID,
                expectedCommand,
                predicate,
                null,
                false,
                null);

        Trigger trigger = this.defaultApi.patchCommandTrigger(triggerID, form, predicate, null);
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(false, trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertNull(trigger.getServerCode());
        assertSamePredicate(predicate, trigger.getPredicate());
        assertSameCommands(expectedCommand, trigger.getCommand());
        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        String thingID = this.defaultApi.getTarget().getTypedID().toString();
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/"+ triggerID, request1.getPath());
        Assert.assertEquals("PATCH", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("command", JsonUtil.commandToJson(expectedCommand));
        expectedRequestBody.put("predicate", JsonUtil.predicateToJson(predicate));
        expectedRequestBody.put("triggersWhat","COMMAND");
        this.assertRequestBody(expectedRequestBody, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/" + triggerID, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());

        Map<String, String> expectedRequestHeaders2 = new HashMap<>();
        expectedRequestHeaders2.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders2.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders2.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders2, request2);
    }

    @Test
    // call patchTrigger(String, TriggeredCommandForm, Predicate, TriggerOptions)
    public void patchCommandTrigger403ErrorTest() throws Exception {
        Assert.assertNotNull(this.defaultApi.getTarget());
        TypedID thingID = this.defaultApi.getTarget().getTypedID();

        TriggeredCommandForm form = getDefaultForm();
        Predicate predicate = getDefaultPredicate();

        Command expectedCommand = getDefaultCommand();
        this.addEmptyMockResponse(403);

        try {
            this.defaultApi.patchCommandTrigger("trigger-1234", form, predicate, null);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (ForbiddenException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/trigger-1234", request.getPath());
        Assert.assertEquals("PATCH", request.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("command", JsonUtil.commandToJson(expectedCommand));
        expectedRequestBody.put("predicate",JsonUtil.predicateToJson(predicate));
        expectedRequestBody.put("triggersWhat","COMMAND");
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    // call patchTrigger(String, TriggeredCommandForm, Predicate, TriggerOptions)
    public void patchTrigger404ErrorTest() throws Exception {
        Assert.assertNotNull(this.defaultApi.getTarget());
        TypedID thingID = this.defaultApi.getTarget().getTypedID();

        TriggeredCommandForm form = getDefaultForm();
        Predicate predicate = getDefaultPredicate();

        Command expectedCommand = getDefaultCommand();
        this.addEmptyMockResponse(404);

        try {
            this.defaultApi.patchCommandTrigger("trigger-1234", form, predicate, null);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (NotFoundException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/trigger-1234", request.getPath());
        Assert.assertEquals("PATCH", request.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("command", JsonUtil.commandToJson(expectedCommand));
        expectedRequestBody.put("predicate",JsonUtil.predicateToJson(predicate));
        expectedRequestBody.put("triggersWhat","COMMAND");
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    // call patchTrigger(String, TriggeredCommandForm, Predicate, TriggerOptions)
    public void patchTrigger503ErrorTest() throws Exception {
        Assert.assertNotNull(this.defaultApi.getTarget());
        TypedID thingID = this.defaultApi.getTarget().getTypedID();

        TriggeredCommandForm form = getDefaultForm();
        Predicate predicate = getDefaultPredicate();

        Command expectedCommand = getDefaultCommand();
        this.addEmptyMockResponse(503);

        try {
            this.defaultApi.patchCommandTrigger("trigger-1234", form, predicate, null);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (ServiceUnavailableException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/trigger-1234", request.getPath());
        Assert.assertEquals("PATCH", request.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("command", JsonUtil.commandToJson(expectedCommand));
        expectedRequestBody.put("predicate",JsonUtil.predicateToJson(predicate));
        expectedRequestBody.put("triggersWhat","COMMAND");
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test(expected = IllegalStateException.class)
    // call patchTrigger(String, TriggeredCommandForm, Predicate, TriggerOptions)
    public void patchCommandTriggerWithNullTargetTest() throws Exception {
        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        api.patchCommandTrigger("trigger-1234", getDefaultForm(), getDefaultPredicate(), getDefaultOptions());
    }

    @Test(expected = IllegalArgumentException.class)
    // call patchTrigger(String, null, null, null)
    public void patchCommandTrigger_NullForm_NullPredicate_NullOptions_Test() throws Exception{
        this.defaultApi.patchCommandTrigger("trigger-1234", null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    // call patchTrigger(null, TriggeredCommandForm, Predicate, TriggerOptions)
    public void patchCommandTrigger_NullTriggerID_Form_Predicate_Options_Test() throws Exception{
        this.defaultApi.patchCommandTrigger(
                null,
                getDefaultForm(),
                getDefaultPredicate(),
                getDefaultOptions());
    }

    @Test(expected = IllegalArgumentException.class)
    // call patchTrigger("", TriggeredCommandForm, Predicate, TriggerOptions)
    public void patchCommandTrigger_EmptyTriggerID_Form_Predicate_Options_Test() throws Exception{
        this.defaultApi.patchCommandTrigger(
                "",
                getDefaultForm(),
                getDefaultPredicate(),
                getDefaultOptions());
    }

    @Test
    // call patchTrigger(String, ServerCode, StatePredicate, TriggerOptions)
    public void patchServerCodeTrigger_NonNullOptions_ServerCodeWithFullParams_StatePredicate_Test() throws Exception {
        StatePredicate predicate = new StatePredicate(new Condition(
                new EqualsClauseInTrigger(ALIAS1, "power", true)), TriggersWhen.CONDITION_CHANGED);        TriggerOptions options = getDefaultOptions();
        ServerCode serverCode = new ServerCode("function_name", "token12345", "app0001", new JSONObject("{\"param\":\"p0001\"}"));

        String triggerID = "trigger-1234";
        this.addEmptyMockResponse(204);
        this.addMockResponseForGetTriggerWithServerCode(200, triggerID, serverCode, predicate, options, false, null);

        Trigger trigger;
        trigger = this.defaultApi.patchServerCodeTrigger(triggerID, serverCode, predicate, options);
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(false, trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertNull(trigger.getCommand());
        assertSamePredicate(predicate, trigger.getPredicate());
        this.assertServerCode(serverCode, trigger.getServerCode());
        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertNotNull(this.defaultApi.getTarget());
        String thingID = this.defaultApi.getTarget().getTypedID().toString();
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/"+triggerID, request1.getPath());
        Assert.assertEquals("PATCH", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("serverCode", JsonUtil.serverCodeToJson(serverCode));
        expectedRequestBody.put("predicate", JsonUtil.predicateToJson(predicate));
        expectedRequestBody.put("triggersWhat","SERVER_CODE");
        expectedRequestBody.putOpt("title", options.getTitle());
        expectedRequestBody.putOpt("description", options.getDescription());
        expectedRequestBody.putOpt("metadata", options.getMetadata());
        this.assertRequestBody(expectedRequestBody, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/" + triggerID, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());

        Map<String, String> expectedRequestHeaders2 = new HashMap<>();
        expectedRequestHeaders2.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders2.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders2.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders2, request2);
    }
    @Test
    // call patchTrigger(String, ServerCode, SchedulePredicate, TriggerOptions)
    public void patchServerCodeTrigger_NonNullOptions_ServerCodeWithEndPointAndToken_SchedulePredicate_Test() throws Exception {
        SchedulePredicate predicate = new SchedulePredicate("1 * * * * *");
        TriggerOptions options = getDefaultOptions();
        ServerCode serverCode = new ServerCode("function_name", "token12345");

        String triggerID = "trigger-1234";
        this.addEmptyMockResponse(204);
        this.addMockResponseForGetTriggerWithServerCode(200, triggerID, serverCode, predicate, options, false, null);

        Trigger trigger;
        trigger = this.defaultApi.patchServerCodeTrigger(triggerID, serverCode, predicate, options);
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(false, trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertNull(trigger.getCommand());
        assertSamePredicate(predicate, trigger.getPredicate());
        this.assertServerCode(serverCode, trigger.getServerCode());
        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertNotNull(this.defaultApi.getTarget());
        String thingID = this.defaultApi.getTarget().getTypedID().toString();
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/"+triggerID, request1.getPath());
        Assert.assertEquals("PATCH", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("serverCode", JsonUtil.serverCodeToJson(serverCode));
        expectedRequestBody.put("predicate", JsonUtil.predicateToJson(predicate));
        expectedRequestBody.put("triggersWhat","SERVER_CODE");
        expectedRequestBody.putOpt("title", options.getTitle());
        expectedRequestBody.putOpt("description", options.getDescription());
        expectedRequestBody.putOpt("metadata", options.getMetadata());
        this.assertRequestBody(expectedRequestBody, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/" + triggerID, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());

        Map<String, String> expectedRequestHeaders2 = new HashMap<>();
        expectedRequestHeaders2.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders2.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders2.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders2, request2);
    }
    @Test
    // call patchTrigger(String, ServerCode, ScheduleOncePredicate, TriggerOptions)
    public void patchServerCodeTrigger_NonNullOptions_ServerCodeWithEndPointAndNullToken_ScheduleOncePredicate_Test() throws Exception {
        ScheduleOncePredicate predicate = new ScheduleOncePredicate(System.currentTimeMillis());
        TriggerOptions options = getDefaultOptions();
        ServerCode serverCode = new ServerCode("function_name", null);
        String triggerID = "trigger-1234";
        this.addEmptyMockResponse(204);
        this.addMockResponseForGetTriggerWithServerCode(200, triggerID, serverCode, predicate, options, false, null);

        Trigger trigger;
        trigger = this.defaultApi.patchServerCodeTrigger(triggerID, serverCode, predicate, options);
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(false, trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertNull(trigger.getCommand());
        assertSamePredicate(predicate, trigger.getPredicate());
        this.assertServerCode(serverCode, trigger.getServerCode());
        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertNotNull(this.defaultApi.getTarget());
        String thingID = this.defaultApi.getTarget().getTypedID().toString();
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/"+triggerID, request1.getPath());
        Assert.assertEquals("PATCH", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("serverCode", JsonUtil.serverCodeToJson(serverCode));
        expectedRequestBody.put("predicate", JsonUtil.predicateToJson(predicate));
        expectedRequestBody.put("triggersWhat","SERVER_CODE");
        expectedRequestBody.putOpt("title", options.getTitle());
        expectedRequestBody.putOpt("description", options.getDescription());
        expectedRequestBody.putOpt("metadata", options.getMetadata());
        this.assertRequestBody(expectedRequestBody, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/" + triggerID, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());

        Map<String, String> expectedRequestHeaders2 = new HashMap<>();
        expectedRequestHeaders2.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders2.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders2.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders2, request2);
    }

    @Test
    // call patchTrigger(String, ServerCode, Predicate, null)
    public void patchServerCodeTrigger_NullOptions_ServerCodeWithFullParams_SchedulePredicate_Test() throws Exception {
        SchedulePredicate predicate = new SchedulePredicate("1 * * * *");
        ServerCode serverCode = new ServerCode("function_name", "token12345", "app0001", new JSONObject("{\"param\":\"p0001\"}"));
        String triggerID = "trigger-1234";
        this.addEmptyMockResponse(204);
        this.addMockResponseForGetTriggerWithServerCode(200, triggerID, serverCode, predicate, null, false, null);

        Trigger trigger;
        trigger = this.defaultApi.patchServerCodeTrigger(triggerID, serverCode, predicate, null);
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(false, trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertNull(trigger.getCommand());
        assertSamePredicate(predicate, trigger.getPredicate());
        this.assertServerCode(serverCode, trigger.getServerCode());
        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertNotNull(this.defaultApi.getTarget());
        String thingID = this.defaultApi.getTarget().getTypedID().toString();
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/"+triggerID, request1.getPath());
        Assert.assertEquals("PATCH", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("serverCode", JsonUtil.serverCodeToJson(serverCode));
        expectedRequestBody.put("predicate", JsonUtil.predicateToJson(predicate));
        expectedRequestBody.put("triggersWhat","SERVER_CODE");
        this.assertRequestBody(expectedRequestBody, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/" + triggerID, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());

        Map<String, String> expectedRequestHeaders2 = new HashMap<>();
        expectedRequestHeaders2.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders2.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders2.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders2, request2);
    }

    @Test
    // call patchTrigger(String, null, StatePredicate, TriggerOptions)
    public void patchServerCodeTrigger_NonNullOptions_NullServerCode_StatePredicate_Test() throws Exception {
        StatePredicate predicate = new StatePredicate(new Condition(
                new EqualsClauseInTrigger(ALIAS1, "power", true)), TriggersWhen.CONDITION_CHANGED);        TriggerOptions options = getDefaultOptions();

        String triggerID = "trigger-1234";
        this.addEmptyMockResponse(204);
        this.addMockResponseForGetTriggerWithServerCode(
                200,
                triggerID,
                getDefaultServerCode(),
                predicate,
                options,
                false,
                null);

        Trigger trigger;
        trigger = this.defaultApi.patchServerCodeTrigger(triggerID, (ServerCode) null, predicate, options);
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(false, trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertNull(trigger.getCommand());
        assertSamePredicate(predicate, trigger.getPredicate());
        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertNotNull(this.defaultApi.getTarget());
        String thingID = this.defaultApi.getTarget().getTypedID().toString();
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/"+triggerID, request1.getPath());
        Assert.assertEquals("PATCH", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("predicate", JsonUtil.predicateToJson(predicate));
        expectedRequestBody.put("triggersWhat","SERVER_CODE");
        expectedRequestBody.putOpt("title", options.getTitle());
        expectedRequestBody.putOpt("description", options.getDescription());
        expectedRequestBody.putOpt("metadata", options.getMetadata());
        this.assertRequestBody(expectedRequestBody, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/" + triggerID, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());

        Map<String, String> expectedRequestHeaders2 = new HashMap<>();
        expectedRequestHeaders2.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders2.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders2.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders2, request2);
    }

    @Test
    // call patchTrigger(String, ServerCode, null, TriggerOptions)
    public void patchServerCodeTrigger_NonNullOptions_ServerCode_NullStatePredicate_Test() throws Exception {

        TriggerOptions options = getDefaultOptions();
        String triggerID = "trigger-1234";
        this.addEmptyMockResponse(204);
        this.addMockResponseForGetTriggerWithServerCode(
                200,
                triggerID,
                getDefaultServerCode(),
                getDefaultPredicate(),
                options,
                false,
                null);

        Trigger trigger;
        trigger = this.defaultApi.patchServerCodeTrigger(
                triggerID,
                getDefaultServerCode(),
                null,
                options);
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(false, trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertNull(trigger.getCommand());
        assertServerCode(getDefaultServerCode(), trigger.getServerCode());
        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertNotNull(this.defaultApi.getTarget());
        String thingID = this.defaultApi.getTarget().getTypedID().toString();
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/"+triggerID, request1.getPath());
        Assert.assertEquals("PATCH", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("triggersWhat","SERVER_CODE");
        expectedRequestBody.put("serverCode", JsonUtil.serverCodeToJson(getDefaultServerCode()));
        expectedRequestBody.putOpt("title", options.getTitle());
        expectedRequestBody.putOpt("description", options.getDescription());
        expectedRequestBody.putOpt("metadata", options.getMetadata());
        this.assertRequestBody(expectedRequestBody, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/" + triggerID, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());

        Map<String, String> expectedRequestHeaders2 = new HashMap<>();
        expectedRequestHeaders2.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders2.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders2.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders2, request2);
    }

    @Test
    // call patchTrigger(String, null, null, TriggerOptions)
    public void patchServerCodeTrigger_NonNullOptions_NullServerCode_NullStatePredicate_Test() throws Exception {

        TriggerOptions options = getDefaultOptions();
        String triggerID = "trigger-1234";
        this.addEmptyMockResponse(204);
        this.addMockResponseForGetTriggerWithServerCode(
                200,
                triggerID,
                getDefaultServerCode(),
                getDefaultPredicate(),
                options,
                false,
                null);

        Trigger trigger;
        trigger = this.defaultApi.patchServerCodeTrigger(
                triggerID,
                (ServerCode) null,
                null,
                options);
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(false, trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertNull(trigger.getCommand());
        assertServerCode(getDefaultServerCode(), trigger.getServerCode());
        assertSamePredicate(getDefaultPredicate(), trigger.getPredicate());
        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertNotNull(this.defaultApi.getTarget());
        String thingID = this.defaultApi.getTarget().getTypedID().toString();
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/"+triggerID, request1.getPath());
        Assert.assertEquals("PATCH", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("triggersWhat","SERVER_CODE");
        expectedRequestBody.putOpt("title", options.getTitle());
        expectedRequestBody.putOpt("description", options.getDescription());
        expectedRequestBody.putOpt("metadata", options.getMetadata());
        this.assertRequestBody(expectedRequestBody, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/" + triggerID, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());

        Map<String, String> expectedRequestHeaders2 = new HashMap<>();
        expectedRequestHeaders2.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders2.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders2.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders2, request2);
    }

    @Test(expected = IllegalStateException.class)
    // call patchTrigger(String, ServerCode, Predicate, TriggerOptions)
    public void patchServerCodeTrigger_NullOptions_WithNullTargetTest() throws Exception {
        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        api.patchServerCodeTrigger(
                "trigger-1234",
                getDefaultServerCode(),
                getDefaultPredicate(),
                getDefaultOptions());
    }

    @Test(expected = IllegalArgumentException.class)
    // call patchTrigger(String, null, null, null)
    public void patchServerCodeTrigger_NullOptions_NullServerCode_NullPredicate_Test() throws Exception{
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";

        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);
        api.patchServerCodeTrigger("trigger-1234", (ServerCode) null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    // call patchTrigger(null, ServerCode, Predicate, TriggerOptions)
    public void patchServerCodeTrigger_NullTriggerID_NonNullOptions_ServerCode_Predicate_Test() throws Exception{
        this.defaultApi.patchServerCodeTrigger(
                null,
                getDefaultServerCode(),
                getDefaultPredicate(),
                getDefaultOptions());
    }

    @Test(expected = IllegalArgumentException.class)
    // call patchTrigger("", ServerCode, Predicate, TriggerOptions)
    public void patchServerCodeTrigger_EmptyTriggerID_NonNullOptions_ServerCode_Predicate_Test() throws Exception{
        this.defaultApi.patchServerCodeTrigger(
                "",
                getDefaultServerCode(),
                getDefaultPredicate(),
                getDefaultOptions());
    }

    @Test
    // call patchTrigger(String, ServerCode, Predicate, TriggerOptions)
    public void patchServerCodeTrigger404ErrorTest() throws Exception {
        ServerCode expectedServerCode = getDefaultServerCode();
        Predicate predicate = getDefaultPredicate();

        this.addEmptyMockResponse(404);

        try {
            this.defaultApi.patchServerCodeTrigger("trigger-1234", expectedServerCode, predicate);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (NotFoundException e) {
        }
        // verify the request
        Assert.assertNotNull(this.defaultApi.getTarget());
        String thingID = this.defaultApi.getTarget().getTypedID().toString();
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/trigger-1234", request.getPath());
        Assert.assertEquals("PATCH", request.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("serverCode", JsonUtil.serverCodeToJson(expectedServerCode));
        expectedRequestBody.put("predicate",JsonUtil.predicateToJson(predicate));
        expectedRequestBody.put("triggersWhat","SERVER_CODE");
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    // call patchTrigger(String, ServerCode, Predicate, TriggerOptions)
    public void patchServerCodeTrigger503ErrorTest() throws Exception {
        ServerCode expectedServerCode = getDefaultServerCode();
        Predicate predicate = getDefaultPredicate();

        this.addEmptyMockResponse(503);

        try {
            this.defaultApi.patchServerCodeTrigger("trigger-1234", expectedServerCode, predicate);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (ServiceUnavailableException e) {
        }
        // verify the request
        Assert.assertNotNull(this.defaultApi.getTarget());
        String thingID = this.defaultApi.getTarget().getTypedID().toString();
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/trigger-1234", request.getPath());
        Assert.assertEquals("PATCH", request.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("serverCode", JsonUtil.serverCodeToJson(expectedServerCode));
        expectedRequestBody.put("predicate",JsonUtil.predicateToJson(predicate));
        expectedRequestBody.put("triggersWhat","SERVER_CODE");
        this.assertRequestBody(expectedRequestBody, request);
    }


    @Test
    // call patchTrigger(String, ServerCode, StatePredicate)
    public void patchServerCodeTrigger_ServerCodeWithFullParams_StatePredicate_Test() throws Exception {
        StatePredicate predicate = new StatePredicate(new Condition(
                new EqualsClauseInTrigger(ALIAS1, "power", true)), TriggersWhen.CONDITION_CHANGED);        TriggerOptions options = getDefaultOptions();
        ServerCode serverCode = new ServerCode("function_name", "token12345", "app0001", new JSONObject("{\"param\":\"p0001\"}"));

        String triggerID = "trigger-1234";
        this.addEmptyMockResponse(204);
        this.addMockResponseForGetTriggerWithServerCode(200, triggerID, serverCode, predicate, options, false, null);

        Trigger trigger;
        trigger = this.defaultApi.patchServerCodeTrigger(triggerID, serverCode, predicate);
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(false, trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertNull(trigger.getCommand());
        assertSamePredicate(predicate, trigger.getPredicate());
        this.assertServerCode(serverCode, trigger.getServerCode());
        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertNotNull(this.defaultApi.getTarget());
        String thingID = this.defaultApi.getTarget().getTypedID().toString();
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/"+triggerID, request1.getPath());
        Assert.assertEquals("PATCH", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("serverCode", JsonUtil.serverCodeToJson(serverCode));
        expectedRequestBody.put("predicate", JsonUtil.predicateToJson(predicate));
        expectedRequestBody.put("triggersWhat","SERVER_CODE");
        this.assertRequestBody(expectedRequestBody, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/" + triggerID, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());

        Map<String, String> expectedRequestHeaders2 = new HashMap<>();
        expectedRequestHeaders2.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders2.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders2.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders2, request2);
    }
    @Test
    // call patchTrigger(String, ServerCode, SchedulePredicate)
    public void patchServerCodeTrigger_ServerCodeWithEndPointAndToken_SchedulePredicate_Test() throws Exception {
        SchedulePredicate predicate = new SchedulePredicate("1 * * * * *");
        TriggerOptions options = getDefaultOptions();
        ServerCode serverCode = new ServerCode("function_name", "token12345");

        String triggerID = "trigger-1234";
        this.addEmptyMockResponse(204);
        this.addMockResponseForGetTriggerWithServerCode(200, triggerID, serverCode, predicate, options, false, null);

        Trigger trigger;
        trigger = this.defaultApi.patchServerCodeTrigger(triggerID, serverCode, predicate);
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(false, trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertNull(trigger.getCommand());
        assertSamePredicate(predicate, trigger.getPredicate());
        this.assertServerCode(serverCode, trigger.getServerCode());
        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertNotNull(this.defaultApi.getTarget());
        String thingID = this.defaultApi.getTarget().getTypedID().toString();
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/"+triggerID, request1.getPath());
        Assert.assertEquals("PATCH", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("serverCode", JsonUtil.serverCodeToJson(serverCode));
        expectedRequestBody.put("predicate", JsonUtil.predicateToJson(predicate));
        expectedRequestBody.put("triggersWhat","SERVER_CODE");
        this.assertRequestBody(expectedRequestBody, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/" + triggerID, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());

        Map<String, String> expectedRequestHeaders2 = new HashMap<>();
        expectedRequestHeaders2.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders2.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders2.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders2, request2);
    }
    @Test
    // call patchTrigger(String, ServerCode, ScheduleOncePredicate)
    public void patchServerCodeTrigger_ServerCodeWithEndPointAndNullToken_ScheduleOncePredicate_Test() throws Exception {
        ScheduleOncePredicate predicate = new ScheduleOncePredicate(System.currentTimeMillis());
        TriggerOptions options = getDefaultOptions();
        ServerCode serverCode = new ServerCode("function_name", null);
        String triggerID = "trigger-1234";
        this.addEmptyMockResponse(204);
        this.addMockResponseForGetTriggerWithServerCode(200, triggerID, serverCode, predicate, options, false, null);

        Trigger trigger;
        trigger = this.defaultApi.patchServerCodeTrigger(triggerID, serverCode, predicate);
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(false, trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertNull(trigger.getCommand());
        assertSamePredicate(predicate, trigger.getPredicate());
        this.assertServerCode(serverCode, trigger.getServerCode());
        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertNotNull(this.defaultApi.getTarget());
        String thingID = this.defaultApi.getTarget().getTypedID().toString();
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/"+triggerID, request1.getPath());
        Assert.assertEquals("PATCH", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("serverCode", JsonUtil.serverCodeToJson(serverCode));
        expectedRequestBody.put("predicate", JsonUtil.predicateToJson(predicate));
        expectedRequestBody.put("triggersWhat","SERVER_CODE");
        this.assertRequestBody(expectedRequestBody, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/" + triggerID, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());

        Map<String, String> expectedRequestHeaders2 = new HashMap<>();
        expectedRequestHeaders2.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders2.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders2.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders2, request2);
    }

    @Test
    // call patchTrigger(String, null, StatePredicate)
    public void patchServerCodeTrigger_NullServerCode_StatePredicate_Test() throws Exception {
        StatePredicate predicate = new StatePredicate(new Condition(
                new EqualsClauseInTrigger(ALIAS1, "power", true)), TriggersWhen.CONDITION_CHANGED);        TriggerOptions options = getDefaultOptions();

        String triggerID = "trigger-1234";
        this.addEmptyMockResponse(204);
        this.addMockResponseForGetTriggerWithServerCode(
                200,
                triggerID,
                getDefaultServerCode(),
                predicate,
                options,
                false,
                null);

        Trigger trigger;
        trigger = this.defaultApi.patchServerCodeTrigger(triggerID, (ServerCode) null, predicate);
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(false, trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertNull(trigger.getCommand());
        assertSamePredicate(predicate, trigger.getPredicate());
        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertNotNull(this.defaultApi.getTarget());
        String thingID = this.defaultApi.getTarget().getTypedID().toString();
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/"+triggerID, request1.getPath());
        Assert.assertEquals("PATCH", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("predicate", JsonUtil.predicateToJson(predicate));
        expectedRequestBody.put("triggersWhat","SERVER_CODE");
        this.assertRequestBody(expectedRequestBody, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/" + triggerID, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());

        Map<String, String> expectedRequestHeaders2 = new HashMap<>();
        expectedRequestHeaders2.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders2.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders2.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders2, request2);
    }

    @Test
    // call patchTrigger(String, ServerCode, null)
    public void patchServerCodeTrigger_ServerCode_NullStatePredicate_Test() throws Exception {

        TriggerOptions options = getDefaultOptions();
        String triggerID = "trigger-1234";
        this.addEmptyMockResponse(204);
        this.addMockResponseForGetTriggerWithServerCode(
                200,
                triggerID,
                getDefaultServerCode(),
                getDefaultPredicate(),
                options,
                false,
                null);

        Trigger trigger;
        trigger = this.defaultApi.patchServerCodeTrigger(
                triggerID,
                getDefaultServerCode(),
                null);
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(false, trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertNull(trigger.getCommand());
        assertServerCode(getDefaultServerCode(), trigger.getServerCode());
        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertNotNull(this.defaultApi.getTarget());
        String thingID = this.defaultApi.getTarget().getTypedID().toString();
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/"+triggerID, request1.getPath());
        Assert.assertEquals("PATCH", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("triggersWhat","SERVER_CODE");
        expectedRequestBody.put("serverCode", JsonUtil.serverCodeToJson(getDefaultServerCode()));
        this.assertRequestBody(expectedRequestBody, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/" + triggerID, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());

        Map<String, String> expectedRequestHeaders2 = new HashMap<>();
        expectedRequestHeaders2.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders2.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders2.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders2, request2);
    }

    @Test(expected = IllegalStateException.class)
    // call patchTrigger(String, ServerCode, Predicate)
    public void patchServerCodeTrigger_WithNullTargetTest() throws Exception {
        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        api.patchServerCodeTrigger(
                "trigger-1234",
                getDefaultServerCode(),
                getDefaultPredicate());
    }

    @Test(expected = IllegalArgumentException.class)
    // call patchTrigger(String, null, null)
    public void patchServerCodeTrigger_NullServerCode_NullPredicate_Test() throws Exception{
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";

        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);
        api.patchServerCodeTrigger("trigger-1234", (ServerCode) null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    // call patchTrigger(null, ServerCode, Predicate)
    public void patchServerCodeTrigger_NullTriggerID_ServerCode_Predicate_Test() throws Exception{
        this.defaultApi.patchServerCodeTrigger(
                null,
                getDefaultServerCode(),
                getDefaultPredicate());
    }

    @Test(expected = IllegalArgumentException.class)
    // call patchTrigger("", ServerCode, Predicate)
    public void patchServerCodeTrigger_EmptyTriggerID_ServerCode_Predicate_Test() throws Exception{
        this.defaultApi.patchServerCodeTrigger(
                "",
                getDefaultServerCode(),
                getDefaultPredicate());
    }

    @Test
    // call patchTrigger(String, ServerCode, Predicate)
    public void patchServerCodeTrigger404ErrorTest2() throws Exception {
        ServerCode expectedServerCode = getDefaultServerCode();
        Predicate predicate = getDefaultPredicate();

        this.addEmptyMockResponse(404);

        try {
            this.defaultApi.patchServerCodeTrigger("trigger-1234", expectedServerCode, predicate);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (NotFoundException e) {
        }
        // verify the request
        Assert.assertNotNull(this.defaultApi.getTarget());
        String thingID = this.defaultApi.getTarget().getTypedID().toString();
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/trigger-1234", request.getPath());
        Assert.assertEquals("PATCH", request.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("serverCode", JsonUtil.serverCodeToJson(expectedServerCode));
        expectedRequestBody.put("predicate",JsonUtil.predicateToJson(predicate));
        expectedRequestBody.put("triggersWhat","SERVER_CODE");
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    // call patchTrigger(String, ServerCode, Predicate)
    public void patchServerCodeTrigger503ErrorTest2() throws Exception {
        ServerCode expectedServerCode = getDefaultServerCode();
        Predicate predicate = getDefaultPredicate();

        this.addEmptyMockResponse(503);

        try {
            this.defaultApi.patchServerCodeTrigger("trigger-1234", expectedServerCode, predicate);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (ServiceUnavailableException e) {
        }
        // verify the request
        Assert.assertNotNull(this.defaultApi.getTarget());
        String thingID = this.defaultApi.getTarget().getTypedID().toString();
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/trigger-1234", request.getPath());
        Assert.assertEquals("PATCH", request.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("serverCode", JsonUtil.serverCodeToJson(expectedServerCode));
        expectedRequestBody.put("predicate",JsonUtil.predicateToJson(predicate));
        expectedRequestBody.put("triggersWhat","SERVER_CODE");
        this.assertRequestBody(expectedRequestBody, request);
    }

    @Test
    //call patchTrigger(String, TriggeredCommandForm, StatePredicate)
    public void patchCommandTrigger_FormOnlyHasActions_StatePredicate_Test() throws Exception {

        List<AliasAction<? extends Action>> actions = getDefaultActions();
        TriggeredCommandForm form = TriggeredCommandForm.Builder.newBuilder(actions).build();
        StatePredicate predicate = new StatePredicate(new Condition(
                new EqualsClauseInTrigger(ALIAS1, "power", true)), TriggersWhen.CONDITION_CHANGED);
        TriggerOptions options = TriggerOptions.Builder.newBuilder()
                .setTitle("trigger title")
                .setDescription("trigger description")
                .setMetadata(new JSONObject().put("key1", "value1"))
                .build();

        String triggerID = "trigger-1234";

        Assert.assertNotNull(this.defaultApi.getTarget());
        Command expectedCommand = CommandFactory.newTriggeredCommand(
                this.defaultApi.getOwner().getTypedID(),
                actions,
                this.defaultApi.getTarget().getTypedID(),
                null,
                null,
                null);

        this.addEmptyMockResponse(204);
        this.addMockResponseForGetTriggerWithCommand(
                200,
                triggerID,
                expectedCommand,
                predicate,
                options,
                false,
                null);

        Trigger trigger = this.defaultApi.patchCommandTrigger(triggerID, form, predicate);
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(false, trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertNull(trigger.getServerCode());
        assertSamePredicate(predicate, trigger.getPredicate());
        assertSameCommands(expectedCommand, trigger.getCommand());
        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        String thingID = this.defaultApi.getTarget().getTypedID().toString();
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/"+ triggerID, request1.getPath());
        Assert.assertEquals("PATCH", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("command", JsonUtil.commandToJson(expectedCommand));
        expectedRequestBody.put("predicate", JsonUtil.predicateToJson(predicate));
        expectedRequestBody.put("triggersWhat","COMMAND");
        this.assertRequestBody(expectedRequestBody, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/" + triggerID, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());

        Map<String, String> expectedRequestHeaders2 = new HashMap<>();
        expectedRequestHeaders2.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders2.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders2.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders2, request2);
    }
    @Test
    // call patchTrigger(String, TriggeredCommandForm, ScheduleOncePredicate)
    public void patchCommandTrigger_FormHasActionsAndCommandOption_ScheduledOncePredicate_Test() throws Exception {
        List<AliasAction<? extends Action>> actions = getDefaultActions();
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

        String triggerID = "trigger-1234";

        Assert.assertNotNull(this.defaultApi.getTarget());
        Command expectedCommand = CommandFactory.newTriggeredCommand(
                this.defaultApi.getOwner().getTypedID(),
                actions,
                this.defaultApi.getTarget().getTypedID(),
                form.getTitle(),
                form.getDescription(),
                form.getMetadata());

        this.addEmptyMockResponse(204);
        this.addMockResponseForGetTriggerWithCommand(
                200,
                triggerID,
                expectedCommand,
                predicate,
                options,
                false,
                null);

        Trigger trigger = this.defaultApi.patchCommandTrigger(triggerID, form, predicate);
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(false, trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertNull(trigger.getServerCode());
        assertSamePredicate(predicate, trigger.getPredicate());
        assertSameCommands(expectedCommand, trigger.getCommand());
        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        String thingID = this.defaultApi.getTarget().getTypedID().toString();
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/"+ triggerID, request1.getPath());
        Assert.assertEquals("PATCH", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("command", JsonUtil.commandToJson(expectedCommand));
        expectedRequestBody.put("predicate", JsonUtil.predicateToJson(predicate));
        expectedRequestBody.put("triggersWhat","COMMAND");
        this.assertRequestBody(expectedRequestBody, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/" + triggerID, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());

        Map<String, String> expectedRequestHeaders2 = new HashMap<>();
        expectedRequestHeaders2.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders2.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders2.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders2, request2);
    }

    @Test
    // call patchTrigger(String, TriggeredCommandForm, SchedulePredicate)
    public void patchCommandTrigger_FormHasTarget_SchedulePredicate_Test() throws Exception {
        List<AliasAction<? extends Action>> actions = new ArrayList<>();
        TypedID targetID = new TypedID(TypedID.Types.THING, "another thing");
        actions.add(new AliasAction<Action>(
                ALIAS1,
                new AirConditionerActions(true, null)));
        TriggeredCommandForm form = TriggeredCommandForm.Builder.newBuilder(actions)
                .setTargetID(targetID)
                .build();
        SchedulePredicate predicate = new SchedulePredicate("1 * * * *");
        TriggerOptions options = TriggerOptions.Builder.newBuilder()
                .setTitle("trigger title")
                .setMetadata(new JSONObject().put("key1", "value1"))
                .build();

        String triggerID = "trigger-1234";

        Assert.assertNotNull(this.defaultApi.getTarget());
        Command expectedCommand = CommandFactory.newTriggeredCommand(
                this.defaultApi.getOwner().getTypedID(),
                actions,
                targetID,
                null,
                null,
                null);

        this.addEmptyMockResponse(204);
        this.addMockResponseForGetTriggerWithCommand(
                200,
                triggerID,
                expectedCommand,
                predicate,
                options,
                false,
                null);

        Trigger trigger = this.defaultApi.patchCommandTrigger(triggerID, form, predicate);
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(false, trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertNull(trigger.getServerCode());
        assertSamePredicate(predicate, trigger.getPredicate());
        assertSameCommands(expectedCommand, trigger.getCommand());
        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        String thingID = this.defaultApi.getTarget().getTypedID().toString();
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/"+ triggerID, request1.getPath());
        Assert.assertEquals("PATCH", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("command", JsonUtil.commandToJson(expectedCommand));
        expectedRequestBody.put("predicate", JsonUtil.predicateToJson(predicate));
        expectedRequestBody.put("triggersWhat","COMMAND");
        this.assertRequestBody(expectedRequestBody, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/" + triggerID, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());

        Map<String, String> expectedRequestHeaders2 = new HashMap<>();
        expectedRequestHeaders2.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders2.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders2.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders2, request2);
    }

    @Test
    // call patchTrigger(String, TriggeredCommandForm, null)
    public void patchCommandTrigger_Form_NullPredicate_Test() throws Exception {
        List<AliasAction<? extends Action>> actions = getDefaultActions();
        TriggeredCommandForm form = getDefaultForm();
        TriggerOptions options = TriggerOptions.Builder.newBuilder()
                .setTitle("cross thing trigger title")
                .setDescription("cross thing trigger description")
                .build();

        String triggerID = "trigger-1234";

        Assert.assertNotNull(this.defaultApi.getTarget());
        Command expectedCommand = CommandFactory.newTriggeredCommand(
                this.defaultApi.getOwner().getTypedID(),
                form.getAliasActions(),
                this.defaultApi.getTarget().getTypedID(),
                form.getTitle(),
                form.getDescription(),
                form.getMetadata());

        this.addEmptyMockResponse(204);
        this.addMockResponseForGetTriggerWithCommand(
                200,
                triggerID,
                expectedCommand,
                getDefaultPredicate(),
                options,
                false,
                null);

        Trigger trigger = this.defaultApi.patchCommandTrigger(triggerID, form, null);
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(false, trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertNull(trigger.getServerCode());
        assertSameCommands(expectedCommand, trigger.getCommand());
        assertSamePredicate(getDefaultPredicate(), trigger.getPredicate());
        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        String thingID = this.defaultApi.getTarget().getTypedID().toString();
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/"+ triggerID, request1.getPath());
        Assert.assertEquals("PATCH", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("command", JsonUtil.commandToJson(expectedCommand));
        expectedRequestBody.put("triggersWhat","COMMAND");
        this.assertRequestBody(expectedRequestBody, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/" + triggerID, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());

        Map<String, String> expectedRequestHeaders2 = new HashMap<>();
        expectedRequestHeaders2.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders2.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders2.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders2, request2);
    }

    @Test
    // call patchTrigger(String, null, SchedulePredicate)
    public void patchCommandTrigger_NullForm_SchedulePredicate_Test() throws Exception {
        SchedulePredicate predicate = new SchedulePredicate("1 * * * *");
        TriggerOptions options = getDefaultOptions();
        String triggerID = "trigger-1234";

        Assert.assertNotNull(this.defaultApi.getTarget());

        this.addEmptyMockResponse(204);
        this.addMockResponseForGetTriggerWithCommand(
                200,
                triggerID,
                getDefaultCommand(),
                getDefaultPredicate(),
                options,
                false,
                null);

        Trigger trigger = this.defaultApi.patchCommandTrigger(triggerID, null, predicate);
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(false, trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertNull(trigger.getServerCode());
        assertSameCommands(getDefaultCommand(), trigger.getCommand());
        assertSamePredicate(getDefaultPredicate(), trigger.getPredicate());
        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        String thingID = this.defaultApi.getTarget().getTypedID().toString();
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/"+ triggerID, request1.getPath());
        Assert.assertEquals("PATCH", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("triggersWhat","COMMAND");
        expectedRequestBody.putOpt("predicate", JsonUtil.predicateToJson(predicate));
        this.assertRequestBody(expectedRequestBody, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID + "/triggers/" + triggerID, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());

        Map<String, String> expectedRequestHeaders2 = new HashMap<>();
        expectedRequestHeaders2.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders2.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders2.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders2, request2);
    }

    @Test
    // call patchTrigger(String, TriggeredCommandForm, Predicate)
    public void patchCommandTrigger403ErrorTest2() throws Exception {
        Assert.assertNotNull(this.defaultApi.getTarget());
        TypedID thingID = this.defaultApi.getTarget().getTypedID();

        TriggeredCommandForm form = getDefaultForm();
        Predicate predicate = getDefaultPredicate();

        Command expectedCommand = getDefaultCommand();
        this.addEmptyMockResponse(403);

        try {
            this.defaultApi.patchCommandTrigger("trigger-1234", form, predicate);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (ForbiddenException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/trigger-1234", request.getPath());
        Assert.assertEquals("PATCH", request.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("command", JsonUtil.commandToJson(expectedCommand));
        expectedRequestBody.put("predicate",JsonUtil.predicateToJson(predicate));
        expectedRequestBody.put("triggersWhat","COMMAND");
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    // call patchTrigger(String, TriggeredCommandForm, Predicate)
    public void patchTrigger404ErrorTest2() throws Exception {
        Assert.assertNotNull(this.defaultApi.getTarget());
        TypedID thingID = this.defaultApi.getTarget().getTypedID();

        TriggeredCommandForm form = getDefaultForm();
        Predicate predicate = getDefaultPredicate();

        Command expectedCommand = getDefaultCommand();
        this.addEmptyMockResponse(404);

        try {
            this.defaultApi.patchCommandTrigger("trigger-1234", form, predicate);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (NotFoundException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/trigger-1234", request.getPath());
        Assert.assertEquals("PATCH", request.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("command", JsonUtil.commandToJson(expectedCommand));
        expectedRequestBody.put("predicate",JsonUtil.predicateToJson(predicate));
        expectedRequestBody.put("triggersWhat","COMMAND");
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    // call patchTrigger(String, TriggeredCommandForm, Predicate)
    public void patchTrigger503ErrorTest2() throws Exception {
        Assert.assertNotNull(this.defaultApi.getTarget());
        TypedID thingID = this.defaultApi.getTarget().getTypedID();

        TriggeredCommandForm form = getDefaultForm();
        Predicate predicate = getDefaultPredicate();

        Command expectedCommand = getDefaultCommand();
        this.addEmptyMockResponse(503);

        try {
            this.defaultApi.patchCommandTrigger("trigger-1234", form, predicate);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (ServiceUnavailableException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/trigger-1234", request.getPath());
        Assert.assertEquals("PATCH", request.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("command", JsonUtil.commandToJson(expectedCommand));
        expectedRequestBody.put("predicate",JsonUtil.predicateToJson(predicate));
        expectedRequestBody.put("triggersWhat","COMMAND");
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test(expected = IllegalStateException.class)
    // call patchTrigger(String, TriggeredCommandForm, Predicate)
    public void patchCommandTriggerWithNullTargetTest2() throws Exception {
        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        api.patchCommandTrigger("trigger-1234", getDefaultForm(), getDefaultPredicate());
    }

    @Test(expected = IllegalArgumentException.class)
    // call patchTrigger(String, null, null)
    public void patchCommandTrigger_NullForm_NullPredicate_Test() throws Exception{
        this.defaultApi.patchCommandTrigger("trigger-1234", null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    // call patchTrigger(null, TriggeredCommandForm, Predicate)
    public void patchCommandTrigger_NullTriggerID_Form_Predicate_Test() throws Exception{
        this.defaultApi.patchCommandTrigger(
                null,
                getDefaultForm(),
                getDefaultPredicate());
    }

    @Test(expected = IllegalArgumentException.class)
    // call patchTrigger("", TriggeredCommandForm, Predicate)
    public void patchCommandTrigger_EmptyTriggerID_Form_Predicate_Test() throws Exception{
        this.defaultApi.patchCommandTrigger(
                "",
                getDefaultForm(),
                getDefaultPredicate());
    }

}
