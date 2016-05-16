package com.kii.thingif;

import android.support.test.runner.AndroidJUnit4;
import android.util.Pair;

import com.kii.thingif.command.Action;
import com.kii.thingif.command.Command;
import com.kii.thingif.exception.BadRequestException;
import com.kii.thingif.exception.ForbiddenException;
import com.kii.thingif.exception.NotFoundException;
import com.kii.thingif.exception.ServiceUnavailableException;
import com.kii.thingif.schema.Schema;
import com.kii.thingif.testschemas.SetBrightness;
import com.kii.thingif.testschemas.SetColor;
import com.kii.thingif.testschemas.SetColorTemperature;
import com.kii.thingif.testschemas.TurnPower;
import com.kii.thingif.trigger.Condition;
import com.kii.thingif.trigger.Predicate;
import com.kii.thingif.trigger.ServerCode;
import com.kii.thingif.trigger.StatePredicate;
import com.kii.thingif.trigger.Trigger;
import com.kii.thingif.trigger.TriggersWhen;
import com.kii.thingif.trigger.clause.Equals;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * https://github.com/KiiCorp/ThingIF/blob/master/rest_api_spec/trigger-endpoint.yaml
 */
@RunWith(AndroidJUnit4.class)
public class ThingIFAPI_ListTriggersTest extends ThingIFAPITestBase {
    @Test
    public void listTriggersWithCommandTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);
        String paginationKey = "pagination-12345-key";

        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        api.setTarget(target);

        List<Action> command1Actions = new ArrayList<Action>();
        command1Actions.add(new TurnPower(true));
        Command command1 = new Command(schema.getSchemaName(), schema.getSchemaVersion(), target.getTypedID(), api.getOwner().getTypedID(), command1Actions);
        Condition condition1 = new Condition(new Equals("power", true));
        Predicate predicate1 = new StatePredicate(condition1, TriggersWhen.CONDITION_FALSE_TO_TRUE);
        Trigger trigger1 = new Trigger(predicate1, command1);

        List<Action> command2Actions = new ArrayList<Action>();
        command2Actions.add(new SetColor(10, 20, 30));
        Command command2 = new Command(schema.getSchemaName(), schema.getSchemaVersion(), target.getTypedID(), api.getOwner().getTypedID(), command2Actions);
        Condition condition2 = new Condition(new Equals("power", false));
        Predicate predicate2 = new StatePredicate(condition2, TriggersWhen.CONDITION_CHANGED);
        Trigger trigger2 = new Trigger(predicate2, command2);

        List<Action> command3Actions = new ArrayList<Action>();
        command3Actions.add(new SetColorTemperature(35));
        command3Actions.add(new SetBrightness(40));
        Command command3 = new Command(schema.getSchemaName(), schema.getSchemaVersion(), target.getTypedID(), api.getOwner().getTypedID(), command3Actions);
        Condition condition3 = new Condition(new Equals("colorTemperature", 28));
        Predicate predicate3 = new StatePredicate(condition3, TriggersWhen.CONDITION_TRUE);
        Trigger trigger3 = new Trigger(predicate3, command3);

        this.addMockResponseForListTriggers(200, new Trigger[] {trigger1, trigger2}, paginationKey, schema);
        this.addMockResponseForListTriggers(200, new Trigger[]{trigger3}, null, schema);

        // verify the result
        Pair<List<Trigger>, String> result1 = api.listTriggers(10, null);
        Assert.assertEquals(paginationKey, result1.second);
        List<Trigger> triggers1 = result1.first;
        Assert.assertEquals(2, triggers1.size());
        this.assertTrigger(schema, trigger1, triggers1.get(0));
        this.assertTrigger(schema, trigger2, triggers1.get(1));

        Pair<List<Trigger>, String> result2 = api.listTriggers(10, result1.second);
        Assert.assertNull(result2.second);
        List<Trigger> triggers2 = result2.first;
        Assert.assertEquals(1, triggers2.size());
        this.assertTrigger(schema, trigger3, triggers2.get(0));

        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers?bestEffortLimit=10", request1.getPath());
        Assert.assertEquals("GET", request1.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers?bestEffortLimit=10&paginationKey=" + paginationKey, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());
        this.assertRequestHeader(expectedRequestHeaders, request2);
    }
    @Test
    public void listTriggersWithServerCodeTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);
        String paginationKey = "pagination-12345-key";

        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        api.setTarget(target);

        ServerCode serverCode1 = new ServerCode("function_1", "token0001", "app00001", new JSONObject("{\"param\":\"p0001\"}"));
        Condition condition1 = new Condition(new Equals("power", true));
        Predicate predicate1 = new StatePredicate(condition1, TriggersWhen.CONDITION_FALSE_TO_TRUE);
        Trigger trigger1 = new Trigger(predicate1, serverCode1);

        ServerCode serverCode2 = new ServerCode("function_2", "token0002", "app00002", new JSONObject("{\"param\":\"p0002\"}"));
        Condition condition2 = new Condition(new Equals("power", false));
        Predicate predicate2 = new StatePredicate(condition2, TriggersWhen.CONDITION_CHANGED);
        Trigger trigger2 = new Trigger(predicate2, serverCode2);

        ServerCode serverCode3 = new ServerCode("function_3", "token0003", "app00003", new JSONObject("{\"param\":\"p0003\"}"));
        Condition condition3 = new Condition(new Equals("colorTemperature", 28));
        Predicate predicate3 = new StatePredicate(condition3, TriggersWhen.CONDITION_TRUE);
        Trigger trigger3 = new Trigger(predicate3, serverCode3);

        this.addMockResponseForListTriggers(200, new Trigger[] {trigger1, trigger2}, paginationKey, schema);
        this.addMockResponseForListTriggers(200, new Trigger[]{trigger3}, null, schema);

        // verify the result
        Pair<List<Trigger>, String> result1 = api.listTriggers(10, null);
        Assert.assertEquals(paginationKey, result1.second);
        List<Trigger> triggers1 = result1.first;
        Assert.assertEquals(2, triggers1.size());
        this.assertTrigger(schema, trigger1, triggers1.get(0));
        this.assertTrigger(schema, trigger2, triggers1.get(1));

        Pair<List<Trigger>, String> result2 = api.listTriggers(10, result1.second);
        Assert.assertNull(result2.second);
        List<Trigger> triggers2 = result2.first;
        Assert.assertEquals(1, triggers2.size());
        this.assertTrigger(schema, trigger3, triggers2.get(0));

        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers?bestEffortLimit=10", request1.getPath());
        Assert.assertEquals("GET", request1.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers?bestEffortLimit=10&paginationKey=" + paginationKey, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());
        this.assertRequestHeader(expectedRequestHeaders, request2);
    }
    @Test
    public void listTriggersWithBestEffortLimitZeroTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);
        String paginationKey = "pagination-12345-key";

        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        api.setTarget(target);

        List<Action> command1Actions = new ArrayList<Action>();
        command1Actions.add(new TurnPower(true));
        Command command1 = new Command(schema.getSchemaName(), schema.getSchemaVersion(), target.getTypedID(), api.getOwner().getTypedID(), command1Actions);
        Condition condition1 = new Condition(new Equals("power", true));
        Predicate predicate1 = new StatePredicate(condition1, TriggersWhen.CONDITION_FALSE_TO_TRUE);
        Trigger trigger1 = new Trigger(predicate1, command1);

        List<Action> command2Actions = new ArrayList<Action>();
        command2Actions.add(new SetColor(10, 20, 30));
        Command command2 = new Command(schema.getSchemaName(), schema.getSchemaVersion(), target.getTypedID(), api.getOwner().getTypedID(), command2Actions);
        Condition condition2 = new Condition(new Equals("power", false));
        Predicate predicate2 = new StatePredicate(condition2, TriggersWhen.CONDITION_CHANGED);
        Trigger trigger2 = new Trigger(predicate2, command2);

        List<Action> command3Actions = new ArrayList<Action>();
        command3Actions.add(new SetColorTemperature(35));
        command3Actions.add(new SetBrightness(40));
        Command command3 = new Command(schema.getSchemaName(), schema.getSchemaVersion(), target.getTypedID(), api.getOwner().getTypedID(), command3Actions);
        Condition condition3 = new Condition(new Equals("colorTemperature", 28));
        Predicate predicate3 = new StatePredicate(condition3, TriggersWhen.CONDITION_TRUE);
        Trigger trigger3 = new Trigger(predicate3, command3);

        this.addMockResponseForListTriggers(200, new Trigger[] {trigger1, trigger2}, paginationKey, schema);
        this.addMockResponseForListTriggers(200, new Trigger[]{trigger3}, null, schema);

        // verify the result
        Pair<List<Trigger>, String> result1 = api.listTriggers(0, null);
        Assert.assertEquals(paginationKey, result1.second);
        List<Trigger> triggers1 = result1.first;
        Assert.assertEquals(2, triggers1.size());
        this.assertTrigger(schema, trigger1, triggers1.get(0));
        this.assertTrigger(schema, trigger2, triggers1.get(1));

        Pair<List<Trigger>, String> result2 = api.listTriggers(0, result1.second);
        Assert.assertNull(result2.second);
        List<Trigger> triggers2 = result2.first;
        Assert.assertEquals(1, triggers2.size());
        this.assertTrigger(schema, trigger3, triggers2.get(0));

        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers", request1.getPath());
        Assert.assertEquals("GET", request1.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers?paginationKey=" + paginationKey, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());
        this.assertRequestHeader(expectedRequestHeaders, request2);
    }
    @Test
    public void listTriggers400ErrorTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        this.addEmptyMockResponse(400);
        try {
            api.setTarget(target);
            api.listTriggers(10, null);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (BadRequestException e) {
        }
        // verify the request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers?bestEffortLimit=10", request1.getPath());
        Assert.assertEquals("GET", request1.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request1);
    }
    @Test
    public void listTriggers403ErrorTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        this.addEmptyMockResponse(403);
        try {
            api.setTarget(target);
            api.listTriggers(10, null);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (ForbiddenException e) {
        }
        // verify the request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers?bestEffortLimit=10", request1.getPath());
        Assert.assertEquals("GET", request1.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request1);
    }
    @Test
    public void listTriggers404ErrorTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        this.addEmptyMockResponse(404);
        try {
            api.setTarget(target);
            api.listTriggers(10, null);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (NotFoundException e) {
        }
        // verify the request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers?bestEffortLimit=10", request1.getPath());
        Assert.assertEquals("GET", request1.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request1);
    }
    @Test
    public void listTriggers503ErrorTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        this.addEmptyMockResponse(503);
        try {
            api.setTarget(target);
            api.listTriggers(10, null);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (ServiceUnavailableException e) {
        }
        // verify the request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers?bestEffortLimit=10", request1.getPath());
        Assert.assertEquals("GET", request1.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request1);
    }
    @Test(expected = IllegalStateException.class)
    public void listTriggersWithNullTargetTest() throws Exception {
        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        api.listTriggers(10, null);
    }
}
