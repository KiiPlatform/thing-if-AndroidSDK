package com.kii.thingif.thingifapi;

import android.content.Context;

import com.kii.thingif.StandaloneThing;
import com.kii.thingif.Target;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.ThingIFAPITestBase;
import com.kii.thingif.TypedID;
import com.kii.thingif.clause.query.EqualsClauseInQuery;
import com.kii.thingif.exception.BadRequestException;
import com.kii.thingif.exception.ConflictException;
import com.kii.thingif.exception.ForbiddenException;
import com.kii.thingif.exception.NotFoundException;
import com.kii.thingif.exception.ServiceUnavailableException;
import com.kii.thingif.exception.UnregisteredAliasException;
import com.kii.thingif.internal.utils.JsonUtils;
import com.kii.thingif.query.AggregatedResult;
import com.kii.thingif.query.Aggregation;
import com.kii.thingif.query.GroupedHistoryStatesQuery;
import com.kii.thingif.query.HistoryState;
import com.kii.thingif.query.TimeRange;
import com.kii.thingif.states.AirConditionerState;
import com.kii.thingif.utils.JsonUtil;
import com.squareup.okhttp.mockwebserver.MockResponse;
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RunWith(RobolectricTestRunner.class)
public class AggregateTest extends ThingIFAPITestBase {

    private Context context;

    @Before
    public void before() throws Exception {
        context = RuntimeEnvironment.application.getApplicationContext();
        this.server = new MockWebServer();
        this.server.start();
    }

    @After
    public void after() throws Exception {
        this.server.shutdown();
    }

    @Test
    public void successTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        TimeRange range = new TimeRange(new Date(1), new Date(100));
        EqualsClauseInQuery clause = new EqualsClauseInQuery("dummy", "value");
        GroupedHistoryStatesQuery query = GroupedHistoryStatesQuery.Builder
                .newBuilder(ALIAS1, range)
                .setClause(clause)
                .build();
        Aggregation aggregation = Aggregation.newMaxAggregation("100", Aggregation.FieldType.INTEGER);

        JSONObject requestBody = JsonUtils.newJson(
                "{" +
                    "\"query\":{" +
                        "\"clause\":{" +
                            "\"clauses\":[" +
                                "{" +
                                    "\"type\":\"eq\"," +
                                    "\"field\":\"" + clause.getField() + "\"," +
                                    "\"value\":\"" + clause.getValue() + "\"" +
                                "}," +
                                "{" +
                                    "\"type\":\"withinTimeRange\"," +
                                    "\"upperLimit\":" + range.getTo().getTime() + "," +
                                    "\"lowerLimit\":" + range.getFrom().getTime() +
                                "}" +
                            "]," +
                            "\"type\":\"and\"" +
                        "}," +
                        "\"grouped\":true," +
                        "\"aggregations\":[" +
                            "{" +
                                "\"type\":\"" + aggregation.getFunction().name() + "\"," +
                                "\"field\":\"" + aggregation.getField() + "\"," +
                                "\"fieldType\":\"" + aggregation.getFieldType().name() + "\"}" +
                        "]" +
                    "}" +
                "}");

        JSONObject responseBody = JsonUtils.newJson(
            "{" +
                "\"results\":[" +
                    "{" +
                        "\"range\":{" +
                            "\"from\":" + range.getFrom().getTime() + "," +
                            "\"to\":" + range.getTo().getTime() +
                        "}," +
                        "\"value\": 2000," +
                        "\"objects\":[" +
                            "{" +
                                "\"_created\": 50," +
                                "\"power\": true," +
                                "\"currentTemperature\": 25" +
                            "}" +
                        "]" +
                    "}" +
                "]" +
            "}");

        MockResponse response = new MockResponse().setResponseCode(200);
        response.setBody(responseBody.toString());
        this.server.enqueue(response);

        ThingIFAPI api = createDefaultThingIFAPIBuilder(this.context, APP_ID, APP_KEY)
                .setTarget(target)
                .build();

        List<AggregatedResult<Integer, AirConditionerState>> results = api.aggregate(query, aggregation, Integer.class);

        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        AggregatedResult result = results.get(0);
        Assert.assertEquals(range, result.getTimeRange());
        Assert.assertEquals(2000, result.getValue().intValue());
        List<HistoryState<AirConditionerState>> objects = result.getAggregatedObjects();
        Assert.assertNotNull(objects);
        Assert.assertEquals(1, objects.size());
        HistoryState<AirConditionerState> historyState = objects.get(0);
        Assert.assertEquals(50L, historyState.getCreatedAt().getTime());
        AirConditionerState state = historyState.getState();
        Assert.assertTrue(state.power);
        Assert.assertEquals((Integer)25, state.currentTemperature);

        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/states/aliases/" + ALIAS1 + "/query",
                request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.TraitStateQueryRequest+json");
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
        this.assertRequestBody(requestBody, request);
    }

    @Test
    public void successNoClauseTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        TimeRange range = new TimeRange(new Date(1), new Date(100));
        GroupedHistoryStatesQuery query = GroupedHistoryStatesQuery.Builder
                .newBuilder(ALIAS1, range)
                .build();
        Aggregation aggregation = Aggregation.newMaxAggregation("100", Aggregation.FieldType.INTEGER);

        JSONObject requestBody = JsonUtils.newJson(
                "{" +
                    "\"query\":{" +
                        "\"clause\":{" +
                            "\"type\":\"withinTimeRange\"," +
                            "\"upperLimit\":" + range.getTo().getTime() + "," +
                            "\"lowerLimit\":" + range.getFrom().getTime() +
                        "}," +
                        "\"grouped\":true," +
                        "\"aggregations\":[" +
                            "{" +
                                "\"type\":\"" + aggregation.getFunction().name() + "\"," +
                                "\"field\":\"" + aggregation.getField() + "\"," +
                                "\"fieldType\":\"" + aggregation.getFieldType().name() + "\"" +
                            "}" +
                        "]" +
                    "}" +
                "}");

        JSONObject responseBody = JsonUtils.newJson(
                "{" +
                    "\"results\":[" +
                        "{" +
                            "\"range\":{" +
                                "\"from\":" + range.getFrom().getTime() + "," +
                                "\"to\":" + range.getTo().getTime() +
                            "}," +
                            "\"value\": 2000," +
                            "\"objects\":[" +
                                "{" +
                                    "\"_created\": 50," +
                                    "\"power\": true," +
                                    "\"currentTemperature\": 25" +
                                "}" +
                            "]" +
                        "}" +
                    "]" +
                "}");

        MockResponse response = new MockResponse().setResponseCode(200);
        response.setBody(responseBody.toString());
        this.server.enqueue(response);

        ThingIFAPI api = createDefaultThingIFAPIBuilder(this.context, APP_ID, APP_KEY)
                .setTarget(target)
                .build();

        List<AggregatedResult<Integer, AirConditionerState>> results = api.aggregate(query, aggregation, Integer.class);

        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        AggregatedResult result = results.get(0);
        Assert.assertEquals(range, result.getTimeRange());
        Assert.assertEquals(2000, result.getValue().intValue());
        List<HistoryState<AirConditionerState>> objects = result.getAggregatedObjects();
        Assert.assertNotNull(objects);
        Assert.assertEquals(1, objects.size());
        HistoryState<AirConditionerState> historyState = objects.get(0);
        Assert.assertEquals(50L, historyState.getCreatedAt().getTime());
        AirConditionerState state = historyState.getState();
        Assert.assertTrue(state.power);
        Assert.assertEquals((Integer) 25, state.currentTemperature);

        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/states/aliases/" + ALIAS1 + "/query",
                request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.TraitStateQueryRequest+json");
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
        this.assertRequestBody(requestBody, request);
    }

    @Test(expected = IllegalStateException.class)
    public void errorNoTargetTest() throws Exception {
        TimeRange range = new TimeRange(new Date(1), new Date(100));
        EqualsClauseInQuery clause = new EqualsClauseInQuery("dummy", "value");
        GroupedHistoryStatesQuery query = GroupedHistoryStatesQuery.Builder
                .newBuilder(ALIAS1, range)
                .setClause(clause)
                .build();
        Aggregation aggregation = Aggregation.newMaxAggregation("100", Aggregation.FieldType.INTEGER);

        ThingIFAPI api = createDefaultThingIFAPIBuilder(this.context, APP_ID, APP_KEY)
                .build();
        api.aggregate(query, aggregation, Integer.class);
    }

    @Test(expected = UnregisteredAliasException.class)
    public void errorUnknownAliasTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        TimeRange range = new TimeRange(new Date(1), new Date(100));
        EqualsClauseInQuery clause = new EqualsClauseInQuery("dummy", "value");
        GroupedHistoryStatesQuery query = GroupedHistoryStatesQuery.Builder
                .newBuilder("unknown", range)
                .setClause(clause)
                .build();
        Aggregation aggregation = Aggregation.newMaxAggregation("100", Aggregation.FieldType.INTEGER);

        ThingIFAPI api = createDefaultThingIFAPIBuilder(this.context, APP_ID, APP_KEY)
                .setTarget(target)
                .build();
        api.aggregate(query, aggregation, Integer.class);
    }

    @Test
    public void error400Test() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        TimeRange range = new TimeRange(new Date(1), new Date(100));
        EqualsClauseInQuery clause = new EqualsClauseInQuery("dummy", "value");
        GroupedHistoryStatesQuery query = GroupedHistoryStatesQuery.Builder
                .newBuilder(ALIAS1, range)
                .setClause(clause)
                .build();
        Aggregation aggregation = Aggregation.newMaxAggregation("100", Aggregation.FieldType.INTEGER);

        this.addEmptyMockResponse(400);

        ThingIFAPI api = createDefaultThingIFAPIBuilder(this.context, APP_ID, APP_KEY)
                .setTarget(target)
                .build();

        try {
            api.aggregate(query, aggregation, Integer.class);
            Assert.fail("BadRequestException should be thrown");
        } catch (BadRequestException e) {
            // Expected.
        } catch (Exception e) {
            Assert.fail("Unexpected exception: " + e.toString());
        }

        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/states/aliases/" + ALIAS1 + "/query",
                request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.TraitStateQueryRequest+json");
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }

    @Test
    public void error403Test() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        TimeRange range = new TimeRange(new Date(1), new Date(100));
        EqualsClauseInQuery clause = new EqualsClauseInQuery("dummy", "value");
        GroupedHistoryStatesQuery query = GroupedHistoryStatesQuery.Builder
                .newBuilder(ALIAS1, range)
                .setClause(clause)
                .build();
        Aggregation aggregation = Aggregation.newMaxAggregation("100", Aggregation.FieldType.INTEGER);

        this.addEmptyMockResponse(403);

        ThingIFAPI api = createDefaultThingIFAPIBuilder(this.context, APP_ID, APP_KEY)
                .setTarget(target)
                .build();

        try {
            api.aggregate(query, aggregation, Integer.class);
            Assert.fail("ForbiddenException should be thrown");
        } catch (ForbiddenException e) {
            // Expected.
        } catch (Exception e) {
            Assert.fail("Unexpected exception: " + e.toString());
        }

        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/states/aliases/" + ALIAS1 + "/query",
                request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.TraitStateQueryRequest+json");
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }

    @Test
    public void error404Test() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        TimeRange range = new TimeRange(new Date(1), new Date(100));
        EqualsClauseInQuery clause = new EqualsClauseInQuery("dummy", "value");
        GroupedHistoryStatesQuery query = GroupedHistoryStatesQuery.Builder
                .newBuilder(ALIAS1, range)
                .setClause(clause)
                .build();
        Aggregation aggregation = Aggregation.newMaxAggregation("100", Aggregation.FieldType.INTEGER);

        this.addEmptyMockResponse(404);

        ThingIFAPI api = createDefaultThingIFAPIBuilder(this.context, APP_ID, APP_KEY)
                .setTarget(target)
                .build();

        try {
            api.aggregate(query, aggregation, Integer.class);
            Assert.fail("NotFoundException should be thrown");
        } catch (NotFoundException e) {
            // Expected.
        } catch (Exception e) {
            Assert.fail("Unexpected exception: " + e.toString());
        }

        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/states/aliases/" + ALIAS1 + "/query",
                request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.TraitStateQueryRequest+json");
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }

    @Test
    public void error409Test() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        TimeRange range = new TimeRange(new Date(1), new Date(100));
        EqualsClauseInQuery clause = new EqualsClauseInQuery("dummy", "value");
        GroupedHistoryStatesQuery query = GroupedHistoryStatesQuery.Builder
                .newBuilder(ALIAS1, range)
                .setClause(clause)
                .build();
        Aggregation aggregation = Aggregation.newMaxAggregation("100", Aggregation.FieldType.INTEGER);

        this.addEmptyMockResponse(409);

        ThingIFAPI api = createDefaultThingIFAPIBuilder(this.context, APP_ID, APP_KEY)
                .setTarget(target)
                .build();

        try {
            api.aggregate(query, aggregation, Integer.class);
            Assert.fail("ConflictException should be thrown");
        } catch (ConflictException e) {
            // Expected.
        } catch (Exception e) {
            Assert.fail("Unexpected exception: " + e.toString());
        }

        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/states/aliases/" + ALIAS1 + "/query",
                request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.TraitStateQueryRequest+json");
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }

    @Test
    public void error503Test() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        TimeRange range = new TimeRange(new Date(1), new Date(100));
        EqualsClauseInQuery clause = new EqualsClauseInQuery("dummy", "value");
        GroupedHistoryStatesQuery query = GroupedHistoryStatesQuery.Builder
                .newBuilder(ALIAS1, range)
                .setClause(clause)
                .build();
        Aggregation aggregation = Aggregation.newMaxAggregation("100", Aggregation.FieldType.INTEGER);

        this.addEmptyMockResponse(503);

        ThingIFAPI api = createDefaultThingIFAPIBuilder(this.context, APP_ID, APP_KEY)
                .setTarget(target)
                .build();

        try {
            api.aggregate(query, aggregation, Integer.class);
            Assert.fail("ServiceUnavailableException should be thrown");
        } catch (ServiceUnavailableException e) {
            // Expected.
        } catch (Exception e) {
            Assert.fail("Unexpected exception: " + e.toString());
        }

        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/states/aliases/" + ALIAS1 + "/query",
                request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.TraitStateQueryRequest+json");
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
}
