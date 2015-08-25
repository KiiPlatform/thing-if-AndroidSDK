package com.kii.iotcloud;

import android.support.test.runner.AndroidJUnit4;
import android.util.Pair;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kii.iotcloud.command.Action;
import com.kii.iotcloud.command.ActionResult;
import com.kii.iotcloud.command.Command;
import com.kii.iotcloud.command.CommandState;
import com.kii.iotcloud.command.CommandUtils;
import com.kii.iotcloud.exception.IoTCloudRestException;
import com.kii.iotcloud.exception.UnsupportedActionException;
import com.kii.iotcloud.exception.UnsupportedSchemaException;
import com.kii.iotcloud.schema.Schema;
import com.kii.iotcloud.schema.SchemaBuilder;
import com.kii.iotcloud.testmodel.LightState;
import com.kii.iotcloud.testmodel.SetBrightness;
import com.kii.iotcloud.testmodel.SetBrightnessResult;
import com.kii.iotcloud.testmodel.SetColor;
import com.kii.iotcloud.testmodel.SetColorResult;
import com.kii.iotcloud.testmodel.SetColorTemperature;
import com.kii.iotcloud.testmodel.SetColorTemperatureResult;
import com.kii.iotcloud.testmodel.TurnPower;
import com.kii.iotcloud.testmodel.TurnPowerResult;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * https://github.com/KiiCorp/IoTCloud/blob/master/rest_api_spec/command-endpoint.yaml
 */
@RunWith(AndroidJUnit4.class)
public class IoTCloudAPI_CommandTest extends IoTCloudAPITestBase {
    @Test
    public void postNewCommandTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String commandID = "command-1234";
        Long created = System.currentTimeMillis();
        Long modified = System.currentTimeMillis();
        Target target = new Target(thingID, accessToken);
        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);

        this.addMockResponseForPostNewCommand(201, commandID);
        this.addMockResponseForGetCommand(200, commandID, api.getOwner().getID(), thingID, actions, null, null, schema, created, modified);

        Command command = api.postNewCommand(target, DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions);
        // verify the result
        Assert.assertEquals(commandID, command.getCommandID());
        Assert.assertEquals(DEMO_SCHEMA_NAME, command.getSchemaName());
        Assert.assertEquals(DEMO_SCHEMA_VERSION, command.getSchemaVersion());
        Assert.assertEquals(api.getOwner().getID(), command.getIssuerID());
        Assert.assertEquals(thingID, command.getTargetID());
        Assert.assertEquals(created, command.getCreated());
        Assert.assertEquals(modified, command.getModified());
        Assert.assertNull(command.getCommandState());
        Assert.assertNull(command.getFiredByTriggerID());
        Assert.assertEquals(2, command.getActions().size());
        Assert.assertEquals(setColor.getActionName(), ((SetColor)command.getActions().get(0)).getActionName());
        Assert.assertArrayEquals(setColor.color, ((SetColor) command.getActions().get(0)).color);
        Assert.assertEquals(setColorTemperature.getActionName(), ((SetColorTemperature) command.getActions().get(1)).getActionName());
        Assert.assertEquals(setColorTemperature.colorTemperature, ((SetColorTemperature) command.getActions().get(1)).colorTemperature);
        Assert.assertEquals(0, command.getActionResults().size());
        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/commands", request1.getPath());
        Assert.assertEquals("POST", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<String, String>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("schema", DEMO_SCHEMA_NAME);
        expectedRequestBody.addProperty("schemaVersion", DEMO_SCHEMA_VERSION);
        expectedRequestBody.addProperty("issuer", api.getOwner().getID().toString());
        JsonArray expectedActions = new JsonArray();
        for (Action action : actions) {
            expectedActions.add(GsonRepository.gson(schema).toJsonTree(action));
        }
        expectedRequestBody.add("actions", expectedActions);
        this.assertRequestBody(expectedRequestBody, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/commands/" + commandID, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());

        Map<String, String> expectedRequestHeaders2 = new HashMap<String, String>();
        expectedRequestHeaders2.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders2.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders2.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders2, request2);
    }
    @Test
    public void postNewCommand400ErrorTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String commandID = "command-1234";
        Target target = new Target(thingID, accessToken);
        List<Action> actions = new ArrayList<Action>();
        actions.add(new SetColor(128, 0, 255));

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);

        this.addEmptyMockResponse(400);

        try {
            api.postNewCommand(target, DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions);
            Assert.fail("IoTCloudRestException should be thrown");
        } catch (IoTCloudRestException e) {
            Assert.assertEquals(400, e.getStatusCode());
        }
        // verify the request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/commands", request1.getPath());
        Assert.assertEquals("POST", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<String, String>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("schema", DEMO_SCHEMA_NAME);
        expectedRequestBody.addProperty("schemaVersion", DEMO_SCHEMA_VERSION);
        expectedRequestBody.addProperty("issuer", api.getOwner().getID().toString());
        JsonArray expectedActions = new JsonArray();
        for (Action action : actions) {
            expectedActions.add(GsonRepository.gson(schema).toJsonTree(action));
        }
        expectedRequestBody.add("actions", expectedActions);
        this.assertRequestBody(expectedRequestBody, request1);
    }
    @Test
    public void postNewCommand403ErrorTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String commandID = "command-1234";
        Target target = new Target(thingID, accessToken);
        List<Action> actions = new ArrayList<Action>();
        actions.add(new SetColor(128, 0, 255));

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);

        this.addEmptyMockResponse(403);

        try {
            api.postNewCommand(target, DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions);
            Assert.fail("IoTCloudRestException should be thrown");
        } catch (IoTCloudRestException e) {
            Assert.assertEquals(403, e.getStatusCode());
        }
        // verify the request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/commands", request1.getPath());
        Assert.assertEquals("POST", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<String, String>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("schema", DEMO_SCHEMA_NAME);
        expectedRequestBody.addProperty("schemaVersion", DEMO_SCHEMA_VERSION);
        expectedRequestBody.addProperty("issuer", api.getOwner().getID().toString());
        JsonArray expectedActions = new JsonArray();
        for (Action action : actions) {
            expectedActions.add(GsonRepository.gson(schema).toJsonTree(action));
        }
        expectedRequestBody.add("actions", expectedActions);
        this.assertRequestBody(expectedRequestBody, request1);
    }
    @Test
    public void postNewCommand503ErrorTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String commandID = "command-1234";
        Target target = new Target(thingID, accessToken);
        List<Action> actions = new ArrayList<Action>();
        actions.add(new SetColor(128, 0, 255));

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);

        this.addEmptyMockResponse(503);

        try {
            api.postNewCommand(target, DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions);
            Assert.fail("IoTCloudRestException should be thrown");
        } catch (IoTCloudRestException e) {
            Assert.assertEquals(503, e.getStatusCode());
        }
        // verify the request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/commands", request1.getPath());
        Assert.assertEquals("POST", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<String, String>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("schema", DEMO_SCHEMA_NAME);
        expectedRequestBody.addProperty("schemaVersion", DEMO_SCHEMA_VERSION);
        expectedRequestBody.addProperty("issuer", api.getOwner().getID().toString());
        JsonArray expectedActions = new JsonArray();
        for (Action action : actions) {
            expectedActions.add(GsonRepository.gson(schema).toJsonTree(action));
        }
        expectedRequestBody.add("actions", expectedActions);
        this.assertRequestBody(expectedRequestBody, request1);
    }
    @Test(expected = IllegalArgumentException.class)
    public void postNewCommandWithNullTargetTest() throws Exception {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new SetColor(128, 0, 255));
        actions.add(new SetColorTemperature(25));

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        api.postNewCommand(null, DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions);
    }
    @Test(expected = UnsupportedSchemaException.class)
    public void postNewCommandWithNullSchemaNameTest() throws Exception {
        Target target = new Target(new TypedID(TypedID.Types.THING, "th.1234567890"), "thing-access-token-1234");
        List<Action> actions = new ArrayList<Action>();
        actions.add(new SetColor(128, 0, 255));
        actions.add(new SetColorTemperature(25));

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        api.postNewCommand(target, null, DEMO_SCHEMA_VERSION, actions);
    }
    @Test(expected = IllegalArgumentException.class)
    public void postNewCommandWithNullActionsTest() throws Exception {
        Target target = new Target(new TypedID(TypedID.Types.THING, "th.1234567890"), "thing-access-token-1234");

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        api.postNewCommand(target, DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void postNewCommandWithEmptyActionsTest() throws Exception {
        Target target = new Target(new TypedID(TypedID.Types.THING, "th.1234567890"), "thing-access-token-1234");
        List<Action> actions = new ArrayList<Action>();

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        api.postNewCommand(target, DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions);
    }
    @Test
    public void getCommandTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String commandID = "command-1234";
        Long created = System.currentTimeMillis();
        Long modified = System.currentTimeMillis();
        Target target = new Target(thingID, accessToken);
        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        List<ActionResult> actionResults = new ArrayList<ActionResult>();
        SetColorResult setColorResult = new SetColorResult(true);
        SetColorTemperatureResult setColorTemperatureResult = new SetColorTemperatureResult(false);
        actionResults.add(setColorResult);
        actionResults.add(setColorTemperatureResult);
        CommandState state = CommandState.DELIVERED;

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        this.addMockResponseForGetCommand(200, commandID, api.getOwner().getID(), thingID, actions, actionResults, state, schema, created, modified);

        Command command = api.getCommand(target, commandID);

        // verify the result
        Assert.assertEquals(commandID, command.getCommandID());
        Assert.assertEquals(DEMO_SCHEMA_NAME, command.getSchemaName());
        Assert.assertEquals(DEMO_SCHEMA_VERSION, command.getSchemaVersion());
        Assert.assertEquals(api.getOwner().getID(), command.getIssuerID());
        Assert.assertEquals(thingID, command.getTargetID());
        Assert.assertEquals(created, command.getCreated());
        Assert.assertEquals(modified, command.getModified());
        Assert.assertEquals(state, command.getCommandState());
        Assert.assertNull(command.getFiredByTriggerID());
        Assert.assertEquals(2, command.getActions().size());
        Assert.assertEquals(setColor.getActionName(), ((SetColor) command.getActions().get(0)).getActionName());
        Assert.assertArrayEquals(setColor.color, ((SetColor) command.getActions().get(0)).color);
        Assert.assertEquals(setColorTemperature.getActionName(), ((SetColorTemperature) command.getActions().get(1)).getActionName());
        Assert.assertEquals(setColorTemperature.colorTemperature, ((SetColorTemperature) command.getActions().get(1)).colorTemperature);
        Assert.assertEquals(2, command.getActionResults().size());
        Assert.assertEquals(setColorResult.getActionName(), ((SetColorResult)command.getActionResults().get(0)).getActionName());
        Assert.assertEquals(setColorResult.succeeded, ((SetColorResult) command.getActionResults().get(0)).succeeded);
        Assert.assertEquals(setColorTemperatureResult.getActionName(), ((SetColorTemperatureResult)command.getActionResults().get(1)).getActionName());
        Assert.assertEquals(setColorTemperatureResult.succeeded, ((SetColorTemperatureResult) command.getActionResults().get(1)).succeeded);

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
    public void getCommand403ErrorTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String commandID = "command-1234";
        Target target = new Target(thingID, accessToken);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        this.addEmptyMockResponse(403);

        try {
            api.getCommand(target, commandID);
            Assert.fail("IoTCloudRestException should be thrown");
        } catch (IoTCloudRestException e) {
            Assert.assertEquals(403, e.getStatusCode());
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
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String commandID = "command-1234";
        Target target = new Target(thingID, accessToken);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        this.addEmptyMockResponse(404);

        try {
            api.getCommand(target, commandID);
            Assert.fail("IoTCloudRestException should be thrown");
        } catch (IoTCloudRestException e) {
            Assert.assertEquals(404, e.getStatusCode());
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
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String commandID = "command-1234";
        Target target = new Target(thingID, accessToken);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        this.addEmptyMockResponse(503);

        try {
            api.getCommand(target, commandID);
            Assert.fail("IoTCloudRestException should be thrown");
        } catch (IoTCloudRestException e) {
            Assert.assertEquals(503, e.getStatusCode());
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
    @Test(expected = UnsupportedSchemaException.class)
    public void getCommandWithInvalidSchemaNameTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        SchemaBuilder sb = SchemaBuilder.newSchemaBuilder(DEMO_THING_TYPE, DEMO_SCHEMA_NAME + "_invalid", DEMO_SCHEMA_VERSION, LightState.class);
        sb.addActionClass(SetColor.class, SetColorResult.class);
        sb.addActionClass(SetBrightness.class, SetBrightnessResult.class);
        sb.addActionClass(SetColorTemperature.class, SetColorTemperatureResult.class);
        sb.addActionClass(TurnPower.class, TurnPowerResult.class);
        Schema invalidSchema = sb.build();

        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String commandID = "command-1234";
        Long created = System.currentTimeMillis();
        Long modified = System.currentTimeMillis();
        Target target = new Target(thingID, accessToken);
        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        List<ActionResult> actionResults = new ArrayList<ActionResult>();
        SetColorResult setColorResult = new SetColorResult(true);
        SetColorTemperatureResult setColorTemperatureResult = new SetColorTemperatureResult(false);
        actionResults.add(setColorResult);
        actionResults.add(setColorTemperatureResult);
        CommandState state = CommandState.DELIVERED;

        IoTCloudAPI api = this.craeteIoTCloudAPIWithSchema(APP_ID, APP_KEY, invalidSchema);
        this.addMockResponseForGetCommand(200, commandID, api.getOwner().getID(), thingID, actions, actionResults, state, schema, created, modified);

        api.getCommand(target, commandID);
    }
    @Test(expected = UnsupportedSchemaException.class)
    public void getCommandWithInvalidSchemaVersionTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        SchemaBuilder sb = SchemaBuilder.newSchemaBuilder(DEMO_THING_TYPE, DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION + 1, LightState.class);
        sb.addActionClass(SetColor.class, SetColorResult.class);
        sb.addActionClass(SetBrightness.class, SetBrightnessResult.class);
        sb.addActionClass(SetColorTemperature.class, SetColorTemperatureResult.class);
        sb.addActionClass(TurnPower.class, TurnPowerResult.class);
        Schema invalidSchema = sb.build();

        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String commandID = "command-1234";
        Long created = System.currentTimeMillis();
        Long modified = System.currentTimeMillis();
        Target target = new Target(thingID, accessToken);
        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        List<ActionResult> actionResults = new ArrayList<ActionResult>();
        SetColorResult setColorResult = new SetColorResult(true);
        SetColorTemperatureResult setColorTemperatureResult = new SetColorTemperatureResult(false);
        actionResults.add(setColorResult);
        actionResults.add(setColorTemperatureResult);
        CommandState state = CommandState.DELIVERED;

        IoTCloudAPI api = this.craeteIoTCloudAPIWithSchema(APP_ID, APP_KEY, invalidSchema);
        this.addMockResponseForGetCommand(200, commandID, api.getOwner().getID(), thingID, actions, actionResults, state, schema, created, modified);

        api.getCommand(target, commandID);
    }
    @Test(expected = UnsupportedActionException.class)
    public void getCommandWithUnknownActionTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        SchemaBuilder sb = SchemaBuilder.newSchemaBuilder(DEMO_THING_TYPE, DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, LightState.class);
        sb.addActionClass(SetBrightness.class, SetBrightnessResult.class);
        sb.addActionClass(SetColorTemperature.class, SetColorTemperatureResult.class);
        sb.addActionClass(TurnPower.class, TurnPowerResult.class);
        Schema unsupportedSetColorSchema = sb.build();

        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String commandID = "command-1234";
        Long created = System.currentTimeMillis();
        Long modified = System.currentTimeMillis();
        Target target = new Target(thingID, accessToken);
        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        actions.add(setColor);
        List<ActionResult> actionResults = new ArrayList<ActionResult>();
        SetColorResult setColorResult = new SetColorResult(true);
        actionResults.add(setColorResult);
        CommandState state = CommandState.DELIVERED;

        IoTCloudAPI api = this.craeteIoTCloudAPIWithSchema(APP_ID, APP_KEY, unsupportedSetColorSchema);
        this.addMockResponseForGetCommand(200, commandID, api.getOwner().getID(), thingID, actions, actionResults, state, schema, created, modified);

        GsonRepository.clearCache();
        api.getCommand(target, commandID);
    }
    @Test(expected = IllegalArgumentException.class)
    public void getCommandWithNullTargetTest() throws Exception {
        String commandID = "command-1234";

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        api.getCommand(null, commandID);
    }
    @Test(expected = IllegalArgumentException.class)
    public void getCommandWithNullCommandIDTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new Target(thingID, accessToken);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        api.getCommand(target, null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void getCommandWithEmptyCommandIDTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new Target(thingID, accessToken);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        api.getCommand(target, "");
    }
    @Test
    public void listCommandsTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new Target(thingID, accessToken);
        String paginationKey = "pagination-12345-key";

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);

        List<Action> command1Actions = new ArrayList<Action>();
        command1Actions.add(new TurnPower(true));
        Command command1 = new Command(schema.getSchemaName(), schema.getSchemaVersion(), target.getID(), api.getOwner().getID(), command1Actions);
        command1.addActionResult(new TurnPowerResult(true));
        CommandUtils.setCommandState(command1, CommandState.DELIVERED);
        CommandUtils.setFiredByTriggerID(command1, "trigger-1234");
        CommandUtils.setCreated(command1, System.currentTimeMillis());
        CommandUtils.setModified(command1, System.currentTimeMillis());

        List<Action> command2Actions = new ArrayList<Action>();
        command2Actions.add(new SetColor(10, 20, 30));
        Command command2 = new Command(schema.getSchemaName(), schema.getSchemaVersion(), target.getID(), api.getOwner().getID(), command2Actions);
        command2.addActionResult(new SetColorResult(false));
        CommandUtils.setCommandState(command2, CommandState.SENDING);
        CommandUtils.setCreated(command2, System.currentTimeMillis());
        CommandUtils.setModified(command2, System.currentTimeMillis());

        List<Action> command3Actions = new ArrayList<Action>();
        command3Actions.add(new SetColorTemperature(35));
        command3Actions.add(new SetBrightness(40));
        Command command3 = new Command(schema.getSchemaName(), schema.getSchemaVersion(), target.getID(), api.getOwner().getID(), command3Actions);
        command3.addActionResult(new SetColorTemperatureResult(true));
        command3.addActionResult(new SetBrightnessResult(true));
        CommandUtils.setCommandState(command3, CommandState.DONE);
        CommandUtils.setCreated(command3, System.currentTimeMillis());
        CommandUtils.setModified(command3, System.currentTimeMillis());

        this.addMockResponseForListCommands(200, schema, new Command[]{command1, command2}, paginationKey);
        this.addMockResponseForListCommands(200, schema, new Command[]{command3}, null);

        // verify the result
        Pair<List<Command>, String> result1 = api.listCommands(target, 10, null);
        Assert.assertEquals(paginationKey, result1.second);
        List<Command> commands1 = result1.first;
        Assert.assertEquals(2, commands1.size());
        this.assertCommand(schema, command1, commands1.get(0));
        this.assertCommand(schema, command2, commands1.get(1));

        Pair<List<Command>, String> result2= api.listCommands(target, 10, result1.second);
        Assert.assertNull(result2.second);
        List<Command> commands2 = result2.first;
        Assert.assertEquals(1, commands2.size());
        this.assertCommand(schema, command3, commands2.get(0));

        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/commands?bestEffortLimit=10", request1.getPath());
        Assert.assertEquals("GET", request1.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/commands?bestEffortLimit=10&paginationKey=" + paginationKey, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());

        this.assertRequestHeader(expectedRequestHeaders, request2);
    }
    @Test
    public void listCommandsWithBestEffortLimitZeroTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new Target(thingID, accessToken);
        String paginationKey = "pagination-12345-key";

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);

        List<Action> commandActions = new ArrayList<Action>();
        commandActions.add(new TurnPower(true));
        Command command = new Command(schema.getSchemaName(), schema.getSchemaVersion(), target.getID(), api.getOwner().getID(), commandActions);
        command.addActionResult(new TurnPowerResult(true));
        CommandUtils.setCommandState(command, CommandState.DELIVERED);
        CommandUtils.setFiredByTriggerID(command, "trigger-1234");
        CommandUtils.setCreated(command, System.currentTimeMillis());
        CommandUtils.setModified(command, System.currentTimeMillis());

        this.addMockResponseForListCommands(200, schema, new Command[]{command}, paginationKey);

        // verify the result
        Pair<List<Command>, String> result = api.listCommands(target, 0, null);
        Assert.assertEquals(paginationKey, result.second);
        List<Command> commands = result.first;
        Assert.assertEquals(1, commands.size());
        this.assertCommand(schema, command, commands.get(0));

        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/commands", request.getPath());
        Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
    @Test
    public void listCommands400ErrorTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new Target(thingID, accessToken);
        String paginationKey = "pagination-12345-key";

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);

        this.addEmptyMockResponse(400);

        try {
            api.listCommands(target, 10, null);
            Assert.fail("IoTCloudRestException should be thrown");
        } catch (IoTCloudRestException e) {
            Assert.assertEquals(400, e.getStatusCode());
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/commands?bestEffortLimit=10", request.getPath());
        Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
    @Test
    public void listCommands404ErrorTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new Target(thingID, accessToken);
        String paginationKey = "pagination-12345-key";

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);

        this.addEmptyMockResponse(404);

        try {
            api.listCommands(target, 10, null);
            Assert.fail("IoTCloudRestException should be thrown");
        } catch (IoTCloudRestException e) {
            Assert.assertEquals(404, e.getStatusCode());
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/commands?bestEffortLimit=10", request.getPath());
        Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
    @Test(expected = IllegalArgumentException.class)
    public void listCommandsWithNullTargetTest() throws Exception {
        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        api.listCommands(null, 10, null);
    }

}