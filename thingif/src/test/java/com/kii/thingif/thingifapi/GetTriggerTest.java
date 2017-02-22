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
import com.kii.thingif.thingifapi.utils.ThingIFAPIUtils;
import com.kii.thingif.trigger.Condition;
import com.kii.thingif.trigger.Predicate;
import com.kii.thingif.trigger.ScheduleOncePredicate;
import com.kii.thingif.trigger.StatePredicate;
import com.kii.thingif.trigger.Trigger;
import com.kii.thingif.trigger.TriggersWhen;
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

        List<AliasAction<? extends Action>> actions = new ArrayList<>();
        actions.add(new AliasAction<Action>(
                ALIAS1,
                new AirConditionerActions(true, null)));
        actions.add(new AliasAction<Action>(
                ALIAS2,
                new HumidityActions(45)));
        String commandTitle = "command title";
        String commandDescription = "command description";
        JSONObject commandMetaData = new JSONObject().put("k", "v");
        Command expectedCommand = CommandFactory.newCommand(
                issuer,
                actions,
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

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
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
                new Condition(new EqualsClauseInTrigger("power", true)), TriggersWhen.CONDITION_CHANGED);

        ThingIFAPI api = this.createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);

        String endpoint = "function_name";
        String executorAccessToken = UUID.randomUUID().toString();
        String targetAppID = UUID.randomUUID().toString().substring(0, 8);
        JSONObject parameters = new JSONObject("{\"name\":\"kii\", \"age\":30, \"enabled\":true}");
        ServerCode expectedServerCode = new ServerCode(endpoint, executorAccessToken, targetAppID, parameters);
        this.addMockResponseForGetTriggerWithServerCode(200, triggerID, expectedServerCode, predicate, true, "COMMAND_EXECUTION_REJECTED", schema);

        ThingIFAPIUtils.setTarget(api, target);
        Trigger trigger = api.getTrigger(triggerID);
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(true, trigger.disabled());
        Assert.assertEquals("COMMAND_EXECUTION_REJECTED", trigger.getDisabledReason());
        Assert.assertNull(trigger.getCommand());
        this.assertPredicate(predicate, trigger.getPredicate());
        this.assertServerCode(expectedServerCode, trigger.getServerCode());
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
}
