package com.kii.thingif.thingifapi;

import android.content.Context;

import com.kii.thingif.KiiApp;
import com.kii.thingif.Owner;
import com.kii.thingif.StandaloneThing;
import com.kii.thingif.Target;
import com.kii.thingif.TargetState;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.ThingIFAPITestBase;
import com.kii.thingif.TypedID;
import com.kii.thingif.actions.AirConditionerActions;
import com.kii.thingif.actions.HumidityActions;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.Command;
import com.kii.thingif.command.CommandState;
import com.kii.thingif.exception.ForbiddenException;
import com.kii.thingif.exception.NotFoundException;
import com.kii.thingif.exception.ServiceUnavailableException;
import com.kii.thingif.exception.UnregisteredAliasException;
import com.kii.thingif.states.AirConditionerState;
import com.kii.thingif.states.HumidityState;
import com.kii.thingif.thingifapi.utils.ThingIFAPIUtils;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RunWith(RobolectricTestRunner.class)
public class GetCommandTest  extends ThingIFAPITestBase {
    private ThingIFAPI api;
    private final String alias1 = "AirConditionerAlias";
    private final String alias2 = "HumidityAlias";

    @Before
    public void before() throws Exception{
        Context context = RuntimeEnvironment.application.getApplicationContext();
        this.server = new MockWebServer();
        this.server.start();

        Map<String, Class<? extends Action>> actionTypes = new HashMap<>();
        actionTypes.put(alias1, AirConditionerActions.class);
        actionTypes.put(alias2, HumidityActions.class);

        Map<String, Class<? extends TargetState>> stateTypes = new HashMap<>();
        stateTypes.put(alias1, AirConditionerState.class);
        stateTypes.put(alias2, HumidityState.class);

        String ownerID = UUID.randomUUID().toString();
        Owner owner = new Owner(new TypedID(TypedID.Types.USER, ownerID), "owner-access-token-1234");
        KiiApp app = getApp(APP_ID, APP_KEY);
        ThingIFAPI.Builder builder = ThingIFAPI.Builder.newBuilder(
                context,
                app,
                owner,
                actionTypes,
                stateTypes);
        this.api = builder.build();

    }
    @After
    public void after() throws Exception {
        this.server.shutdown();
    }
    @Test
    public void baseTest() throws Exception{
        String commandID = "command-1234";
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);
        String commandTitle = "title";
        String commandDescription = "description";
        JSONObject metaData = new JSONObject().put("k", "v");
        String firedTriggerID = "trigger1";
        CommandState state = CommandState.SENDING;
        Long created = System.currentTimeMillis();
        Long modified = System.currentTimeMillis();
        
        // prepare the get command response
        JSONArray actionsInResponse = new JSONArray();
        actionsInResponse
                .put(new JSONObject().put(
                        alias1,
                        new JSONArray()
                                .put(new JSONObject().put("turnPower", true))
                                .put(new JSONObject().put("setPresetTemperature", 100))))
                .put(new JSONObject().put(
                        alias2,
                        new JSONArray()
                                .put(new JSONObject().put("setPresetHumidity", 50))));
        JSONArray resultsInResponse = new JSONArray();
        resultsInResponse
                .put(new JSONObject().put(
                        alias1,
                        new JSONArray()
                                .put(new JSONObject().put("turnPower",
                                        new JSONObject().put("succeeded", true)))
                                .put(new JSONObject().put("setPresetTemperature",
                                        new JSONObject()
                                                .put("succeeded", false)
                                                .put("errorMessage", "invalid value")
                                                .put("data", new JSONObject().put("k", "v"))))))
                .put(new JSONObject().put(
                        alias2,
                        new JSONArray()
                                .put(new JSONObject().put("setPresetHumidity",
                                        new JSONObject().put("succeeded", true)))));


        this.addMockResponseForGetCommand(
                200,
                commandID,
                api.getOwner().getTypedID(),
                target.getTypedID(),
                actionsInResponse,
                resultsInResponse,
                state,
                created,
                modified,
                firedTriggerID,
                commandTitle,
                commandDescription,
                metaData);

        ThingIFAPIUtils.setTarget(api, target);
        Command cmd = api.getCommand(commandID);

        // verify result
        Assert.assertEquals(commandID, cmd.getCommandID());
        Assert.assertEquals(
                api.getOwner().getTypedID().toString(),
                cmd.getIssuerID().toString());
        Assert.assertNotNull(cmd.getTargetID());
        Assert.assertEquals(thingID.toString(), cmd.getTargetID().toString());
        Assert.assertNotNull(cmd.getCreated());
        Assert.assertEquals(created.longValue(), cmd.getCreated().longValue());
        Assert.assertNotNull(cmd.getModified());
        Assert.assertEquals(modified.longValue(), cmd.getModified().longValue());
        Assert.assertNotNull(cmd.getCommandState());
        Assert.assertEquals("SENDING", cmd.getCommandState().toString());
        Assert.assertEquals(firedTriggerID, cmd.getFiredByTriggerID());
        Assert.assertEquals(commandTitle, cmd.getTitle());
        Assert.assertEquals(commandDescription, cmd.getDescription());
        Assert.assertNotNull(cmd.getMetadata());
        Assert.assertEquals(metaData.toString(), cmd.getMetadata().toString());

        Assert.assertEquals(2, cmd.getAliasActions().size());
        Assert.assertEquals(alias1, cmd.getAliasActions().get(0).getAlias());
        Action action1 = cmd.getAliasActions().get(0).getAction();
        Assert.assertTrue(action1 instanceof AirConditionerActions);
        Assert.assertEquals(
                true,
                ((AirConditionerActions)action1).isPower().booleanValue());
        Assert.assertEquals(
                100,
                ((AirConditionerActions)action1).getPresetTemperature().intValue());
        Action action2 = cmd.getAliasActions().get(1).getAction();
        Assert.assertTrue(action2 instanceof HumidityActions);
        Assert.assertEquals(
                50,
                ((HumidityActions)action2).getPresetHumidity().intValue());

        Assert.assertNotNull(cmd.getAliasActionResults());
        Assert.assertEquals(2, cmd.getAliasActionResults().size());
        Assert.assertEquals(alias1, cmd.getAliasActionResults().get(0).getAlias());
        Assert.assertEquals(2, cmd.getAliasActionResults().get(0).getResults().size());
        Assert.assertEquals(
                "turnPower",
                cmd.getAliasActionResults().get(0).getResults().get(0).getActionName());
        Assert.assertEquals(
                true,
                cmd.getAliasActionResults().get(0).getResults().get(0).isSucceeded());
        Assert.assertNull(
                cmd.getAliasActionResults().get(0).getResults().get(0).getErrorMessage());
        Assert.assertNull(
                cmd.getAliasActionResults().get(0).getResults().get(0).getData());
        Assert.assertEquals(
                "setPresetTemperature",
                cmd.getAliasActionResults().get(0).getResults().get(1).getActionName());
        Assert.assertEquals(
                false,
                cmd.getAliasActionResults().get(0).getResults().get(1).isSucceeded());
        Assert.assertEquals(
                "invalid value",
                cmd.getAliasActionResults().get(0).getResults().get(1).getErrorMessage());
        Assert.assertNotNull(
                cmd.getAliasActionResults().get(0).getResults().get(1).getData());
        Assert.assertEquals(
                metaData.toString(),
                cmd.getAliasActionResults().get(0).getResults().get(1).getData().toString());
        Assert.assertEquals(alias2, cmd.getAliasActionResults().get(1).getAlias());
        Assert.assertEquals(1, cmd.getAliasActionResults().get(1).getResults().size());
        Assert.assertEquals(
                "setPresetHumidity",
                cmd.getAliasActionResults().get(1).getResults().get(0).getActionName());
        Assert.assertEquals(
                true,
                cmd.getAliasActionResults().get(1).getResults().get(0).isSucceeded());
        Assert.assertNull(
                cmd.getAliasActionResults().get(1).getResults().get(0).getErrorMessage());
        Assert.assertNull(
                cmd.getAliasActionResults().get(1).getResults().get(0).getData());

        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(
                BASE_PATH + "/targets/" + thingID.toString() + "/commands/" + commandID,
                request.getPath());
        Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }

    @Test
    public void getCommand403ErrorTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String commandID = "command-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        this.addEmptyMockResponse(403);

        try {
            ThingIFAPIUtils.setTarget(api, target);
            api.getCommand(commandID);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (ForbiddenException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/commands/" + commandID, request.getPath());
        Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
    @Test
    public void getCommand404ErrorTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String commandID = "command-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        this.addEmptyMockResponse(404);

        try {
            ThingIFAPIUtils.setTarget(api, target);
            api.getCommand(commandID);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (NotFoundException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/commands/" + commandID, request.getPath());
        Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
    @Test
    public void getCommand503ErrorTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String commandID = "command-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        this.addEmptyMockResponse(503);

        try {
            ThingIFAPIUtils.setTarget(api, target);
            api.getCommand(commandID);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (ServiceUnavailableException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/commands/" + commandID, request.getPath());
        Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }

    @Test(expected = IllegalStateException.class)
    public void getCommandWithNullTargetTest() throws Exception {
        String commandID = "command-1234";
        api.getCommand(commandID);
    }
    @Test(expected = IllegalArgumentException.class)
    public void getCommandWithNullCommandIDTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        ThingIFAPIUtils.setTarget(api, target);
        api.getCommand(null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void getCommandWithEmptyCommandIDTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);
        ThingIFAPIUtils.setTarget(api, target);
        api.getCommand("");
    }

    @Test()
    public void getCommandWithUnregisterAliasTest() throws Exception{
        String commandID = "command-1234";
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);
        String commandTitle = "title";
        String commandDescription = "description";
        JSONObject metaData = new JSONObject().put("k", "v");
        String firedTriggerID = "trigger1";
        CommandState state = CommandState.SENDING;
        Long created = System.currentTimeMillis();
        Long modified = System.currentTimeMillis();

        // prepare the get command response
        JSONArray actionsInResponse = new JSONArray();
        actionsInResponse
                .put(new JSONObject().put(
                        "NewAlias",
                        new JSONArray()
                                .put(new JSONObject().put("newAction", true))));

        this.addMockResponseForGetCommand(
                200,
                commandID,
                api.getOwner().getTypedID(),
                target.getTypedID(),
                actionsInResponse,
                null,
                state,
                created,
                modified,
                firedTriggerID,
                commandTitle,
                commandDescription,
                metaData);
        ThingIFAPIUtils.setTarget(api, target);
        try {
            api.getCommand(commandID);
            Assert.fail("UnregisteredAliasException should be thrown");
        }catch (UnregisteredAliasException ex) {
        }

        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(
                BASE_PATH + "/targets/" + thingID.toString() + "/commands/" + commandID,
                request.getPath());
        Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
}