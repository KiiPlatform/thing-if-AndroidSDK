package com.kii.thingif.thingifapi;

import android.content.Context;

import com.kii.thingif.KiiApp;
import com.kii.thingif.Owner;
import com.kii.thingif.StandaloneThing;
import com.kii.thingif.Target;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.ThingIFAPITestBase;
import com.kii.thingif.TypedID;
import com.kii.thingif.actions.SetPresetHumidity;
import com.kii.thingif.actions.SetPresetTemperature;
import com.kii.thingif.actions.TurnPower;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.AliasAction;
import com.kii.thingif.command.Command;
import com.kii.thingif.command.CommandForm;
import com.kii.thingif.exception.BadRequestException;
import com.kii.thingif.exception.ForbiddenException;
import com.kii.thingif.exception.ServiceUnavailableException;
import com.kii.thingif.states.AirConditionerState;
import com.kii.thingif.states.HumidityState;
import com.kii.thingif.thingifapi.utils.ThingIFAPIUtils;
import com.kii.thingif.utils.JsonUtil;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RunWith(RobolectricTestRunner.class)
public class PostNewCommandTest extends ThingIFAPITestBase {
    private ThingIFAPI api;
    private final String alias1 = "AirConditionerAlias";
    private final String alias2 = "HumidityAlias";

    private List<AliasAction> getDefaultAliasActions() {
        List<AliasAction> aliasActions = new ArrayList<>();

        List<Action> actions1 = new ArrayList<>();
        actions1.add(new TurnPower(true));
        actions1.add(new SetPresetTemperature(23));
        aliasActions.add(new AliasAction(alias1, actions1));

        List<Action> actions2 = new ArrayList<>();
        actions2.add(new SetPresetHumidity(50));
        aliasActions.add(new AliasAction(alias2, actions2));
        return aliasActions;
    }

    @Before
    public void before() throws Exception{
        Context context = RuntimeEnvironment.application.getApplicationContext();
        this.server = new MockWebServer();
        this.server.start();

        String ownerID = UUID.randomUUID().toString();
        Owner owner = new Owner(new TypedID(TypedID.Types.USER, ownerID), "owner-access-token-1234");
        KiiApp app = getApp(APP_ID, APP_KEY);
        ThingIFAPI.Builder builder = ThingIFAPI.Builder
                .newBuilder(context, app, owner)
                .registerAction(alias1, "turnPower", TurnPower.class)
                .registerAction(alias1, "setPresetTemperature", SetPresetTemperature.class)
                .registerAction(alias2, "setPresetHumidity", SetPresetHumidity.class)
                .registerTargetState(alias1, AirConditionerState.class)
                .registerTargetState(alias2, HumidityState.class);
        this.api = builder.build();

    }
    @After
    public void after() throws Exception {
        this.server.shutdown();
    }
    @Test
    public void basicTest() throws Exception{

        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String commandID = "command-1234";
        Long created = System.currentTimeMillis();
        Long modified = System.currentTimeMillis();
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);
        String commandTitle = "title";
        String commandDescription = "description";
        JSONObject metaData = new JSONObject().put("k", "v");

        // prepare the get command response
        JSONArray aliasActionsInResponse = new JSONArray();
        aliasActionsInResponse
                .put(new JSONObject().put(
                        alias1,
                        new JSONArray()
                                .put(new JSONObject().put("turnPower", true))
                                .put(new JSONObject().put("setPresetTemperature", 23))))
                .put(new JSONObject().put(
                        alias2,
                        new JSONArray()
                                .put(new JSONObject().put("setPresetHumidity", 50))));

        ThingIFAPIUtils.setTarget(this.api, target);

        List<AliasAction> aliasActions = new ArrayList<>();

        List<Action> actions1 = new ArrayList<>();
        actions1.add(new TurnPower(true));
        actions1.add(new SetPresetTemperature(23));
        aliasActions.add(new AliasAction(alias1, actions1));

        List<Action> actions2 = new ArrayList<>();
        actions2.add(new SetPresetHumidity(50));
        aliasActions.add(new AliasAction(alias2, actions2));

        CommandForm form = CommandForm
                .Builder
                .newBuilder(aliasActions)
                .setTitle(commandTitle)
                .setDescription(commandDescription)
                .setMetadata(metaData)
                .build();

        this.addMockResponseForPostNewCommand(201, commandID);
        this.addMockResponseForGetCommand(
                200,
                commandID,
                api.getOwner().getTypedID(),
                thingID,
                aliasActionsInResponse,
                null,
                null,
                created,
                modified,
                null,
                null,
                null,
                null);

        Command createdCommand =api.postNewCommand(form);
        Assert.assertNotNull(createdCommand);

        // verify result
        Assert.assertEquals(commandID, createdCommand.getCommandID());
        Assert.assertEquals(
                api.getOwner().getTypedID().toString(),
                createdCommand.getIssuerID().toString());
        Assert.assertNotNull(createdCommand.getTargetID());
        Assert.assertEquals(thingID.toString(), createdCommand.getTargetID().toString());
        Assert.assertNotNull(createdCommand.getCreated());
        Assert.assertEquals(created.longValue(), createdCommand.getCreated().longValue());
        Assert.assertNotNull(createdCommand.getModified());
        Assert.assertEquals(modified.longValue(), createdCommand.getModified().longValue());
        Assert.assertNull(createdCommand.getCommandState());
        Assert.assertNull(createdCommand.getFiredByTriggerID());
        Assert.assertNull(createdCommand.getTitle());
        Assert.assertNull(createdCommand.getDescription());
        Assert.assertNull(createdCommand.getMetadata());
        Assert.assertNull(createdCommand.getAliasActionResults());

        Assert.assertEquals(2, createdCommand.getAliasActions().size());
        assertJSONObject(
                JsonUtil.aliasActionToJson(aliasActions.get(0)),
                JsonUtil.aliasActionToJson(createdCommand.getAliasActions().get(0)));
        assertJSONObject(
                JsonUtil.aliasActionToJson(aliasActions.get(1)),
                JsonUtil.aliasActionToJson(createdCommand.getAliasActions().get(1)));

        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/commands", request1.getPath());
        Assert.assertEquals("POST", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/vnd.kii.CommandCreationRequest+json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("issuer", api.getOwner().getTypedID().toString());
        expectedRequestBody.put("actions", aliasActionsInResponse);
        expectedRequestBody.put("title", commandTitle);
        expectedRequestBody.put("description", commandDescription);
        expectedRequestBody.put("metadata", metaData);
        this.assertRequestBody(expectedRequestBody, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/commands/" + commandID, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());

        Map<String, String> expectedRequestHeaders2 = new HashMap<>();
        expectedRequestHeaders2.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders2.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders2.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders2, request2);
    }

    @Test
    public void postNewCommand400ErrorTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        CommandForm form = CommandForm
                .Builder
                .newBuilder(getDefaultAliasActions())
                .build();
        this.addEmptyMockResponse(400);

        try {
            ThingIFAPIUtils.setTarget(api, target);
            api.postNewCommand(form);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (BadRequestException e) {
        }
        // verify the request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/commands", request1.getPath());
        Assert.assertEquals("POST", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/vnd.kii.CommandCreationRequest+json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JSONArray aliasActionsInRequest = new JSONArray();
        aliasActionsInRequest
                .put(new JSONObject().put(
                        alias1,
                        new JSONArray()
                                .put(new JSONObject().put("turnPower", true))
                                .put(new JSONObject().put("setPresetTemperature", 23))))
                .put(new JSONObject().put(
                        alias2,
                        new JSONArray()
                                .put(new JSONObject().put("setPresetHumidity", 50))));
        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("issuer", api.getOwner().getTypedID().toString());
        expectedRequestBody.put("actions", aliasActionsInRequest);
        this.assertRequestBody(expectedRequestBody, request1);
    }
    @Test
    public void postNewCommand403ErrorTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        CommandForm form = CommandForm
                .Builder
                .newBuilder(getDefaultAliasActions())
                .build();

        this.addEmptyMockResponse(403);

        try {
            ThingIFAPIUtils.setTarget(api, target);
            api.postNewCommand(form);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (ForbiddenException e) {
            Assert.assertEquals(403, e.getStatusCode());
        }
        // verify the request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/commands", request1.getPath());
        Assert.assertEquals("POST", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/vnd.kii.CommandCreationRequest+json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JSONArray aliasActionsInRequest = new JSONArray();
        aliasActionsInRequest
                .put(new JSONObject().put(
                        alias1,
                        new JSONArray()
                                .put(new JSONObject().put("turnPower", true))
                                .put(new JSONObject().put("setPresetTemperature", 23))))
                .put(new JSONObject().put(
                        alias2,
                        new JSONArray()
                                .put(new JSONObject().put("setPresetHumidity", 50))));
        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("issuer", api.getOwner().getTypedID().toString());
        expectedRequestBody.put("actions", aliasActionsInRequest);
        this.assertRequestBody(expectedRequestBody, request1);
    }
    @Test
    public void postNewCommand503ErrorTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        CommandForm form = CommandForm
                .Builder
                .newBuilder(getDefaultAliasActions())
                .build();
        this.addEmptyMockResponse(503);

        try {
            ThingIFAPIUtils.setTarget(api, target);
            api.postNewCommand(form);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (ServiceUnavailableException e) {
        }
        // verify the request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/commands", request1.getPath());
        Assert.assertEquals("POST", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/vnd.kii.CommandCreationRequest+json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JSONArray aliasActionsInRequest = new JSONArray();
        aliasActionsInRequest
                .put(new JSONObject().put(
                        alias1,
                        new JSONArray()
                                .put(new JSONObject().put("turnPower", true))
                                .put(new JSONObject().put("setPresetTemperature", 23))))
                .put(new JSONObject().put(
                        alias2,
                        new JSONArray()
                                .put(new JSONObject().put("setPresetHumidity", 50))));
        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("issuer", api.getOwner().getTypedID().toString());
        expectedRequestBody.put("actions", aliasActionsInRequest);
        this.assertRequestBody(expectedRequestBody, request1);
    }
}
