package com.kii.thing_if.largetests;

import android.support.test.runner.AndroidJUnit4;

import com.kii.thing_if.OnboardWithVendorThingIDOptions;
import com.kii.thing_if.Target;
import com.kii.thing_if.ThingIFAPI;
import com.kii.thing_if.TypedID;
import com.kii.thing_if.clause.query.RangeClauseInQuery;
import com.kii.thing_if.query.GroupedHistoryStates;
import com.kii.thing_if.query.GroupedHistoryStatesQuery;
import com.kii.thing_if.query.HistoryState;
import com.kii.thing_if.query.TimeRange;
import com.kii.thing_if.states.AirConditionerState;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RunWith(AndroidJUnit4.class)
public class QueryGroupedHistoryStatesTest extends LargeTestCaseBase {

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

    @Test(timeout = 120*1000)
    public void query_groupedHistoryStates_Test() throws Exception {

        AirConditionerState[] airStates1 = {
                new AirConditionerState(true, 23),
                new AirConditionerState(true, 24),
                new AirConditionerState(true, 25),
                new AirConditionerState(true, 26)
        };

        AirConditionerState[] airStates2 = {
                new AirConditionerState(true, 17),
                new AirConditionerState(true, 18),
                new AirConditionerState(true, 19),
                new AirConditionerState(true, 20)
        };


        // update first 4 states
        Date start1 = new Date();
        for (AirConditionerState state: airStates1) {
            updateTargetState(this.onboardedApi.getTarget(), new AirConditionerState[]{state});
            Thread.sleep(1000);
        }
        Date end1 = new Date();

        // to next date group
        Thread.sleep(60*1000);
        // update second 4 states
        Date start2 = new Date();
        for (AirConditionerState state: airStates2) {
            updateTargetState(this.onboardedApi.getTarget(), new AirConditionerState[]{state});
            Thread.sleep(1000);
        }
        Date end2 = new Date();

        // query with only time range
        GroupedHistoryStatesQuery query1 =
                GroupedHistoryStatesQuery.Builder
                        .newBuilder(ALIAS1, new TimeRange(start1, end2))
                        .build();
        List<GroupedHistoryStates<AirConditionerState>> result1 =
                onboardedApi.query(query1, AirConditionerState.class);
        Assert.assertTrue(result1.size() >= 2);

        List<HistoryState<AirConditionerState>> expectedStates1 = new ArrayList<>();
        for (AirConditionerState state: airStates1) {
            // createdAt is dummy, will not check it
            expectedStates1.add(new HistoryState<>(state, new Date()));
        }
        for (AirConditionerState state: airStates2) {
            // createdAt is dummy, will not check it
            expectedStates1.add(new HistoryState<>(state, new Date()));
        }

        List<HistoryState<AirConditionerState>> actualStates1 = new ArrayList<>();
        for (GroupedHistoryStates<AirConditionerState> groupedState: result1) {
            for (HistoryState<AirConditionerState> historyState: groupedState.getObjects()) {
                actualStates1.add(historyState);
            }
        }

        Assert.assertEquals(expectedStates1.size(), actualStates1.size());
        for (int i=0; i<expectedStates1.size(); i++) {
            HistoryState<AirConditionerState> expectedState = expectedStates1.get(i);
            HistoryState<AirConditionerState> actualState = actualStates1.get(i);
            Assert.assertEquals(expectedState.getState().power, actualState.getState().power);
            Assert.assertEquals(expectedState.getState().currentTemperature, actualState.getState().currentTemperature);
        }

        // query with clause
        GroupedHistoryStatesQuery query2 =
                GroupedHistoryStatesQuery.Builder
                        .newBuilder(ALIAS1, new TimeRange(start1, end2))
                        .setClause(RangeClauseInQuery.greaterThanOrEqualTo("currentTemperature", 23))
                        .build();
        List<GroupedHistoryStates<AirConditionerState>> result2 =
                onboardedApi.query(query2, AirConditionerState.class);
        Assert.assertTrue(result2.size() >= 1);

        List<HistoryState<AirConditionerState>> expectedStates2 = new ArrayList<>();
        for (AirConditionerState state: airStates1) {
            // createdAt is dummy, will not check it
            expectedStates2.add(new HistoryState<>(state, new Date()));
        }

        List<HistoryState<AirConditionerState>> actualStates2 = new ArrayList<>();
        for (GroupedHistoryStates<AirConditionerState> groupedState: result2) {
            for (HistoryState<AirConditionerState> historyState: groupedState.getObjects()) {
                actualStates2.add(historyState);
            }
        }

        Assert.assertEquals(expectedStates2.size(), actualStates2.size());
        for (int i=0; i<expectedStates2.size(); i++) {
            HistoryState<AirConditionerState> expectedState = expectedStates2.get(i);
            HistoryState<AirConditionerState> actualState = actualStates2.get(i);
            Assert.assertEquals(expectedState.getState().power, actualState.getState().power);
            Assert.assertEquals(expectedState.getState().currentTemperature, actualState.getState().currentTemperature);
        }
    }
}
