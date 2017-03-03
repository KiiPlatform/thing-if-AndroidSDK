package com.kii.thingif.thingifapi;

import android.content.Context;
import android.util.Pair;

import com.kii.thingif.StandaloneThing;
import com.kii.thingif.Target;
import com.kii.thingif.TargetState;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.ThingIFAPITestBase;
import com.kii.thingif.clause.query.AllClause;
import com.kii.thingif.exception.UnregisteredAliasException;
import com.kii.thingif.query.HistoryState;
import com.kii.thingif.query.HistoryStatesQuery;
import com.kii.thingif.states.AirConditionerState;
import com.kii.thingif.states.HumidityState;
import com.kii.thingif.thingifapi.utils.ThingIFAPIUtils;
import com.kii.thingif.utils.JsonUtil;
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

        Pair<List<HistoryState<AirConditionerState>>, String> result = api.query(query);

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
        api.query(query);
    }

    @Test(expected = IllegalArgumentException.class)
    public void query_with_nullQuery_IllegalArgumentException_should_thrown_Test() throws Exception{
        Target target = new StandaloneThing("thing1", "vendor-thing-1", "dummyToken");
        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);
        api.query((HistoryStatesQuery) null);
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
        api.query(query);
    }
}
