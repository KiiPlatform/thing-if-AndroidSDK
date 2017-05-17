package com.kii.thingiftrait.thingifapi;

import android.content.Context;

import com.kii.thingiftrait.StandaloneThing;
import com.kii.thingiftrait.Target;
import com.kii.thingiftrait.ThingIFAPI;
import com.kii.thingiftrait.ThingIFAPITestBase;
import com.kii.thingiftrait.TypedID;
import com.kii.thingiftrait.actions.SetPresetHumidity;
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
import com.kii.thingiftrait.trigger.ServerCode;
import com.kii.thingiftrait.trigger.StatePredicate;
import com.kii.thingiftrait.trigger.Trigger;
import com.kii.thingiftrait.trigger.TriggersWhen;
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
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RunWith(RobolectricTestRunner.class)
public class GetTriggerTest extends ThingIFAPITestBase{

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
    @Test
    public void getStateTriggerWithCommandTest() throws Exception {
        StatePredicate predicate =
                new StatePredicate(
                        new Condition(new EqualsClauseInTrigger(ALIAS1, "power", true)),
                        TriggersWhen.CONDITION_CHANGED);
        getTriggerWithCommandTest(predicate);
    }

    @Test
    public void getOneTimeTriggerWithCommandTest() throws Exception {
        ScheduleOncePredicate predicate = new ScheduleOncePredicate(1000);
        getTriggerWithCommandTest(predicate);
    }

    private void getTriggerWithCommandTest(Predicate predicate) throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);

        TypedID issuer = new TypedID(TypedID.Types.USER, "user1234");

        List<AliasAction> aliasActions = new ArrayList<>();
        List<Action> actions1 = new ArrayList<>();
        actions1.add(new TurnPower(true));
        aliasActions.add(new AliasAction(ALIAS1, actions1));
        List<Action> actions2 = new ArrayList<>();
        actions2.add(new SetPresetHumidity(45));
        aliasActions.add(new AliasAction(ALIAS2, actions2));
        String commandTitle = "command title";
        String commandDescription = "command description";
        JSONObject commandMetaData = new JSONObject().put("k", "v");
        Command expectedCommand = CommandFactory.newCommand(
                issuer,
                aliasActions,
                null,
                target.getTypedID(),
                null,
                null,
                null,
                null,
                null,
                commandTitle,
                commandDescription,
                commandMetaData);

        this.addMockResponseForGetTriggerWithCommand(
                200,
                triggerID,
                expectedCommand,
                predicate,
                null,
                true,
                "COMMAND_EXECUTION_REJECTED");

        ThingIFAPIUtils.setTarget(api, target);
        Trigger trigger = api.getTrigger(triggerID);
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(true, trigger.disabled());
        Assert.assertEquals("COMMAND_EXECUTION_REJECTED", trigger.getDisabledReason());
        Assert.assertNull(trigger.getServerCode());
        assertSamePredicate(predicate, trigger.getPredicate());
        assertSameCommands(expectedCommand, trigger.getCommand());
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID, request.getPath());
        Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }

    @Test
    public void getTriggerWithServerCodeTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        StatePredicate predicate = new StatePredicate(
                new Condition(new EqualsClauseInTrigger(ALIAS1, "power", true)), TriggersWhen.CONDITION_CHANGED);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);

        String endpoint = "function_name";
        String executorAccessToken = UUID.randomUUID().toString();
        String targetAppID = UUID.randomUUID().toString().substring(0, 8);
        JSONObject parameters = new JSONObject("{\"name\":\"kii\", \"age\":30, \"enabled\":true}");
        ServerCode expectedServerCode = new ServerCode(endpoint, executorAccessToken, targetAppID, parameters);
        this.addMockResponseForGetTriggerWithServerCode(
                200,
                triggerID,
                expectedServerCode,
                predicate,
                null,
                true,
                "COMMAND_EXECUTION_REJECTED");

        ThingIFAPIUtils.setTarget(api, target);
        Trigger trigger = api.getTrigger(triggerID);
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(true, trigger.disabled());
        Assert.assertEquals("COMMAND_EXECUTION_REJECTED", trigger.getDisabledReason());
        Assert.assertNull(trigger.getCommand());
        assertSamePredicate(predicate, trigger.getPredicate());
        assertServerCode(expectedServerCode, trigger.getServerCode());
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID, request.getPath());
        Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }

    @Test
    public void getTrigger403ErrorTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        this.addEmptyMockResponse(403);

        try {
            ThingIFAPIUtils.setTarget(api, target);
            api.getTrigger(triggerID);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (ForbiddenException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID, request.getPath());
        Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
    @Test
    public void getTrigger404ErrorTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        this.addEmptyMockResponse(404);

        try {
            ThingIFAPIUtils.setTarget(api, target);
            api.getTrigger(triggerID);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (NotFoundException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID, request.getPath());
        Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
    @Test
    public void getTrigger503ErrorTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        this.addEmptyMockResponse(503);

        try {
            ThingIFAPIUtils.setTarget(api, target);
            api.getTrigger(triggerID);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (ServiceUnavailableException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID, request.getPath());
        Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }

    @Test(expected = IllegalStateException.class)
    public void getTriggerWithNullTargetTest() throws Exception {
        String triggerID = "trigger-1234";

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        api.getTrigger(triggerID);
    }
    @Test(expected = IllegalArgumentException.class)
    public void getTriggerWithNullTriggerIDTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);
        api.getTrigger(null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void getTriggerWithEmptyTriggerIDTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);
        api.getTrigger("");
    }
}
