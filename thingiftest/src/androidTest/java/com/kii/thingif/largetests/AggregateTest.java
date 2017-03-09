package com.kii.thingif.largetests;

import android.support.test.runner.AndroidJUnit4;

import com.kii.thingif.OnboardWithVendorThingIDOptions;
import com.kii.thingif.Target;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.TypedID;
import com.kii.thingif.clause.query.AllClause;
import com.kii.thingif.query.AggregatedResult;
import com.kii.thingif.query.Aggregation;
import com.kii.thingif.query.GroupedHistoryStatesQuery;
import com.kii.thingif.query.TimeRange;
import com.kii.thingif.states.AirConditionerState;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RunWith(AndroidJUnit4.class)
public class AggregateTest extends LargeTestCaseBase {

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
    public void successQueryEmptyResultsTest() throws Exception {
        TimeRange range = new TimeRange(new Date(1), new Date());
        GroupedHistoryStatesQuery query = GroupedHistoryStatesQuery.Builder
                .newBuilder(ALIAS1, range)
                .setClause(new AllClause())
                .build();
        Aggregation aggregation = Aggregation.newCountAggregation(
                "power",
                Aggregation.FieldType.BOOLEAN);

        List<AggregatedResult<Integer, AirConditionerState>> results = this.onboardedApi.aggregate(query, aggregation, Integer.class);

        Assert.assertNotNull(results);
        Assert.assertEquals(0, results.size());
    }

    @Test
    public void successCountTest() throws Exception {
        AirConditionerState[] airStates = {
                new AirConditionerState(true, 23),
                new AirConditionerState(true, 24),
                new AirConditionerState(true, 25),
                new AirConditionerState(true, 26)
        };

        // update 4 states
        for (AirConditionerState state : airStates) {
            updateTargetState(this.onboardedApi.getTarget(), new AirConditionerState[]{state});
            Thread.sleep(1000);
        }

        Date now = new Date();
        TimeRange range = new TimeRange(now, now);
        GroupedHistoryStatesQuery query = GroupedHistoryStatesQuery.Builder
                .newBuilder(ALIAS1, range)
                .setClause(new AllClause())
                .build();
        Aggregation aggregation = Aggregation.newCountAggregation(
                "currentTemperature",
                Aggregation.FieldType.INTEGER);

        List<AggregatedResult<Integer, AirConditionerState>> results = this.onboardedApi.aggregate(query, aggregation, Integer.class);

        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        AggregatedResult result = results.get(0);
        Assert.assertNotNull(result);
        TimeRange responseRange = result.getTimeRange();
        Assert.assertTrue(range.getFrom().getTime() >= responseRange.getFrom().getTime());
        Assert.assertTrue(range.getTo().getTime() <= responseRange.getTo().getTime());
        Assert.assertEquals(4, result.getValue());
        Assert.assertNull(result.getAggregatedObjects());
    }

    @Test
    public void successMaxTest() throws Exception {
        AirConditionerState[] airStates = {
                new AirConditionerState(true, 23),
                new AirConditionerState(true, 24),
                new AirConditionerState(true, 25),
                new AirConditionerState(true, 26)
        };

        // update 4 states
        for (AirConditionerState state : airStates) {
            updateTargetState(this.onboardedApi.getTarget(), new AirConditionerState[]{state});
            Thread.sleep(1000);
        }

        Date now = new Date();
        TimeRange range = new TimeRange(now, now);
        GroupedHistoryStatesQuery query = GroupedHistoryStatesQuery.Builder
                .newBuilder(ALIAS1, range)
                .setClause(new AllClause())
                .build();
        Aggregation aggregation = Aggregation.newMaxAggregation(
                "currentTemperature",
                Aggregation.FieldType.INTEGER);

        List<AggregatedResult<Integer, AirConditionerState>> results = this.onboardedApi.aggregate(query, aggregation, Integer.class);

        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        AggregatedResult result = results.get(0);
        Assert.assertNotNull(result);
        TimeRange responseRange = result.getTimeRange();
        Assert.assertTrue(range.getFrom().getTime() >= responseRange.getFrom().getTime());
        Assert.assertTrue(range.getTo().getTime() <= responseRange.getTo().getTime());
        Assert.assertEquals(26, result.getValue());
        Assert.assertNull(result.getAggregatedObjects());
    }
    @Test
    public void successMinTest() throws Exception {
        AirConditionerState[] airStates = {
                new AirConditionerState(true, 23),
                new AirConditionerState(true, 24),
                new AirConditionerState(true, 25),
                new AirConditionerState(true, 26)
        };

        // update 4 states
        for (AirConditionerState state : airStates) {
            updateTargetState(this.onboardedApi.getTarget(), new AirConditionerState[]{state});
            Thread.sleep(1000);
        }

        Date now = new Date();
        TimeRange range = new TimeRange(now, now);
        GroupedHistoryStatesQuery query = GroupedHistoryStatesQuery.Builder
                .newBuilder(ALIAS1, range)
                .setClause(new AllClause())
                .build();
        Aggregation aggregation = Aggregation.newMinAggregation(
                "currentTemperature",
                Aggregation.FieldType.INTEGER);

        List<AggregatedResult<Integer, AirConditionerState>> results = this.onboardedApi.aggregate(query, aggregation, Integer.class);

        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        AggregatedResult result = results.get(0);
        Assert.assertNotNull(result);
        TimeRange responseRange = result.getTimeRange();
        Assert.assertTrue(range.getFrom().getTime() >= responseRange.getFrom().getTime());
        Assert.assertTrue(range.getTo().getTime() <= responseRange.getTo().getTime());
        Assert.assertEquals(23, result.getValue());
        Assert.assertNull(result.getAggregatedObjects());
    }
    @Test
    public void successMeanTest() throws Exception {
        AirConditionerState[] airStates = {
                new AirConditionerState(true, 22),
                new AirConditionerState(true, 24),
                new AirConditionerState(true, 26),
                new AirConditionerState(true, 28)
        };

        // update 4 states
        for (AirConditionerState state : airStates) {
            updateTargetState(this.onboardedApi.getTarget(), new AirConditionerState[]{state});
            Thread.sleep(1000);
        }

        Date now = new Date();
        TimeRange range = new TimeRange(now, now);
        GroupedHistoryStatesQuery query = GroupedHistoryStatesQuery.Builder
                .newBuilder(ALIAS1, range)
                .setClause(new AllClause())
                .build();
        Aggregation aggregation = Aggregation.newMeanAggregation(
                "currentTemperature",
                Aggregation.FieldType.INTEGER);

        List<AggregatedResult<Integer, AirConditionerState>> results = this.onboardedApi.aggregate(query, aggregation, Integer.class);

        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        AggregatedResult result = results.get(0);
        Assert.assertNotNull(result);
        TimeRange responseRange = result.getTimeRange();
        Assert.assertTrue(range.getFrom().getTime() >= responseRange.getFrom().getTime());
        Assert.assertTrue(range.getTo().getTime() <= responseRange.getTo().getTime());
        Assert.assertEquals(25, result.getValue());
        Assert.assertNull(result.getAggregatedObjects());
    }

    @Test
    public void successSumTest() throws Exception {
        AirConditionerState[] airStates = {
                new AirConditionerState(true, 23),
                new AirConditionerState(true, 24),
                new AirConditionerState(true, 25),
                new AirConditionerState(true, 26)
        };

        // update 4 states
        for (AirConditionerState state : airStates) {
            updateTargetState(this.onboardedApi.getTarget(), new AirConditionerState[]{state});
            Thread.sleep(1000);
        }

        Date now = new Date();
        TimeRange range = new TimeRange(now, now);
        GroupedHistoryStatesQuery query = GroupedHistoryStatesQuery.Builder
                .newBuilder(ALIAS1, range)
                .setClause(new AllClause())
                .build();
        Aggregation aggregation = Aggregation.newSumAggregation(
                "currentTemperature",
                Aggregation.FieldType.INTEGER);

        List<AggregatedResult<Integer, AirConditionerState>> results = this.onboardedApi.aggregate(query, aggregation, Integer.class);

        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        AggregatedResult result = results.get(0);
        Assert.assertNotNull(result);
        TimeRange responseRange = result.getTimeRange();
        Assert.assertTrue(range.getFrom().getTime() >= responseRange.getFrom().getTime());
        Assert.assertTrue(range.getTo().getTime() <= responseRange.getTo().getTime());
        Assert.assertEquals(98, result.getValue());
        Assert.assertNull(result.getAggregatedObjects());
    }
}
