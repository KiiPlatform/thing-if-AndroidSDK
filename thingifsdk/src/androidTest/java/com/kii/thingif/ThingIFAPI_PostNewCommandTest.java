package com.kii.thingif;

import android.support.test.runner.AndroidJUnit4;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.Command;
import com.kii.thingif.exception.BadRequestException;
import com.kii.thingif.exception.ForbiddenException;
import com.kii.thingif.exception.ServiceUnavailableException;
import com.kii.thingif.exception.UnsupportedSchemaException;
import com.kii.thingif.internal.GsonRepository;
import com.kii.thingif.schema.Schema;
import com.kii.thingif.testschemas.SetColor;
import com.kii.thingif.testschemas.SetColorTemperature;
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
public class ThingIFAPI_PostNewCommandTest extends IoTCloudAPITestBase {
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

        ThingIFAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);

        this.addMockResponseForPostNewCommand(201, commandID);
        this.addMockResponseForGetCommand(200, commandID, api.getOwner().getTypedID(), thingID, actions, null, null, created, modified, schema);

        api.setTarget(target);
        Command command = api.postNewCommand(DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions);
        // verify the result
        Assert.assertEquals(commandID, command.getCommandID());
        Assert.assertEquals(DEMO_SCHEMA_NAME, command.getSchemaName());
        Assert.assertEquals(DEMO_SCHEMA_VERSION, command.getSchemaVersion());
        Assert.assertEquals(api.getOwner().getTypedID(), command.getIssuerID());
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
        Assert.assertNull(command.getActionResults());
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
        expectedRequestBody.addProperty("issuer", api.getOwner().getTypedID().toString());
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

        ThingIFAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);

        this.addEmptyMockResponse(400);

        try {
            api.setTarget(target);
            api.postNewCommand(DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions);
            Assert.fail("IoTCloudRestException should be thrown");
        } catch (BadRequestException e) {
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
        expectedRequestBody.addProperty("issuer", api.getOwner().getTypedID().toString());
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

        ThingIFAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);

        this.addEmptyMockResponse(403);

        try {
            api.setTarget(target);
            api.postNewCommand(DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions);
            Assert.fail("IoTCloudRestException should be thrown");
        } catch (ForbiddenException e) {
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
        expectedRequestBody.addProperty("issuer", api.getOwner().getTypedID().toString());
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

        ThingIFAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);

        this.addEmptyMockResponse(503);

        try {
            api.setTarget(target);
            api.postNewCommand(DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions);
            Assert.fail("IoTCloudRestException should be thrown");
        } catch (ServiceUnavailableException e) {
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
        expectedRequestBody.addProperty("issuer", api.getOwner().getTypedID().toString());
        JsonArray expectedActions = new JsonArray();
        for (Action action : actions) {
            expectedActions.add(GsonRepository.gson(schema).toJsonTree(action));
        }
        expectedRequestBody.add("actions", expectedActions);
        this.assertRequestBody(expectedRequestBody, request1);
    }
    @Test(expected = IllegalStateException.class)
    public void postNewCommandWithNullTargetTest() throws Exception {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new SetColor(128, 0, 255));
        actions.add(new SetColorTemperature(25));

        ThingIFAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        api.postNewCommand(DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions);
    }
    @Test(expected = UnsupportedSchemaException.class)
    public void postNewCommandWithNullSchemaNameTest() throws Exception {
        Target target = new Target(new TypedID(TypedID.Types.THING, "th.1234567890"), "thing-access-token-1234");
        List<Action> actions = new ArrayList<Action>();
        actions.add(new SetColor(128, 0, 255));
        actions.add(new SetColorTemperature(25));

        ThingIFAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        api.setTarget(target);
        api.postNewCommand(null, DEMO_SCHEMA_VERSION, actions);
    }
    @Test(expected = IllegalArgumentException.class)
    public void postNewCommandWithNullActionsTest() throws Exception {
        Target target = new Target(new TypedID(TypedID.Types.THING, "th.1234567890"), "thing-access-token-1234");

        ThingIFAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        api.setTarget(target);
        api.postNewCommand(DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void postNewCommandWithEmptyActionsTest() throws Exception {
        Target target = new Target(new TypedID(TypedID.Types.THING, "th.1234567890"), "thing-access-token-1234");
        List<Action> actions = new ArrayList<Action>();

        ThingIFAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        api.setTarget(target);
        api.postNewCommand(DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions);
    }
}
