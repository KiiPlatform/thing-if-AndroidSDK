package com.kii.thingiftrait.query;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.kii.thingiftrait.SmallTestBase;
import com.kii.thingiftrait.states.AirConditionerState;
import com.kii.thingiftrait.utils.JsonUtil;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class GroupedHistoryStatesAdatperTest extends SmallTestBase {
    @Test
    public void deserializationTest() {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        GroupedHistoryStates.class,
                        new GroupedHistoryStatesAdapter<>(AirConditionerState.class))
                .create();

        Type groupedStateType =
                new TypeToken<GroupedHistoryStates<AirConditionerState>>(){}.getType();

        // objects array is not empty
        List<HistoryState<AirConditionerState>> historyStates1 = new ArrayList<>();
        Date from1 = new Date();
        historyStates1.add(new HistoryState<>(new AirConditionerState(true, 23), new Date()));
        historyStates1.add(new HistoryState<>(new AirConditionerState(false, 24), new Date()));
        historyStates1.add(new HistoryState<>(new AirConditionerState(true, 25), new Date()));
        historyStates1.add(new HistoryState<>(new AirConditionerState(false, 26), new Date()));
        TimeRange range1 = new TimeRange(from1, new Date());

        GroupedHistoryStates<AirConditionerState> groupedStates1 =
                new GroupedHistoryStates<>(range1, historyStates1);
        GroupedHistoryStates<AirConditionerState> deserializedGroupedStates1 =
                gson.fromJson(
                        JsonUtil.groupedHistoryStateToJson(groupedStates1).toString(),
                        groupedStateType);
        Assert.assertTrue(
                groupedStates1.getTimeRange().equals(deserializedGroupedStates1.getTimeRange()));
        Assert.assertEquals(4, deserializedGroupedStates1.getObjects().size());
        assertSameGroupedHistoryStates(groupedStates1, deserializedGroupedStates1);

        // objects array is empty
        List<HistoryState<AirConditionerState>> historyStates2 = new ArrayList<>();
        Date from2 = new Date();
        TimeRange range2 = new TimeRange(from2, new Date());

        GroupedHistoryStates<AirConditionerState> groupedStates2 =
                new GroupedHistoryStates<>(range2, historyStates2);
        GroupedHistoryStates<AirConditionerState> deserializedGroupedStates2 =
                gson.fromJson(
                        JsonUtil.groupedHistoryStateToJson(groupedStates2).toString(),
                        groupedStateType);
        Assert.assertTrue(
                groupedStates2.getTimeRange().equals(deserializedGroupedStates2.getTimeRange()));
        Assert.assertEquals(0, deserializedGroupedStates2.getObjects().size());
        assertSameGroupedHistoryStates(groupedStates2, deserializedGroupedStates2);
    }
}
