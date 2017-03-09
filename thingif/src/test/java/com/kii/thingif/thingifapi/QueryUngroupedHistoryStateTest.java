package com.kii.thingif.thingifapi;

import android.content.Context;
import android.util.Pair;

import com.kii.thingif.StandaloneThing;
import com.kii.thingif.Target;
import com.kii.thingif.TargetState;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.ThingIFAPITestBase;
import com.kii.thingif.clause.query.AllClause;
import com.kii.thingif.exception.BadRequestException;
import com.kii.thingif.exception.ConflictException;
import com.kii.thingif.exception.ForbiddenException;
import com.kii.thingif.exception.NotFoundException;
import com.kii.thingif.exception.ServiceUnavailableException;
import com.kii.thingif.exception.UnregisteredAliasException;
import com.kii.thingif.query.HistoryState;
import com.kii.thingif.query.HistoryStatesQuery;
import com.kii.thingif.states.AirConditionerState;
import com.kii.thingif.states.HumidityState;
import com.kii.thingif.thingifapi.utils.ThingIFAPIUtils;
import com.kii.thingif.utils.JsonUtil;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RunWith(RobolectricTestRunner.class)
public class QueryUngroupedHistoryStateTest extends ThingIFAPITestBase {
    private Context context;
    private ThingIFAPI defaultApi;

    @Before
    public void before() throws Exception {
        context = RuntimeEnvironment.application.getApplicationContext();
        this.server = new MockWebServer();
        this.server.start();

        Target target = new StandaloneThing("thing1", "vendor-thing-1", "dummyToken");
        this.defaultApi = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(defaultApi, target);
    }

    @After
    public void after() throws Exception {
        this.server.shutdown();
    }

    private HistoryStatesQuery getDefaultQuery() {
        return HistoryStatesQuery.Builder
                .newBuilder(ALIAS1, new AllClause())
                .setBestEffortLimit(5)
                .setNextPaginationKey("100/2")
                .setFirmwareVersion("v1")
                .build();
    }

    @Test
    public void baseTest() throws Exception{
        Target target = new StandaloneThing("thing1", "vendor-thing-1", "dummyToken");

        List<HistoryState<? extends TargetState>> expectedStates = new ArrayList<>();
        expectedStates.add(new HistoryState<>(new AirConditionerState(true, 23), new Date()));
        expectedStates.add(new HistoryState<>(new AirConditionerState(true, 12), new Date()));
        expectedStates.add(new HistoryState<>(new AirConditionerState(false, 13), new Date()));

        addMockResponseForQueryUngroupedHistoryState(200, expectedStates, "100/3");

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);

        HistoryStatesQuery query = HistoryStatesQuery.Builder
                .newBuilder(ALIAS1, new AllClause())
                .setBestEffortLimit(5)
                .setNextPaginationKey("100/2")
                .setFirmwareVersion("v1")
                .build();

        Pair<List<HistoryState<AirConditionerState>>, String> result = api.query(query, AirConditionerState.class);

        // verify result
        Assert.assertEquals("100/3", result.second);
        Assert.assertEquals(expectedStates.size(), result.first.size());

        for (int i=0; i<expectedStates.size(); i++) {
            HistoryState expectedHistoryState = expectedStates.get(i);
            HistoryState<AirConditionerState> actualHistoryState = result.first.get(i);
            Assert.assertTrue(expectedHistoryState.getState().equals(actualHistoryState.getState()));
            Assert.assertTrue(expectedHistoryState.getCreatedAt().equals(actualHistoryState.getCreatedAt()));
        }

        // verify request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        junit.framework.Assert.assertEquals(
                MessageFormat.format("{0}/targets/{1}/states/aliases/{2}/query",
                        BASE_PATH,
                        target.getTypedID().toString(),
                        ALIAS1),
                request1.getPath());
        junit.framework.Assert.assertEquals("POST", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/vnd.kii.TraitStateQueryRequest+json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put(
                "query",
                new JSONObject()
                        .put("clause", JsonUtil.queryClauseToJson(new AllClause())));
        expectedRequestBody.put("firmwareVersion", "v1");
        expectedRequestBody.put("paginationKey", "100/2");
        expectedRequestBody.put("bestEffortLimit", 5);
        this.assertRequestBody(expectedRequestBody, request1);
    }

    @Test
    public void query_ungrouped_historyStates_emptyResults_response_Test() throws Exception{
        Target target = this.defaultApi.getTarget();
        Assert.assertNotNull(target);
        HistoryStatesQuery query = getDefaultQuery();

        List<HistoryState<? extends TargetState>> expectedStates = new ArrayList<>();

        addMockResponseForQueryUngroupedHistoryState(200, expectedStates, null);

        Pair<List<HistoryState<AirConditionerState>>, String> result =
                this.defaultApi.query(query, AirConditionerState.class);

        // verify result
        Assert.assertNull(result.second);
        Assert.assertEquals(0, result.first.size());

        // verify request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        junit.framework.Assert.assertEquals(
                MessageFormat.format("{0}/targets/{1}/states/aliases/{2}/query",
                        BASE_PATH,
                        target.getTypedID().toString(),
                        ALIAS1),
                request1.getPath());
        junit.framework.Assert.assertEquals("POST", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/vnd.kii.TraitStateQueryRequest+json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put(
                "query",
                new JSONObject()
                        .put("clause", JsonUtil.queryClauseToJson(new AllClause())));
        expectedRequestBody.putOpt("firmwareVersion", query.getFirmwareVersion());
        expectedRequestBody.put("paginationKey", query.getNextPaginationKey());
        expectedRequestBody.put("bestEffortLimit", query.getBestEffortLimit());
        this.assertRequestBody(expectedRequestBody, request1);
    }

    @Test(expected = UnregisteredAliasException.class)
    public void query_with_unregisteredAlias_UnregisterAliasException_should_thrown_Test() throws Exception{
        Target target = new StandaloneThing("thing1", "vendor-thing-1", "dummyToken");
        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);

        HistoryStatesQuery query = HistoryStatesQuery.Builder
                .newBuilder("NewAlias", new AllClause())
                .setBestEffortLimit(5)
                .setNextPaginationKey("100/2")
                .setFirmwareVersion("v1")
                .build();
        api.query(query, AirConditionerState.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void query_with_nullQuery_IllegalArgumentException_should_thrown_Test() throws Exception{
        Target target = new StandaloneThing("thing1", "vendor-thing-1", "dummyToken");
        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);
        api.query((HistoryStatesQuery) null, AirConditionerState.class);
    }

    @Test(expected = IllegalStateException.class)
    public void query_with_nullTarget_IllegalStateException_should_thrown_Test() throws Exception {
        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        HistoryStatesQuery query = HistoryStatesQuery.Builder
                .newBuilder("NewAlias", new AllClause())
                .setBestEffortLimit(5)
                .setNextPaginationKey("100/2")
                .setFirmwareVersion("v1")
                .build();
        api.query(query, AirConditionerState.class);
    }

    @Test
    public void query_ungrouped_historyStates_400ErrorTest() throws Exception {
        HistoryStatesQuery query = getDefaultQuery();
        Target target = this.defaultApi.getTarget();
        Assert.assertNotNull(target);

        this.addEmptyMockResponse(400);
        try {
            this.defaultApi.query(query, AirConditionerState.class);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (BadRequestException e) {
        }
        // verify the request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        junit.framework.Assert.assertEquals(
                MessageFormat.format("{0}/targets/{1}/states/aliases/{2}/query",
                        BASE_PATH,
                        target.getTypedID().toString(),
                        ALIAS1),
                request1.getPath());
        Assert.assertEquals("POST", request1.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.TraitStateQueryRequest+json");
        this.assertRequestHeader(expectedRequestHeaders, request1);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put(
                "query",
                new JSONObject()
                        .put("clause", JsonUtil.queryClauseToJson(new AllClause())));
        expectedRequestBody.putOpt("firmwareVersion", query.getFirmwareVersion());
        expectedRequestBody.putOpt("paginationKey", query.getNextPaginationKey());
        expectedRequestBody.putOpt("bestEffortLimit", query.getBestEffortLimit());
        this.assertRequestBody(expectedRequestBody, request1);
    }
    @Test
    public void query_ungrouped_historyStates_403ErrorTest() throws Exception {
        HistoryStatesQuery query = getDefaultQuery();
        Target target = this.defaultApi.getTarget();
        Assert.assertNotNull(target);

        this.addEmptyMockResponse(403);
        try {
            this.defaultApi.query(query, AirConditionerState.class);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (ForbiddenException e) {
        }
        // verify the request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        junit.framework.Assert.assertEquals(
                MessageFormat.format("{0}/targets/{1}/states/aliases/{2}/query",
                        BASE_PATH,
                        target.getTypedID().toString(),
                        ALIAS1),
                request1.getPath());
        Assert.assertEquals("POST", request1.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.TraitStateQueryRequest+json");
        this.assertRequestHeader(expectedRequestHeaders, request1);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put(
                "query",
                new JSONObject()
                        .put("clause", JsonUtil.queryClauseToJson(new AllClause())));
        expectedRequestBody.putOpt("firmwareVersion", query.getFirmwareVersion());
        expectedRequestBody.putOpt("paginationKey", query.getNextPaginationKey());
        expectedRequestBody.putOpt("bestEffortLimit", query.getBestEffortLimit());
        this.assertRequestBody(expectedRequestBody, request1);
    }
    @Test
    public void query_ungrouped_historyStates_404ErrorTest() throws Exception {
        HistoryStatesQuery query = getDefaultQuery();
        Target target = this.defaultApi.getTarget();
        Assert.assertNotNull(target);

        this.addEmptyMockResponse(404);
        try {
            this.defaultApi.query(query, AirConditionerState.class);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (NotFoundException e) {
        }
        // verify the request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        junit.framework.Assert.assertEquals(
                MessageFormat.format("{0}/targets/{1}/states/aliases/{2}/query",
                        BASE_PATH,
                        target.getTypedID().toString(),
                        ALIAS1),
                request1.getPath());
        Assert.assertEquals("POST", request1.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.TraitStateQueryRequest+json");
        this.assertRequestHeader(expectedRequestHeaders, request1);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put(
                "query",
                new JSONObject()
                        .put("clause", JsonUtil.queryClauseToJson(new AllClause())));
        expectedRequestBody.putOpt("firmwareVersion", query.getFirmwareVersion());
        expectedRequestBody.putOpt("paginationKey", query.getNextPaginationKey());
        expectedRequestBody.putOpt("bestEffortLimit", query.getBestEffortLimit());
        this.assertRequestBody(expectedRequestBody, request1);
    }
    @Test
    public void query_ungrouped_historyStates_503ErrorTest() throws Exception {

        HistoryStatesQuery query = getDefaultQuery();
        Target target = this.defaultApi.getTarget();
        Assert.assertNotNull(target);

        this.addEmptyMockResponse(503);
        try {
            this.defaultApi.query(query, AirConditionerState.class);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (ServiceUnavailableException e) {
        }
        // verify the request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        junit.framework.Assert.assertEquals(
                MessageFormat.format("{0}/targets/{1}/states/aliases/{2}/query",
                        BASE_PATH,
                        target.getTypedID().toString(),
                        ALIAS1),
                request1.getPath());
        Assert.assertEquals("POST", request1.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.TraitStateQueryRequest+json");
        this.assertRequestHeader(expectedRequestHeaders, request1);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put(
                "query",
                new JSONObject()
                        .put("clause", JsonUtil.queryClauseToJson(new AllClause())));
        expectedRequestBody.putOpt("firmwareVersion", query.getFirmwareVersion());
        expectedRequestBody.putOpt("paginationKey", query.getNextPaginationKey());
        expectedRequestBody.putOpt("bestEffortLimit", query.getBestEffortLimit());
        this.assertRequestBody(expectedRequestBody, request1);
    }

    @Test
    public void query_ungrouped_historyStates_409ErrorTest() throws Exception {
        HistoryStatesQuery query = getDefaultQuery();
        Target target = this.defaultApi.getTarget();
        Assert.assertNotNull(target);

        this.addEmptyMockResponse(409);
        try {
            this.defaultApi.query(query, AirConditionerState.class);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (ConflictException e) {
        }
        // verify the request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        junit.framework.Assert.assertEquals(
                MessageFormat.format("{0}/targets/{1}/states/aliases/{2}/query",
                        BASE_PATH,
                        target.getTypedID().toString(),
                        ALIAS1),
                request1.getPath());
        Assert.assertEquals("POST", request1.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.TraitStateQueryRequest+json");
        this.assertRequestHeader(expectedRequestHeaders, request1);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put(
                "query",
                new JSONObject()
                        .put("clause", JsonUtil.queryClauseToJson(new AllClause())));
        expectedRequestBody.putOpt("firmwareVersion", query.getFirmwareVersion());
        expectedRequestBody.putOpt("paginationKey", query.getNextPaginationKey());
        expectedRequestBody.putOpt("bestEffortLimit", query.getBestEffortLimit());
        this.assertRequestBody(expectedRequestBody, request1);
    }

    @Test
    public void query_ungrouped_historyStates_noStatesInServer_409ErrorTest() throws Exception {
        HistoryStatesQuery query = getDefaultQuery();
        Target target = this.defaultApi.getTarget();
        Assert.assertNotNull(target);

        MockResponse response = new MockResponse();
        response.setResponseCode(409);
        String responseBody =
                "{\n" +
                "  \"errorCode\": \"STATE_HISTORY_NOT_AVAILABLE\",\n" +
                "  \"message\": \"Time series bucket does not exist\"\n" +
                "}";
        response.setBody(responseBody);
        this.server.enqueue(response);

        Pair<List<HistoryState<AirConditionerState>>, String> results =
                this.defaultApi.query(query, AirConditionerState.class);
        Assert.assertEquals(0, results.first.size());
        Assert.assertNull(results.second);

        // verify the request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        junit.framework.Assert.assertEquals(
                MessageFormat.format("{0}/targets/{1}/states/aliases/{2}/query",
                        BASE_PATH,
                        target.getTypedID().toString(),
                        ALIAS1),
                request1.getPath());
        Assert.assertEquals("POST", request1.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + this.defaultApi.getOwner().getAccessToken());
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.TraitStateQueryRequest+json");
        this.assertRequestHeader(expectedRequestHeaders, request1);

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put(
                "query",
                new JSONObject()
                        .put("clause", JsonUtil.queryClauseToJson(new AllClause())));
        expectedRequestBody.putOpt("firmwareVersion", query.getFirmwareVersion());
        expectedRequestBody.putOpt("paginationKey", query.getNextPaginationKey());
        expectedRequestBody.putOpt("bestEffortLimit", query.getBestEffortLimit());
        this.assertRequestBody(expectedRequestBody, request1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void query_with_nullTargetStateClass_IllegalArgumentException_should_thrown_Test() throws Exception{
        Target target = new StandaloneThing("thing1", "vendor-thing-1", "dummyToken");
        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);

        HistoryStatesQuery query = HistoryStatesQuery.Builder
                .newBuilder(ALIAS1, new AllClause())
                .setBestEffortLimit(5)
                .setNextPaginationKey("100/2")
                .setFirmwareVersion("v1")
                .build();
        api.query(query, null);
    }

    @Test(expected = ClassCastException.class)
    public void query_with_diffTargetStateClass_ClassCastException_should_thrown_Test() throws Exception{
        Target target = new StandaloneThing("thing1", "vendor-thing-1", "dummyToken");
        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);

        HistoryStatesQuery query = HistoryStatesQuery.Builder
                .newBuilder(ALIAS1, new AllClause())
                .setBestEffortLimit(5)
                .setNextPaginationKey("100/2")
                .setFirmwareVersion("v1")
                .build();
        api.query(query, HumidityState.class);
    }
}
