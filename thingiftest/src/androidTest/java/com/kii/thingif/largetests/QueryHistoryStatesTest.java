package com.kii.thingif.largetests;

import android.support.test.runner.AndroidJUnit4;
import android.util.Pair;

import com.kii.thingif.OnboardWithVendorThingIDOptions;
import com.kii.thingif.Target;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.TypedID;
import com.kii.thingif.clause.query.AllClause;
import com.kii.thingif.clause.query.RangeClauseInQuery;
import com.kii.thingif.exception.NotFoundException;
import com.kii.thingif.query.HistoryState;
import com.kii.thingif.query.HistoryStatesQuery;
import com.kii.thingif.states.AirConditionerState;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.UUID;

@RunWith(AndroidJUnit4.class)
public class QueryHistoryStatesTest extends LargeTestCaseBase {

    private ThingIFAPI onboardedApi;
    @Before
    public void before() throws Exception{
        super.before();
        this.onboardedApi = this.createDefaultThingIFAPI();
        String vendorThingID = UUID.randomUUID().toString();
        String thingPassword = "password";
        // on-boarding thing
        OnboardWithVendorThingIDOptions options =
                new OnboardWithVendorThingIDOptions.Builder()
                        .setThingType(DEFAULT_THING_TYPE)
                        .setFirmwareVersion(DEFAULT_FIRMWARE_VERSION).build();
        Target target = this.onboardedApi.onboardWithVendorThingID(vendorThingID, thingPassword, options);
        Assert.assertEquals(TypedID.Types.THING, target.getTypedID().getType());
        Assert.assertNotNull(target.getAccessToken());
    }

    @Test
    public void successQueryEmptyResults_thingNeverUpdate_Test() throws Exception {
        HistoryStatesQuery query =
                HistoryStatesQuery.Builder.newBuilder(ALIAS1, new AllClause()).build();
        Pair<List<HistoryState<AirConditionerState>>, String > result =
                this.onboardedApi.query(query);

        Assert.assertNull(result.second);
        Assert.assertEquals(0, result.first.size());
    }

    @Test
    public void successQuery_thingUpdatedStates_Test() throws Exception{
        AirConditionerState[] airStates = {
                new AirConditionerState(true, 23),
                new AirConditionerState(true, 24),
                new AirConditionerState(true, 25),
                new AirConditionerState(true, 26)
        };

        // update 4 states
        for (AirConditionerState state: airStates) {
            updateTargetState(this.onboardedApi.getTarget(), new AirConditionerState[]{state});
            Thread.sleep(1000);
        }

        // all query
        HistoryStatesQuery query1 =
                HistoryStatesQuery.Builder.newBuilder(ALIAS1, new AllClause()).build();
        Pair<List<HistoryState<AirConditionerState>>, String> result1 =
                this.onboardedApi.query(query1);

        Assert.assertEquals(4, result1.first.size());
        Assert.assertNull(result1.second);

        // query with empty result returned
        HistoryStatesQuery query2 =
                HistoryStatesQuery.Builder.newBuilder(
                        ALIAS1,
                        RangeClauseInQuery.greaterThan("currentTemperature", 30))
                        .setFirmwareVersion("v1").build();
        Pair<List<HistoryState<AirConditionerState>>, String> result2 =
                this.onboardedApi.query(query2);
        Assert.assertEquals(0, result2.first.size());
        Assert.assertNull(result2.second);

        // query with bestEffortLimit
        HistoryStatesQuery query3 =
                HistoryStatesQuery.Builder
                        .newBuilder(
                            ALIAS1,
                            new AllClause())
                        .setBestEffortLimit(3)
                        .setFirmwareVersion("v1").build();
        Pair<List<HistoryState<AirConditionerState>>, String> result3 =
                this.onboardedApi.query(query3);
        Assert.assertEquals(3, result3.first.size());
        Assert.assertNotNull(result3.second);

        // query with pagination key
        HistoryStatesQuery query4 =
                HistoryStatesQuery.Builder
                        .newBuilder(
                                ALIAS1,
                                new AllClause())
                        .setNextPaginationKey(result3.second).build();
        Pair<List<HistoryState<AirConditionerState>>, String> result4 =
                this.onboardedApi.query(query4);
        Assert.assertEquals(1, result4.first.size());
        Assert.assertNull(result4.second);

        // update thing to new version, in v3, AirConditionerAlias is not defined.
        this.onboardedApi.updateFirmwareVersion("v3");

        // query with older firmwareVersion, where AirConditionerAlias was defined.
        HistoryStatesQuery query5 =
                HistoryStatesQuery.Builder
                        .newBuilder(
                                ALIAS1,
                                new AllClause()).setFirmwareVersion("v1").build();
        Pair<List<HistoryState<AirConditionerState>>, String> result5 =
                this.onboardedApi.query(query5);
        Assert.assertEquals(4, result5.first.size());
        Assert.assertNull(result5.second);
    }

    @Test
    public void failed_query_with_not_defined_alias_404Error() throws Exception{
        // update thing to new version, in v3, AirConditionerAlias is not defined.
        this.onboardedApi.updateFirmwareVersion("v3");

        HistoryStatesQuery query =
                HistoryStatesQuery.Builder
                        .newBuilder(
                                ALIAS1,
                                new AllClause()).build();
        try {
            this.onboardedApi.query(query);
            Assert.fail("should fail");
        }catch (NotFoundException e) {
            Assert.assertEquals("TRAIT_ALIAS_NOT_FOUND", e.getErrorCode());
            Assert.assertNotNull(e.getMessage());
            Assert.assertEquals(404, e.getStatusCode());
        }
    }
}
