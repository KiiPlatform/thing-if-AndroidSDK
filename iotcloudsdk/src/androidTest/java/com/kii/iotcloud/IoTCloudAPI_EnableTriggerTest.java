package com.kii.iotcloud;

import android.support.test.runner.AndroidJUnit4;

import com.kii.iotcloud.command.Action;
import com.kii.iotcloud.command.Command;
import com.kii.iotcloud.exception.ForbiddenException;
import com.kii.iotcloud.exception.NotFoundException;
import com.kii.iotcloud.exception.ServiceUnavailableException;
import com.kii.iotcloud.schema.Schema;
import com.kii.iotcloud.testschemas.SetColor;
import com.kii.iotcloud.testschemas.SetColorTemperature;
import com.kii.iotcloud.trigger.Condition;
import com.kii.iotcloud.trigger.StatePredicate;
import com.kii.iotcloud.trigger.Trigger;
import com.kii.iotcloud.trigger.TriggersWhen;
import com.kii.iotcloud.trigger.clause.Equals;
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
 * https://github.com/KiiCorp/IoTCloud/blob/master/rest_api_spec/trigger-endpoint.yaml
 */
@RunWith(AndroidJUnit4.class)
public class IoTCloudAPI_EnableTriggerTest extends IoTCloudAPITestBase {
    @Test
    public void enableTriggerTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new Target(thingID, accessToken);

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        StatePredicate predicate = new StatePredicate(new Condition(new Equals("power", true)), TriggersWhen.CONDITION_CHANGED);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);

        Command expectedCommand = new Command(schema.getSchemaName(), schema.getSchemaVersion(), target.getID(), api.getOwner().getID(), actions);
        this.addEmptyMockResponse(204);
        this.addMockResponseForGetTrigger(200, triggerID, expectedCommand, predicate, false, null, schema);

        api.setTarget(target);
        Trigger trigger = api.enableTrigger(triggerID, true);
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(false, trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertFalse(trigger.disabled());
        this.assertPredicate(predicate, trigger.getPredicate());
        this.assertCommand(schema, expectedCommand, trigger.getCommand());
        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID + "/enable", request1.getPath());
        Assert.assertEquals("PUT", request1.getMethod());
        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request2);
    }
    @Test
    public void disableTriggerTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new Target(thingID, accessToken);

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        StatePredicate predicate = new StatePredicate(new Condition(new Equals("power", true)), TriggersWhen.CONDITION_CHANGED);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);

        Command expectedCommand = new Command(schema.getSchemaName(), schema.getSchemaVersion(), target.getID(), api.getOwner().getID(), actions);
        this.addEmptyMockResponse(204);
        this.addMockResponseForGetTrigger(200, triggerID, expectedCommand, predicate, true, null, schema);

        api.setTarget(target);
        Trigger trigger = api.enableTrigger(triggerID, false);
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(true, trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertTrue(trigger.disabled());
        this.assertPredicate(predicate, trigger.getPredicate());
        this.assertCommand(schema, expectedCommand, trigger.getCommand());
        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID + "/disable", request1.getPath());
        Assert.assertEquals("PUT", request1.getMethod());
        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request2);
    }
    @Test
    public void enableTrigger403Test() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new Target(thingID, accessToken);
        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        this.addEmptyMockResponse(403);
        try {
            api.setTarget(target);
            api.enableTrigger(triggerID, false);
            Assert.fail("IoTCloudRestException should be thrown");
        } catch (ForbiddenException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID + "/disable", request.getPath());
        Assert.assertEquals("PUT", request.getMethod());
    }
    @Test
    public void enableTrigger404Test() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new Target(thingID, accessToken);
        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        this.addEmptyMockResponse(404);
        try {
            api.setTarget(target);
            api.enableTrigger(triggerID, false);
            Assert.fail("IoTCloudRestException should be thrown");
        } catch (NotFoundException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID + "/disable", request.getPath());
        Assert.assertEquals("PUT", request.getMethod());
    }
    @Test
    public void enableTrigger503Test() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new Target(thingID, accessToken);
        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        this.addEmptyMockResponse(503);
        try {
            api.setTarget(target);
            api.enableTrigger(triggerID, false);
            Assert.fail("IoTCloudRestException should be thrown");
        } catch (ServiceUnavailableException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID + "/disable", request.getPath());
        Assert.assertEquals("PUT", request.getMethod());
    }
    @Test(expected = IllegalStateException.class)
    public void enableTriggerWithNullTargetTest() throws Exception {
        String triggerID = "trigger-1234";
        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        api.enableTrigger(triggerID, false);
    }
    @Test(expected = IllegalArgumentException.class)
    public void enableTriggerWithNullTriggerIDTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new Target(thingID, accessToken);
        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        api.setTarget(target);
        api.enableTrigger(null, false);
    }
    @Test(expected = IllegalArgumentException.class)
    public void enableTriggerWithEmptyTriggerIDTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new Target(thingID, accessToken);
        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        api.setTarget(target);
        api.enableTrigger("", false);
    }
}
