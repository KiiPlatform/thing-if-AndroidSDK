package com.kii.iotcloud;

import android.support.test.runner.AndroidJUnit4;
import android.util.Pair;

import com.kii.iotcloud.command.Action;
import com.kii.iotcloud.command.Command;
import com.kii.iotcloud.command.CommandState;
import com.kii.iotcloud.command.CommandUtils;
import com.kii.iotcloud.exception.BadRequestException;
import com.kii.iotcloud.exception.NotFoundException;
import com.kii.iotcloud.schema.Schema;
import com.kii.iotcloud.testschemas.SetBrightness;
import com.kii.iotcloud.testschemas.SetBrightnessResult;
import com.kii.iotcloud.testschemas.SetColor;
import com.kii.iotcloud.testschemas.SetColorResult;
import com.kii.iotcloud.testschemas.SetColorTemperature;
import com.kii.iotcloud.testschemas.SetColorTemperatureResult;
import com.kii.iotcloud.testschemas.TurnPower;
import com.kii.iotcloud.testschemas.TurnPowerResult;
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
public class IoTCloudAPI_ListCommandTest extends IoTCloudAPITestBase {
    @Test
    public void listCommandsTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new Target(thingID, accessToken);
        String paginationKey = "pagination-12345-key";

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        api.setTarget(target);

        List<Action> command1Actions = new ArrayList<Action>();
        command1Actions.add(new TurnPower(true));
        Command command1 = new Command(schema.getSchemaName(), schema.getSchemaVersion(), target.getTypedID(), api.getOwner().getID(), command1Actions);
        command1.addActionResult(new TurnPowerResult(true));
        CommandUtils.setCommandState(command1, CommandState.DELIVERED);
        CommandUtils.setFiredByTriggerID(command1, "trigger-1234");
        CommandUtils.setCreated(command1, System.currentTimeMillis());
        CommandUtils.setModified(command1, System.currentTimeMillis());

        List<Action> command2Actions = new ArrayList<Action>();
        command2Actions.add(new SetColor(10, 20, 30));
        Command command2 = new Command(schema.getSchemaName(), schema.getSchemaVersion(), target.getTypedID(), api.getOwner().getID(), command2Actions);
        command2.addActionResult(new SetColorResult(false));
        CommandUtils.setCommandState(command2, CommandState.SENDING);
        CommandUtils.setCreated(command2, System.currentTimeMillis());
        CommandUtils.setModified(command2, System.currentTimeMillis());

        List<Action> command3Actions = new ArrayList<Action>();
        command3Actions.add(new SetColorTemperature(35));
        command3Actions.add(new SetBrightness(40));
        Command command3 = new Command(schema.getSchemaName(), schema.getSchemaVersion(), target.getTypedID(), api.getOwner().getID(), command3Actions);
        command3.addActionResult(new SetColorTemperatureResult(true));
        command3.addActionResult(new SetBrightnessResult(true));
        CommandUtils.setCommandState(command3, CommandState.DONE);
        CommandUtils.setCreated(command3, System.currentTimeMillis());
        CommandUtils.setModified(command3, System.currentTimeMillis());

        this.addMockResponseForListCommands(200, new Command[]{command1, command2}, paginationKey, schema);
        this.addMockResponseForListCommands(200, new Command[]{command3}, null, schema);

        // verify the result
        Pair<List<Command>, String> result1 = api.listCommands(10, null);
        Assert.assertEquals(paginationKey, result1.second);
        List<Command> commands1 = result1.first;
        Assert.assertEquals(2, commands1.size());
        this.assertCommand(schema, command1, commands1.get(0));
        this.assertCommand(schema, command2, commands1.get(1));

        Pair<List<Command>, String> result2= api.listCommands(10, result1.second);
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
        Command command = new Command(schema.getSchemaName(), schema.getSchemaVersion(), target.getTypedID(), api.getOwner().getID(), commandActions);
        command.addActionResult(new TurnPowerResult(true));
        CommandUtils.setCommandState(command, CommandState.DELIVERED);
        CommandUtils.setFiredByTriggerID(command, "trigger-1234");
        CommandUtils.setCreated(command, System.currentTimeMillis());
        CommandUtils.setModified(command, System.currentTimeMillis());

        this.addMockResponseForListCommands(200, new Command[]{command}, paginationKey, schema);

        // verify the result
        api.setTarget(target);
        Pair<List<Command>, String> result = api.listCommands(0, null);
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
            api.setTarget(target);
            api.listCommands(10, null);
            Assert.fail("IoTCloudRestException should be thrown");
        } catch (BadRequestException e) {
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
            api.setTarget(target);
            api.listCommands(10, null);
            Assert.fail("IoTCloudRestException should be thrown");
        } catch (NotFoundException e) {
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
    @Test(expected = IllegalStateException.class)
    public void listCommandsWithNullTargetTest() throws Exception {
        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        api.listCommands(10, null);
    }

}