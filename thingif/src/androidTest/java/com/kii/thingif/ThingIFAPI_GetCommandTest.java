package com.kii.thingif;

import android.support.test.runner.AndroidJUnit4;

import com.kii.thingif.command.Action;
import com.kii.thingif.command.ActionResult;
import com.kii.thingif.command.Command;
import com.kii.thingif.command.CommandState;
import com.kii.thingif.exception.ForbiddenException;
import com.kii.thingif.exception.NotFoundException;
import com.kii.thingif.exception.ServiceUnavailableException;
import com.kii.thingif.exception.UnsupportedActionException;
import com.kii.thingif.exception.UnsupportedSchemaException;
import com.kii.thingif.internal.InternalUtils;
import com.kii.thingif.schema.Schema;
import com.kii.thingif.schema.SchemaBuilder;
import com.kii.thingif.testschemas.LightState;
import com.kii.thingif.testschemas.SetBrightness;
import com.kii.thingif.testschemas.SetBrightnessResult;
import com.kii.thingif.testschemas.SetColor;
import com.kii.thingif.testschemas.SetColorResult;
import com.kii.thingif.testschemas.SetColorTemperature;
import com.kii.thingif.testschemas.SetColorTemperatureResult;
import com.kii.thingif.testschemas.TurnPower;
import com.kii.thingif.testschemas.TurnPowerResult;
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
 * https://github.com/KiiCorp/ThingIF/blob/master/rest_api_spec/command-endpoint.yaml
 */
@RunWith(AndroidJUnit4.class)
public class ThingIFAPI_GetCommandTest extends ThingIFAPITestBase {
    @Test
    public void getCommandTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String commandID = "command-1234";
        Long created = System.currentTimeMillis();
        Long modified = System.currentTimeMillis();
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);
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

        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        this.addMockResponseForGetCommand(200, commandID, api.getOwner().getTypedID(), thingID, actions, actionResults, state, created, modified, schema);

        api.setTarget(target);
        Command command = api.getCommand(commandID);

        // verify the result
        Assert.assertEquals(commandID, command.getCommandID());
        Assert.assertEquals(DEMO_SCHEMA_NAME, command.getSchemaName());
        Assert.assertEquals(DEMO_SCHEMA_VERSION, command.getSchemaVersion());
        Assert.assertEquals(api.getOwner().getTypedID(), command.getIssuerID());
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
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        this.addEmptyMockResponse(403);

        try {
            api.setTarget(target);
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
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String commandID = "command-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        this.addEmptyMockResponse(404);

        try {
            api.setTarget(target);
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
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String commandID = "command-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        this.addEmptyMockResponse(503);

        try {
            api.setTarget(target);
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
    @Test
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
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);
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

        ThingIFAPI api = this.craeteThingIFAPIWithSchema(APP_ID, APP_KEY, invalidSchema);
        this.addMockResponseForGetCommand(200, commandID, api.getOwner().getTypedID(), thingID, actions, actionResults, state, created, modified, schema);

        try {
            api.setTarget(target);
            api.getCommand(commandID);
            Assert.fail("UnsupportedSchemaException should be thrown");
        } catch (UnsupportedSchemaException e) {
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
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);
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

        ThingIFAPI api = this.craeteThingIFAPIWithSchema(APP_ID, APP_KEY, invalidSchema);
        this.addMockResponseForGetCommand(200, commandID, api.getOwner().getTypedID(), thingID, actions, actionResults, state, created, modified, schema);

        try {
            api.setTarget(target);
            api.getCommand(commandID);
            Assert.fail("UnsupportedSchemaException should be thrown");
        } catch (UnsupportedSchemaException e) {
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
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);
        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        actions.add(setColor);
        List<ActionResult> actionResults = new ArrayList<ActionResult>();
        SetColorResult setColorResult = new SetColorResult(true);
        actionResults.add(setColorResult);
        CommandState state = CommandState.DELIVERED;

        ThingIFAPI api = this.craeteThingIFAPIWithSchema(APP_ID, APP_KEY, unsupportedSetColorSchema);
        this.addMockResponseForGetCommand(200, commandID, api.getOwner().getTypedID(), thingID, actions, actionResults, state, created, modified, schema);

        InternalUtils.gsonRepositoryClearCache();
        try {
            api.setTarget(target);
            api.getCommand(commandID);
            Assert.fail("UnsupportedActionException should be thrown");
        } catch (UnsupportedActionException e) {
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

        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        api.getCommand(commandID);
    }
    @Test(expected = IllegalArgumentException.class)
    public void getCommandWithNullCommandIDTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        api.setTarget(target);
        api.getCommand(null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void getCommandWithEmptyCommandIDTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        api.setTarget(target);
        api.getCommand("");
    }
}
