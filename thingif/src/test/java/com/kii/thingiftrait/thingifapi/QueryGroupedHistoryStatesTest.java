package com.kii.thingiftrait.thingifapi;

import android.content.Context;

import com.kii.thingiftrait.StandaloneThing;
import com.kii.thingiftrait.Target;
import com.kii.thingiftrait.TargetState;
import com.kii.thingiftrait.ThingIFAPI;
import com.kii.thingiftrait.ThingIFAPITestBase;
import com.kii.thingiftrait.TypedID;
import com.kii.thingiftrait.clause.query.EqualsClauseInQuery;
import com.kii.thingiftrait.exception.BadRequestException;
import com.kii.thingiftrait.exception.ConflictException;
import com.kii.thingiftrait.exception.ForbiddenException;
import com.kii.thingiftrait.exception.NotFoundException;
import com.kii.thingiftrait.exception.ServiceUnavailableException;
import com.kii.thingiftrait.exception.UnregisteredAliasException;
import com.kii.thingiftrait.query.GroupedHistoryStates;
import com.kii.thingiftrait.query.GroupedHistoryStatesQuery;
import com.kii.thingiftrait.query.HistoryState;
import com.kii.thingiftrait.query.TimeRange;
import com.kii.thingiftrait.states.AirConditionerState;
import com.kii.thingiftrait.states.HumidityState;
import com.kii.thingiftrait.utils.JsonUtil;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RunWith(RobolectricTestRunner.class)
public class QueryGroupedHistoryStatesTest extends ThingIFAPITestBase {
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
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        TimeRange range = new TimeRange(new Date(1), new Date(100));
        EqualsClauseInQuery clause = new EqualsClauseInQuery("dummy", "value");
        GroupedHistoryStatesQuery query = GroupedHistoryStatesQuery.Builder
                .newBuilder(ALIAS1, range)
                .setClause(clause)
                .setFirmwareVersion("v1")
                .build();

        List<HistoryState<AirConditionerState>> historyStates1 = new ArrayList<>();
        Date from1 = new Date();
        historyStates1.add(new HistoryState<>(new AirConditionerState(true, 23), new Date()));
        historyStates1.add(new HistoryState<>(new AirConditionerState(false, 24), new Date()));
        historyStates1.add(new HistoryState<>(new AirConditionerState(true, 25), new Date()));
        historyStates1.add(new HistoryState<>(new AirConditionerState(false, 26), new Date()));
        TimeRange range1 = new TimeRange(from1, new Date());
        GroupedHistoryStates<AirConditionerState> groupedStates1 =
                new GroupedHistoryStates<>(range1, historyStates1);

        List<HistoryState<AirConditionerState>> historyStates2 = new ArrayList<>();
        Date from2 = new Date();
        TimeRange range2 = new TimeRange(from2, new Date());
        GroupedHistoryStates<AirConditionerState> groupedStates2 =
                new GroupedHistoryStates<>(range2, historyStates2);

        List<GroupedHistoryStates<? extends TargetState>> expectedGroupedStatesArray = new ArrayList<>();
        expectedGroupedStatesArray.add(groupedStates1);
        expectedGroupedStatesArray.add(groupedStates2);

        this.addMockResponseForQueryGroupedHistoryStates(200, expectedGroupedStatesArray);

        ThingIFAPI api = createDefaultThingIFAPIBuilder(this.context, APP_ID, APP_KEY)
                .setTarget(target)
                .build();

        List<GroupedHistoryStates<AirConditionerState>> results = api.query(
                query,
                AirConditionerState.class);

        Assert.assertNotNull(results);
        Assert.assertEquals(expectedGroupedStatesArray.size(), results.size());
        for (int i=0; i<expectedGroupedStatesArray.size(); i++) {
            GroupedHistoryStates expectedGroupedState = expectedGroupedStatesArray.get(i);
            GroupedHistoryStates actualGroupedState = results.get(i);
            assertSameGroupedHistoryStates(
                    "failed on ["+i+"]",
                    expectedGroupedState,
                    actualGroupedState);
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
        JSONObject requestBody = new JSONObject();
        JSONObject queryJson = new JSONObject();
        queryJson.put("clause", new JSONObject()
                        .put("type", "and")
                        .put("clauses", new JSONArray()
                                .put(JsonUtil.queryClauseToJson(clause))
                                .put(JsonUtil.timeRangeToClause(range))))
                .put("grouped", true);
        requestBody.put("query", queryJson);
        requestBody.put("firmwareVersion", "v1");
        this.assertRequestBody(requestBody, request);
    }

    @Test
    public void successNoClauseTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        TimeRange range = new TimeRange(new Date(1), new Date(100));
        GroupedHistoryStatesQuery query = GroupedHistoryStatesQuery.Builder
                .newBuilder(ALIAS1, range)
                .build();
        List<HistoryState<AirConditionerState>> historyStates1 = new ArrayList<>();
        Date from1 = new Date();
        historyStates1.add(new HistoryState<>(new AirConditionerState(true, 23), new Date()));
        historyStates1.add(new HistoryState<>(new AirConditionerState(false, 24), new Date()));
        historyStates1.add(new HistoryState<>(new AirConditionerState(true, 25), new Date()));
        historyStates1.add(new HistoryState<>(new AirConditionerState(false, 26), new Date()));
        TimeRange range1 = new TimeRange(from1, new Date());
        GroupedHistoryStates<AirConditionerState> groupedStates1 =
                new GroupedHistoryStates<>(range1, historyStates1);

        List<HistoryState<AirConditionerState>> historyStates2 = new ArrayList<>();
        Date from2 = new Date();
        TimeRange range2 = new TimeRange(from2, new Date());
        GroupedHistoryStates<AirConditionerState> groupedStates2 =
                new GroupedHistoryStates<>(range2, historyStates2);

        List<GroupedHistoryStates<? extends TargetState>> expectedGroupedStatesArray = new ArrayList<>();
        expectedGroupedStatesArray.add(groupedStates1);
        expectedGroupedStatesArray.add(groupedStates2);

        this.addMockResponseForQueryGroupedHistoryStates(200, expectedGroupedStatesArray);

        ThingIFAPI api = createDefaultThingIFAPIBuilder(this.context, APP_ID, APP_KEY)
                .setTarget(target)
                .build();

        List<GroupedHistoryStates<AirConditionerState>> results = api.query(
                query,
                AirConditionerState.class);

        Assert.assertNotNull(results);
        Assert.assertEquals(expectedGroupedStatesArray.size(), results.size());
        for (int i=0; i<expectedGroupedStatesArray.size(); i++) {
            GroupedHistoryStates expectedGroupedState = expectedGroupedStatesArray.get(i);
            GroupedHistoryStates actualGroupedState = results.get(i);
            assertSameGroupedHistoryStates(
                    "failed on ["+i+"]",
                    expectedGroupedState,
                    actualGroupedState);
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
        JSONObject requestBody = new JSONObject();
        JSONObject queryJson = new JSONObject();
        queryJson.put("clause", JsonUtil.timeRangeToClause(range))
                .put("grouped", true);
        requestBody.put("query", queryJson);
        this.assertRequestBody(requestBody, request);
    }

    @Test
    public void successNoStatesInServerTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        TimeRange range = new TimeRange(new Date(1), new Date(100));
        EqualsClauseInQuery clause = new EqualsClauseInQuery("dummy", "value");
        GroupedHistoryStatesQuery query = GroupedHistoryStatesQuery.Builder
                .newBuilder(ALIAS1, range)
                .setClause(clause)
                .build();

        MockResponse response = new MockResponse().setResponseCode(409);
        response.setBody(
                "{" +
                        "\"errorCode\": \"STATE_HISTORY_NOT_AVAILABLE\"," +
                        "\"message\": \"Time series bucket does not exist\"" +
                        "}");
        this.server.enqueue(response);

        ThingIFAPI api = createDefaultThingIFAPIBuilder(this.context, APP_ID, APP_KEY)
                .setTarget(target)
                .build();

        List<GroupedHistoryStates<AirConditionerState>> results =
                api.query(query, AirConditionerState.class);

        Assert.assertNotNull(results);
        Assert.assertEquals(0, results.size());

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

    @Test(expected = IllegalStateException.class)
    public void errorNoTargetTest() throws Exception {
        TimeRange range = new TimeRange(new Date(1), new Date(100));
        EqualsClauseInQuery clause = new EqualsClauseInQuery("dummy", "value");
        GroupedHistoryStatesQuery query = GroupedHistoryStatesQuery.Builder
                .newBuilder(ALIAS1, range)
                .setClause(clause)
                .build();
        ThingIFAPI api = createDefaultThingIFAPIBuilder(this.context, APP_ID, APP_KEY)
                .build();
        api.query(query, AirConditionerState.class);
    }

    @Test(expected = UnregisteredAliasException.class)
    public void errorUnknownAliasTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        TimeRange range = new TimeRange(new Date(1), new Date(100));
        EqualsClauseInQuery clause = new EqualsClauseInQuery("dummy", "value");
        GroupedHistoryStatesQuery query = GroupedHistoryStatesQuery.Builder
                .newBuilder("unknown", range)
                .setClause(clause)
                .build();

        ThingIFAPI api = createDefaultThingIFAPIBuilder(this.context, APP_ID, APP_KEY)
                .setTarget(target)
                .build();
        api.query(query, AirConditionerState.class);
    }

    @Test
    public void error400Test() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        TimeRange range = new TimeRange(new Date(1), new Date(100));
        EqualsClauseInQuery clause = new EqualsClauseInQuery("dummy", "value");
        GroupedHistoryStatesQuery query = GroupedHistoryStatesQuery.Builder
                .newBuilder(ALIAS1, range)
                .setClause(clause)
                .build();

        this.addEmptyMockResponse(400);

        ThingIFAPI api = createDefaultThingIFAPIBuilder(this.context, APP_ID, APP_KEY)
                .setTarget(target)
                .build();

        try {
            api.query(query, AirConditionerState.class);
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
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        TimeRange range = new TimeRange(new Date(1), new Date(100));
        EqualsClauseInQuery clause = new EqualsClauseInQuery("dummy", "value");
        GroupedHistoryStatesQuery query = GroupedHistoryStatesQuery.Builder
                .newBuilder(ALIAS1, range)
                .setClause(clause)
                .build();

        this.addEmptyMockResponse(403);

        ThingIFAPI api = createDefaultThingIFAPIBuilder(this.context, APP_ID, APP_KEY)
                .setTarget(target)
                .build();

        try {
            api.query(query, AirConditionerState.class);
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
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        TimeRange range = new TimeRange(new Date(1), new Date(100));
        EqualsClauseInQuery clause = new EqualsClauseInQuery("dummy", "value");
        GroupedHistoryStatesQuery query = GroupedHistoryStatesQuery.Builder
                .newBuilder(ALIAS1, range)
                .setClause(clause)
                .build();

        this.addEmptyMockResponse(404);

        ThingIFAPI api = createDefaultThingIFAPIBuilder(this.context, APP_ID, APP_KEY)
                .setTarget(target)
                .build();

        try {
            api.query(query, AirConditionerState.class);
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
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        TimeRange range = new TimeRange(new Date(1), new Date(100));
        EqualsClauseInQuery clause = new EqualsClauseInQuery("dummy", "value");
        GroupedHistoryStatesQuery query = GroupedHistoryStatesQuery.Builder
                .newBuilder(ALIAS1, range)
                .setClause(clause)
                .build();

        this.addEmptyMockResponse(409);

        ThingIFAPI api = createDefaultThingIFAPIBuilder(this.context, APP_ID, APP_KEY)
                .setTarget(target)
                .build();

        try {
            api.query(query, AirConditionerState.class);
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
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        TimeRange range = new TimeRange(new Date(1), new Date(100));
        EqualsClauseInQuery clause = new EqualsClauseInQuery("dummy", "value");
        GroupedHistoryStatesQuery query = GroupedHistoryStatesQuery.Builder
                .newBuilder(ALIAS1, range)
                .setClause(clause)
                .build();

        this.addEmptyMockResponse(503);

        ThingIFAPI api = createDefaultThingIFAPIBuilder(this.context, APP_ID, APP_KEY)
                .setTarget(target)
                .build();

        try {
            api.query(query, AirConditionerState.class);
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

    @Test(expected = ClassCastException.class)
    public void errorTargetStateClassCastExceptionTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        TimeRange range = new TimeRange(new Date(1), new Date(100));
        EqualsClauseInQuery clause = new EqualsClauseInQuery("dummy", "value");
        GroupedHistoryStatesQuery query = GroupedHistoryStatesQuery.Builder
                .newBuilder(ALIAS1, range)
                .setClause(clause)
                .build();

        ThingIFAPI api = createDefaultThingIFAPIBuilder(this.context, APP_ID, APP_KEY)
                .setTarget(target)
                .build();
        api.query(query, HumidityState.class);
    }
}
