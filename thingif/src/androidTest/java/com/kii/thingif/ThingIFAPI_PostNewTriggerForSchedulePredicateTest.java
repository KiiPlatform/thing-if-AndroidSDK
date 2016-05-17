package com.kii.thingif;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.Command;
import com.kii.thingif.internal.GsonRepository;
import com.kii.thingif.schema.Schema;
import com.kii.thingif.testschemas.SetColor;
import com.kii.thingif.testschemas.SetColorTemperature;
import com.kii.thingif.trigger.Predicate;
import com.kii.thingif.trigger.Schedule;
import com.kii.thingif.trigger.SchedulePredicate;
import com.kii.thingif.trigger.Trigger;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2016/05/17.
 */
public class ThingIFAPI_PostNewTriggerForSchedulePredicateTest
    extends ThingIFAPITestBase
{
    @Test
    public void postNewTriggerWithSchedulePredicateTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingID.getID(),
                "vendor-thing-id", accessToken);

        List<Action> actions = new ArrayList<Action>();
        actions.add(new SetColor(128, 0, 255));
        actions.add(new SetColorTemperature(25));
        Predicate predicate = new SchedulePredicate(new Schedule("1 * * * *"));

        ThingIFAPI api = this.createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);

        Command expectedCommand = new Command(
                schema.getSchemaName(),
                schema.getSchemaVersion(),
                target.getTypedID(),
                api.getOwner().getTypedID(),
                actions);
        this.addMockResponseForPostNewTrigger(201, triggerID);
        this.addMockResponseForGetTriggerWithCommand(
                200,
                triggerID,
                expectedCommand,
                predicate,
                false,
                null,
                schema);

        api.setTarget(target);
        Trigger trigger = api.postNewTrigger(
                DEMO_SCHEMA_NAME,
                DEMO_SCHEMA_VERSION,
                actions,
                predicate);

        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(false, trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertNull(trigger.getServerCode());
        this.assertPredicate(predicate, trigger.getPredicate());
        this.assertCommand(schema, expectedCommand, trigger.getCommand());
        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(
            BASE_PATH + "/targets/" + thingID.toString() + "/triggers",
            request1.getPath());
        Assert.assertEquals("POST", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 =
                new HashMap<String, String>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization",
                "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.add("command",
                GsonRepository.gson(schema).toJsonTree(expectedCommand));
        expectedRequestBody.add("predicate",
                GsonRepository.gson(schema).toJsonTree(predicate));
        expectedRequestBody.add("triggersWhat", new JsonPrimitive("COMMAND"));
        this.assertRequestBody(expectedRequestBody, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(
                BASE_PATH +
                    "/targets/" +
                    thingID.toString() +
                    "/triggers/" +
                    triggerID,
                request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());

        Map<String, String> expectedRequestHeaders2 =
                new HashMap<String, String>();
        expectedRequestHeaders2.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders2.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders2.put(
            "Authorization",
            "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders2, request2);
    }
}
