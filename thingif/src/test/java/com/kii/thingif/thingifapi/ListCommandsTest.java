package com.kii.thingif.thingifapi;

import android.content.Context;
import android.util.Pair;

import com.kii.thingif.StandaloneThing;
import com.kii.thingif.Target;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.ThingIFAPITestBase;
import com.kii.thingif.TypedID;
import com.kii.thingif.actions.AirConditionerActions;
import com.kii.thingif.actions.HumidityActions;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.AliasActionResult;
import com.kii.thingif.command.Command;
import com.kii.thingif.command.CommandState;
import com.kii.thingif.exception.BadRequestException;
import com.kii.thingif.exception.NotFoundException;
import com.kii.thingif.thingifapi.utils.ThingIFAPIUtils;
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
        JSONArray actions1 = new JSONArray()
                .put(new JSONObject().put(ALIAS1, new JSONArray()
                        .put(new JSONObject().put("turnPower", true))
                        .put(new JSONObject().put("setPresetTemperature", 100))))
                .put(new JSONObject().put(ALIAS2, new JSONArray()
                        .put(new JSONObject().put("setPresetHumidity", 50))));

        JSONObject command1 = createCommandJson(
                "command1",
                issuerID,
                targetID,
                actions1,
                null,
                CommandState.SENDING,
                null,
                created,
                modified,
                null,
                null,
                null);

        // prepare command2
        JSONArray actions2 = new JSONArray()
                .put(new JSONObject().put(ALIAS1, new JSONArray()
                        .put(new JSONObject().put("turnPower", false))));
        JSONArray actionResults2 = new JSONArray()
                .put(new JSONObject().put(ALIAS1, new JSONArray()
                        .put(new JSONObject()
                                .put("turnPower",
                                        new JSONObject()
                                                .put("succeeded", false)
                                                .put("errorMessage", "invalid value")
                                                .put("data", new JSONObject().put("k", "v"))))));
        JSONObject metadata = new JSONObject().put("k1", "v1");
        JSONObject command2 = createCommandJson(
                "command2",
                issuerID,
                targetID,
                actions2,
                actionResults2,
                CommandState.DONE,
                "trigger1",
                created,
                modified,
                "title",
                "description",
                metadata);

        // prepare command3
        TypedID issuer2 = new TypedID(TypedID.Types.USER, "user2");
        JSONArray actions3 = new JSONArray()
                .put(new JSONObject().put(ALIAS2, new JSONArray()
                        .put(new JSONObject().put("setPresetHumidity", 60))));
        JSONObject command3 = createCommandJson(
                "command3",
                issuer2,
                targetID,
                actions3,
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
                new JSONArray().put(command1).put(command2),
                paginationKey);
        this.addMockResponseForListCommands(
                200,
                new JSONArray().put(command3),
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
        Assert.assertEquals(ALIAS1, actualCommand1.getAliasActions().get(0).getAlias());
        Action actualAction1 = actualCommand1.getAliasActions().get(0).getAction();
        Assert.assertTrue(actualAction1 instanceof AirConditionerActions);
        Assert.assertEquals(true, ((AirConditionerActions)actualAction1).isPower());
        Assert.assertEquals(
                100,
                ((AirConditionerActions)actualAction1).getPresetTemperature().intValue());
        Assert.assertEquals(ALIAS2, actualCommand1.getAliasActions().get(1).getAlias());
        Action actualAction2 = actualCommand1.getAliasActions().get(1).getAction();
        Assert.assertTrue(actualAction2 instanceof HumidityActions);
        Assert.assertEquals(50, ((HumidityActions)actualAction2).getPresetHumidity().intValue());

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
        Assert.assertEquals(ALIAS1, actualCommand2.getAliasActions().get(0).getAlias());
        Action actualAction3 = actualCommand2.getAliasActions().get(0).getAction();
        Assert.assertTrue(actualAction3 instanceof AirConditionerActions);
        Assert.assertEquals(false, ((AirConditionerActions)actualAction3).isPower());
        Assert.assertNull(((AirConditionerActions)actualAction3).getPresetTemperature());

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
        Assert.assertEquals(ALIAS2, actualCommand3.getAliasActions().get(0).getAlias());
        Action actualAction4 = actualCommand3.getAliasActions().get(0).getAction();
        Assert.assertTrue(actualAction4 instanceof HumidityActions);
        Assert.assertEquals(60, ((HumidityActions)actualAction4).getPresetHumidity().intValue());

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
        JSONArray actions1 = new JSONArray()
                .put(new JSONObject().put(ALIAS1, new JSONArray()
                        .put(new JSONObject().put("turnPower", true))
                        .put(new JSONObject().put("setPresetTemperature", 100))))
                .put(new JSONObject().put(ALIAS2, new JSONArray()
                        .put(new JSONObject().put("setPresetHumidity", 50))));

        JSONObject command1 = createCommandJson(
                "command1",
                issuerID,
                targetID,
                actions1,
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
                new JSONArray().put(command1),
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
        Assert.assertEquals(ALIAS1, actualCommand1.getAliasActions().get(0).getAlias());
        Action actualAction1 = actualCommand1.getAliasActions().get(0).getAction();
        Assert.assertTrue(actualAction1 instanceof AirConditionerActions);
        Assert.assertEquals(true, ((AirConditionerActions)actualAction1).isPower());
        Assert.assertEquals(
                100,
                ((AirConditionerActions)actualAction1).getPresetTemperature().intValue());
        Assert.assertEquals(ALIAS2, actualCommand1.getAliasActions().get(1).getAlias());
        Action actualAction2 = actualCommand1.getAliasActions().get(1).getAction();
        Assert.assertTrue(actualAction2 instanceof HumidityActions);
        Assert.assertEquals(50, ((HumidityActions)actualAction2).getPresetHumidity().intValue());

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

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
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
