package com.kii.thing_if.thingifapi;

import android.content.Context;
import android.util.Pair;

import com.kii.thing_if.StandaloneThing;
import com.kii.thing_if.Target;
import com.kii.thing_if.ThingIFAPI;
import com.kii.thing_if.ThingIFAPITestBase;
import com.kii.thing_if.TypedID;
import com.kii.thing_if.actions.SetPresetHumidity;
import com.kii.thing_if.actions.SetPresetTemperature;
import com.kii.thing_if.actions.TurnPower;
import com.kii.thing_if.clause.trigger.EqualsClauseInTrigger;
import com.kii.thing_if.command.Action;
import com.kii.thing_if.command.AliasAction;
import com.kii.thing_if.command.Command;
import com.kii.thing_if.command.CommandFactory;
import com.kii.thing_if.exception.BadRequestException;
import com.kii.thing_if.exception.ForbiddenException;
import com.kii.thing_if.exception.NotFoundException;
import com.kii.thing_if.exception.ServiceUnavailableException;
import com.kii.thing_if.thingifapi.utils.ThingIFAPIUtils;
import com.kii.thing_if.trigger.Condition;
import com.kii.thing_if.trigger.ScheduleOncePredicate;
import com.kii.thing_if.trigger.SchedulePredicate;
import com.kii.thing_if.trigger.ServerCode;
import com.kii.thing_if.trigger.StatePredicate;
import com.kii.thing_if.trigger.Trigger;
import com.kii.thing_if.trigger.TriggerFactory;
import com.kii.thing_if.trigger.TriggersWhen;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.After;
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
public class ListTriggersTest extends ThingIFAPITestBase{
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
    public void listTriggerBaseTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        TypedID issuer = new TypedID(TypedID.Types.USER, "user1234");
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        // construct command1
        List<AliasAction> aliasActions = new ArrayList<>();
        List<Action> actions11 = new ArrayList<>();
        actions11.add(new TurnPower(true));
        List<Action> actions12 = new ArrayList<>();
        actions12.add(new SetPresetHumidity(45));
        aliasActions.add(new AliasAction(ALIAS1, actions11));
        aliasActions.add(new AliasAction(ALIAS2, actions12));
        Command command1 = CommandFactory.newCommand(
                issuer,
                aliasActions,
                null,
                target.getTypedID(),
                null,
                null,
                null,
                null,
                null,
                "title1",
                "description1",
                new JSONObject().put("k", "v"));

        // construct command2
        List<AliasAction> aliasActions2 = new ArrayList<>();
        List<Action> actions21 = new ArrayList<>();
        actions21.add(new TurnPower(true));
        actions21.add(new SetPresetTemperature(23));
        aliasActions2.add(new AliasAction(ALIAS1, actions21));
        Command command2 = CommandFactory.newCommand(
                issuer,
                aliasActions2,
                null,
                target.getTypedID(),
                null,
                null,
                null,
                null,
                null,
                "title2",
                "description2",
                null);

        StatePredicate statePredicate =
                new StatePredicate(
                        new Condition(new EqualsClauseInTrigger(ALIAS1, "power", true)),
                        TriggersWhen.CONDITION_CHANGED);
        ScheduleOncePredicate scheduleOncePredicate = new ScheduleOncePredicate(1000);
        SchedulePredicate schedulePredicate = new SchedulePredicate("1 * * * *");

        ServerCode serverCode1 = new ServerCode("endpoint", "token", "targetAppID", new JSONObject().put("k1", "v1"));
        ServerCode serverCode2 = new ServerCode("endpoint1", null);
        Trigger commandTrigger1 = TriggerFactory
                .createTrigger(
                        "trigger1",
                        target.getTypedID(),
                        statePredicate,
                        command1,
                        null,
                        false,
                        null,
                        "title1",
                        "description1",
                        new JSONObject().put("k2", "v2"));
        Trigger commandTrigger2 = TriggerFactory
                .createTrigger(
                        "trigger2",
                        target.getTypedID(),
                        scheduleOncePredicate,
                        command2,
                        null,
                        false,
                        null,
                        null,
                        null,
                        null);
        Trigger commandTrigger3 = TriggerFactory
                .createTrigger(
                        "trigger3",
                        target.getTypedID(),
                        schedulePredicate,
                        command1,
                        null,
                        true,
                        "COMMAND_EXECUTION_REJECTED",
                        null,
                        null,
                        null);
        Trigger serverCodeTrigger4 = TriggerFactory
                .createTrigger("trigger4",
                        target.getTypedID(),
                        statePredicate,
                        null,
                        serverCode1,
                        false,
                        null,
                        "trigger title2",
                        "trigger description2",
                        new JSONObject().put("key1", "value1"));
        Trigger serverCodeTrigger5 = TriggerFactory
                .createTrigger("trigger5",
                        target.getTypedID(),
                        schedulePredicate,
                        null,
                        serverCode2,
                        true,
                        "SERVER_CODE_EXECUTION_REJECTED",
                        null,
                        null,
                        null);
        Trigger serverCodeTrigger6 = TriggerFactory
                .createTrigger(
                        "trigger6",
                        target.getTypedID(),
                        scheduleOncePredicate,
                        null,
                        serverCode1,
                        false,
                        null,
                        null,
                        null,
                        null);

        String paginationKey = "pagination-12345-key";
        this.addMockResponseForListTriggers(
                200,
                new Trigger[]{commandTrigger1, commandTrigger2, commandTrigger3},
                paginationKey);
        this.addMockResponseForListTriggers(
                200,
                new Trigger[]{serverCodeTrigger4, serverCodeTrigger5, serverCodeTrigger6},
                null);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);

        // verify result
        Pair<List<Trigger>, String> result1 = api.listTriggers(3, null);
        Assert.assertEquals(paginationKey, result1.second);
        Assert.assertEquals(3, result1.first.size());
        assertSameTrigger(commandTrigger1, result1.first.get(0));
        assertSameTrigger(commandTrigger2, result1.first.get(1));
        assertSameTrigger(commandTrigger3, result1.first.get(2));

        Pair<List<Trigger>, String> result2 = api.listTriggers(3, paginationKey);
        Assert.assertEquals(3, result2.first.size());
        assertSameTrigger(serverCodeTrigger4, result2.first.get(0));
        assertSameTrigger(serverCodeTrigger5, result2.first.get(1));
        assertSameTrigger(serverCodeTrigger6, result2.first.get(2));

        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers?bestEffortLimit=3", request1.getPath());
        Assert.assertEquals("GET", request1.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers?bestEffortLimit=3&paginationKey=" + paginationKey, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());
        this.assertRequestHeader(expectedRequestHeaders, request2);
    }
    
    @Test
    public void listTriggersWithBestEffortLimitZeroTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        TypedID issuer = new TypedID(TypedID.Types.USER, "user1234");
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        // construct command1
        List<AliasAction> aliasActions = new ArrayList<>();
        List<Action> actions1 = new ArrayList<>();
        actions1.add(new TurnPower(true));
        aliasActions.add(new AliasAction(ALIAS1, actions1));
        List<Action> actions2 = new ArrayList<>();
        actions2.add(new SetPresetHumidity(45));
        aliasActions.add(new AliasAction(ALIAS2, actions2));
        Command command1 = CommandFactory.newCommand(
                issuer,
                aliasActions,
                null,
                target.getTypedID(),
                null,
                null,
                null,
                null,
                null,
                "title1",
                "description1",
                new JSONObject().put("k", "v"));

        StatePredicate statePredicate =
                new StatePredicate(
                        new Condition(new EqualsClauseInTrigger(ALIAS1, "power", true)),
                        TriggersWhen.CONDITION_CHANGED);
        SchedulePredicate schedulePredicate = new SchedulePredicate("1 * * * *");

        ServerCode serverCode1 = new ServerCode("endpoint", "token", "targetAppID", new JSONObject().put("k1", "v1"));
        ServerCode serverCode2 = new ServerCode("endpoint1", null);
        Trigger trigger1 = TriggerFactory
                .createTrigger(
                        "trigger1",
                        target.getTypedID(),
                        statePredicate,
                        command1,
                        null,
                        false,
                        null,
                        "title1",
                        "description1",
                        new JSONObject().put("k2", "v2"));

        Trigger trigger2 = TriggerFactory
                .createTrigger("trigger4",
                        target.getTypedID(),
                        statePredicate,
                        null,
                        serverCode1,
                        false,
                        null,
                        "trigger title2",
                        "trigger description2",
                        new JSONObject().put("key1", "value1"));
        Trigger trigger3 = TriggerFactory
                .createTrigger("trigger5",
                        target.getTypedID(),
                        schedulePredicate,
                        null,
                        serverCode2,
                        true,
                        "SERVER_CODE_EXECUTION_REJECTED",
                        null,
                        null,
                        null);

        String paginationKey = "pagination-12345-key";
        this.addMockResponseForListTriggers(
                200,
                new Trigger[]{trigger1, trigger2},
                paginationKey);
        this.addMockResponseForListTriggers(
                200,
                new Trigger[]{trigger3},
                null);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);

        // verify result
        Pair<List<Trigger>, String> result1 = api.listTriggers(0, null);
        Assert.assertEquals(paginationKey, result1.second);
        Assert.assertEquals(2, result1.first.size());
        assertSameTrigger(trigger1, result1.first.get(0));
        assertSameTrigger(trigger2, result1.first.get(1));

        Pair<List<Trigger>, String> result2 = api.listTriggers(0, paginationKey);
        Assert.assertEquals(1, result2.first.size());
        assertSameTrigger(trigger3, result2.first.get(0));

        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers", request1.getPath());
        Assert.assertEquals("GET", request1.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers?paginationKey=" + paginationKey, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());
        this.assertRequestHeader(expectedRequestHeaders, request2);
    }

    @Test
    public void listTriggers400ErrorTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        this.addEmptyMockResponse(400);
        try {
            ThingIFAPIUtils.setTarget(api, target);
            api.listTriggers(10, null);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (BadRequestException e) {
        }
        // verify the request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers?bestEffortLimit=10", request1.getPath());
        Assert.assertEquals("GET", request1.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request1);
    }
    @Test
    public void listTriggers403ErrorTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        this.addEmptyMockResponse(403);
        try {
            ThingIFAPIUtils.setTarget(api, target);
            api.listTriggers(10, null);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (ForbiddenException e) {
        }
        // verify the request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers?bestEffortLimit=10", request1.getPath());
        Assert.assertEquals("GET", request1.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request1);
    }
    @Test
    public void listTriggers404ErrorTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        this.addEmptyMockResponse(404);
        try {
            ThingIFAPIUtils.setTarget(api, target);
            api.listTriggers(10, null);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (NotFoundException e) {
        }
        // verify the request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers?bestEffortLimit=10", request1.getPath());
        Assert.assertEquals("GET", request1.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request1);
    }
    @Test
    public void listTriggers503ErrorTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        this.addEmptyMockResponse(503);
        try {
            ThingIFAPIUtils.setTarget(api, target);
            api.listTriggers(10, null);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (ServiceUnavailableException e) {
        }
        // verify the request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers?bestEffortLimit=10", request1.getPath());
        Assert.assertEquals("GET", request1.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request1);
    }
    @Test(expected = IllegalStateException.class)
    public void listTriggersWithNullTargetTest() throws Exception {
        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        api.listTriggers(10, null);
    }
}
