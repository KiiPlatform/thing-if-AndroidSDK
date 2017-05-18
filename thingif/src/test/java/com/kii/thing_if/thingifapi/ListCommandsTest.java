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
import com.kii.thing_if.command.Action;
import com.kii.thing_if.command.ActionResult;
import com.kii.thing_if.command.ActionResultFactory;
import com.kii.thing_if.command.AliasAction;
import com.kii.thing_if.command.AliasActionResult;
import com.kii.thing_if.command.AliasActionResultFactory;
import com.kii.thing_if.command.Command;
import com.kii.thing_if.command.CommandFactory;
import com.kii.thing_if.command.CommandState;
import com.kii.thing_if.exception.BadRequestException;
import com.kii.thing_if.exception.NotFoundException;
import com.kii.thing_if.thingifapi.utils.ThingIFAPIUtils;
import com.kii.thing_if.utils.JsonUtil;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.json.JSONArray;
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
public class ListCommandsTest extends ThingIFAPITestBase{

    private Context context;
    @Before
    public void before() throws Exception{
        this.context = RuntimeEnvironment.application.getApplicationContext();
        this.server = new MockWebServer();
        this.server.start();

    }
    @After
    public void after() throws Exception {
        this.server.shutdown();
    }

    @Test
    public void baseTest() throws Exception{
        Target target = new StandaloneThing("thing1", "vendor-thing-id", "dummy-token");
        TypedID issuerID = new TypedID(TypedID.Types.USER, "user1");
        TypedID targetID = target.getTypedID();
        String paginationKey = "1-2";

        // prepare command1
        Long created = System.currentTimeMillis();
        Long modified = System.currentTimeMillis();

        List<AliasAction> aliasActions = new ArrayList<>();

        List<Action> actions11 = new ArrayList<>();
        actions11.add(new TurnPower(true));
        actions11.add(new SetPresetTemperature(23));
        aliasActions.add(new AliasAction(ALIAS1, actions11));
        List<Action> actions12 = new ArrayList<>();
        actions12.add(new SetPresetHumidity(50));
        aliasActions.add(new AliasAction(ALIAS2, actions12));

        Command command1 = CommandFactory.newCommand(
                issuerID,
                aliasActions,
                "command1",
                targetID,
                null,
                CommandState.SENDING,
                null,
                created,
                modified,
                null,
                null,
                null);

        // prepare command2
        List<AliasAction> aliasActions2 = new ArrayList<>();
        List<Action> actions21 = new ArrayList<>();
        actions21.add(new TurnPower(false));
        aliasActions2.add(new AliasAction(ALIAS1, actions21));

        List<AliasActionResult> aliasActionResults2 = new ArrayList<>();
        List<ActionResult> actionResults2 = new ArrayList<>();
        actionResults2.add(ActionResultFactory.newActionResult(
                "turnPower",
                false,
                "invalid value",
                new JSONObject().put("k", "v")));

        aliasActionResults2.add(
                AliasActionResultFactory.newAliasActionResult(
                        ALIAS1,
                        actionResults2));

        JSONObject metadata = new JSONObject().put("k1", "v1");
        Command command2 = CommandFactory.newCommand(
                issuerID,
                aliasActions2,
                "command2",
                targetID,
                aliasActionResults2,
                CommandState.DONE,
                "trigger1",
                created,
                modified,
                "title",
                "description",
                metadata);

        // prepare command3
        TypedID issuer2 = new TypedID(TypedID.Types.USER, "user2");

        List<AliasAction> aliasActions3 = new ArrayList<>();
        List<Action> actions3 = new ArrayList<>();
        actions3.add(new SetPresetHumidity(60));
        aliasActions3.add(new AliasAction(ALIAS2, actions3));
        Command command3 = CommandFactory.newCommand(
                issuer2,
                aliasActions3,
                "command3",
                targetID,
                null,
                CommandState.SENDING,
                null,
                created,
                modified,
                null,
                null,
                null);
        this.addMockResponseForListCommands(
                200,
                new JSONArray()
                        .put(JsonUtil.commandToJson(command1))
                        .put(JsonUtil.commandToJson(command2)),
                paginationKey);
        this.addMockResponseForListCommands(
                200,
                new JSONArray().put(JsonUtil.commandToJson(command3)),
                null);


        ThingIFAPI api = createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);

        Pair<List<Command>, String> results1 = api.listCommands(2, null);
        // verify 1st result
        Assert.assertEquals(paginationKey, results1.second);
        Command actualCommand1 = results1.first.get(0);
        Assert.assertEquals("command1", actualCommand1.getCommandID());
        Assert.assertEquals("user:user1", actualCommand1.getIssuerID().toString());
        Assert.assertNotNull(actualCommand1.getTargetID());
        Assert.assertEquals("thing:thing1", actualCommand1.getTargetID().toString());
        Assert.assertEquals(created, actualCommand1.getCreated());
        Assert.assertEquals(modified, actualCommand1.getModified());
        Assert.assertNull(actualCommand1.getAliasActionResults());
        Assert.assertNull(actualCommand1.getFiredByTriggerID());
        Assert.assertNull(actualCommand1.getTitle());
        Assert.assertNull(actualCommand1.getDescription());
        Assert.assertNull(actualCommand1.getMetadata());
        Assert.assertNotNull(actualCommand1.getCommandState());
        Assert.assertEquals("SENDING", actualCommand1.getCommandState().name());

        Assert.assertEquals(2, actualCommand1.getAliasActions().size());
        assertJSONObject(
                JsonUtil.aliasActionToJson(aliasActions.get(0)),
                JsonUtil.aliasActionToJson(actualCommand1.getAliasActions().get(0)));
        assertJSONObject(
                JsonUtil.aliasActionToJson(aliasActions.get(1)),
                JsonUtil.aliasActionToJson(actualCommand1.getAliasActions().get(1)));

        Command actualCommand2 = results1.first.get(1);
        Assert.assertEquals("command2", actualCommand2.getCommandID());
        Assert.assertEquals("user:user1", actualCommand2.getIssuerID().toString());
        Assert.assertNotNull(actualCommand2.getTargetID());
        Assert.assertEquals("thing:thing1", actualCommand2.getTargetID().toString());
        Assert.assertEquals("trigger1", actualCommand2.getFiredByTriggerID());
        Assert.assertEquals("title", actualCommand2.getTitle());
        Assert.assertEquals("description", actualCommand2.getDescription());
        Assert.assertNotNull(actualCommand2.getMetadata());
        Assert.assertEquals(metadata.toString(), actualCommand2.getMetadata().toString());
        Assert.assertEquals(created, actualCommand2.getCreated());
        Assert.assertEquals(modified, actualCommand2.getModified());
        Assert.assertNotNull(actualCommand2.getCommandState());
        Assert.assertEquals("DONE", actualCommand2.getCommandState().name());

        Assert.assertEquals(1, actualCommand2.getAliasActions().size());
        assertJSONObject(
                JsonUtil.aliasActionToJson(aliasActions2.get(0)),
                JsonUtil.aliasActionToJson(actualCommand2.getAliasActions().get(0)));

        Assert.assertNotNull(actualCommand2.getAliasActionResults());
        Assert.assertEquals(1, actualCommand2.getAliasActionResults().size());
        AliasActionResult actualResult = actualCommand2.getAliasActionResults().get(0);
        Assert.assertEquals(ALIAS1, actualResult.getAlias());
        Assert.assertEquals(1, actualResult.getResults().size());
        Assert.assertEquals("turnPower", actualResult.getResults().get(0).getActionName());
        Assert.assertEquals(false, actualResult.getResults().get(0).isSucceeded());
        Assert.assertEquals("invalid value", actualResult.getResults().get(0).getErrorMessage());
        Assert.assertNotNull(actualResult.getResults().get(0).getData());
        Assert.assertEquals(
                new JSONObject().put("k", "v").toString(),
                actualResult.getResults().get(0).getData().toString());

        Pair<List<Command>, String> results2 = api.listCommands(2, results1.second);
        //verify 2nd result
        Assert.assertNull(results2.second);
        Command actualCommand3 = results2.first.get(0);
        Assert.assertEquals("command3", actualCommand3.getCommandID());
        Assert.assertEquals("user:user2", actualCommand3.getIssuerID().toString());
        Assert.assertNotNull(actualCommand3.getTargetID());
        Assert.assertEquals("thing:thing1", actualCommand3.getTargetID().toString());
        Assert.assertEquals(created, actualCommand3.getCreated());
        Assert.assertEquals(modified, actualCommand3.getModified());
        Assert.assertNull(actualCommand3.getAliasActionResults());
        Assert.assertNull(actualCommand3.getFiredByTriggerID());
        Assert.assertNull(actualCommand3.getTitle());
        Assert.assertNull(actualCommand3.getDescription());
        Assert.assertNull(actualCommand3.getMetadata());
        Assert.assertNotNull(actualCommand3.getCommandState());
        Assert.assertEquals("SENDING", actualCommand3.getCommandState().name());

        Assert.assertEquals(1, actualCommand3.getAliasActions().size());
        assertJSONObject(
                JsonUtil.aliasActionToJson(aliasActions3.get(0)),
                JsonUtil.aliasActionToJson(actualCommand3.getAliasActions().get(0)));

        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + targetID.toString() + "/commands?bestEffortLimit=2", request1.getPath());
        Assert.assertEquals("GET", request1.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + targetID.toString() + "/commands?bestEffortLimit=2&paginationKey=" + paginationKey, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());
        this.assertRequestHeader(expectedRequestHeaders, request2);
    }

    @Test
    public void listCommandsWithBestEffortLimitZeroTest() throws Exception {

        Target target = new StandaloneThing("thing1", "vendor-thing-id", "dummy-token");
        TypedID issuerID = new TypedID(TypedID.Types.USER, "user1");
        TypedID targetID = target.getTypedID();

        // prepare command1
        Long created = System.currentTimeMillis();
        Long modified = System.currentTimeMillis();

        List<AliasAction> aliasActions = new ArrayList<>();

        List<Action> actions1 = new ArrayList<>();
        actions1.add(new TurnPower(true));
        actions1.add(new SetPresetTemperature(23));
        aliasActions.add(new AliasAction(ALIAS1, actions1));
        List<Action> actions2 = new ArrayList<>();
        actions2.add(new SetPresetHumidity(50));
        aliasActions.add(new AliasAction(ALIAS2, actions2));

        Command command1 = CommandFactory.newCommand(
                issuerID,
                aliasActions,
                "command1",
                targetID,
                null,
                CommandState.SENDING,
                null,
                created,
                modified,
                null,
                null,
                null);

        this.addMockResponseForListCommands(
                200,
                new JSONArray().put(JsonUtil.commandToJson(command1)),
                null);

        ThingIFAPI api = createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);

        Pair<List<Command>, String> results1 = api.listCommands(0, null);
        // verify 1st result
        Assert.assertEquals(null, results1.second);
        Command actualCommand1 = results1.first.get(0);
        Assert.assertEquals("command1", actualCommand1.getCommandID());
        Assert.assertEquals("user:user1", actualCommand1.getIssuerID().toString());
        Assert.assertNotNull(actualCommand1.getTargetID());
        Assert.assertEquals("thing:thing1", actualCommand1.getTargetID().toString());
        Assert.assertEquals(created, actualCommand1.getCreated());
        Assert.assertEquals(modified, actualCommand1.getModified());
        Assert.assertNull(actualCommand1.getAliasActionResults());
        Assert.assertNull(actualCommand1.getFiredByTriggerID());
        Assert.assertNull(actualCommand1.getTitle());
        Assert.assertNull(actualCommand1.getDescription());
        Assert.assertNull(actualCommand1.getMetadata());
        Assert.assertNotNull(actualCommand1.getCommandState());
        Assert.assertEquals("SENDING", actualCommand1.getCommandState().name());

        Assert.assertEquals(2, actualCommand1.getAliasActions().size());
        assertJSONObject(
                JsonUtil.aliasActionToJson(aliasActions.get(0)),
                JsonUtil.aliasActionToJson(actualCommand1.getAliasActions().get(0)));
        assertJSONObject(
                JsonUtil.aliasActionToJson(aliasActions.get(1)),
                JsonUtil.aliasActionToJson(actualCommand1.getAliasActions().get(1)));

        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + targetID.toString() + "/commands", request.getPath());
        Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }


    @Test
    public void listCommands400ErrorTest() throws Exception {
        Target target = new StandaloneThing("thing1", "vendor-thing-id", "dummy-token");
        TypedID targetID = target.getTypedID();

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);

        this.addEmptyMockResponse(400);

        try {
            ThingIFAPIUtils.setTarget(api, target);
            api.listCommands(10, null);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (BadRequestException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + targetID.toString() + "/commands?bestEffortLimit=10", request.getPath());
        Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
    @Test
    public void listCommands404ErrorTest() throws Exception {
        Target target = new StandaloneThing("thing1", "vendor-thing-id", "dummy-token");
        TypedID targetID = target.getTypedID();

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);

        this.addEmptyMockResponse(404);

        try {
            ThingIFAPIUtils.setTarget(api, target);
            api.listCommands(10, null);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (NotFoundException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + targetID.toString() + "/commands?bestEffortLimit=10", request.getPath());
        Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
    @Test(expected = IllegalStateException.class)
    public void listCommandsWithNullTargetTest() throws Exception {
        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        api.listCommands(10, null);
    }

}
