package com.kii.thing_if.query;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kii.thing_if.internal.gson.TimeRangeAdapter;
import com.kii.thing_if.internal.utils.JsonUtils;
import com.kii.thing_if.states.AirConditionerState;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AggregatedResultAdapterTest {

    @Test
    public void deserializationTest() throws Exception {
        TimeRange range = new TimeRange(new Date(10), new Date(2000));
        AirConditionerState airState = new AirConditionerState(true, 25);
        HistoryState<AirConditionerState> historyState = new HistoryState<>(airState, new Date(300));
        List<HistoryState<AirConditionerState>> states = new ArrayList<>();
        states.add(historyState);
        AggregatedResult<Integer, AirConditionerState> expect =
                new AggregatedResult<Integer, AirConditionerState>(range, 1500, states);

        String json = JsonUtils.newJson(
                "{" +
                    "\"range\":{" +
                        "\"from\":" +  range.getFrom().getTime() + "," +
                        "\"to\":" + range.getTo().getTime() +
                    "}," +
                    "\"aggregations\":[{" +
                        "\"value\": " + expect.getValue() + "," +
                        "\"name\" : \"max\"," +
                        "\"object\":{" +
                            "\"_created\": " + historyState.getCreatedAt().getTime() + "," +
                            "\"power\": " + airState.power + "," +
                            "\"currentTemperature\": " + airState.currentTemperature +
                        "}" +
                    "}]" +
                "}").toString();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        AggregatedResult.class,
                        new AggregatedResultAdapter<>(AirConditionerState.class, Integer.class))
                .registerTypeAdapter(TimeRange.class, new TimeRangeAdapter())
                .create();

        AggregatedResult<Integer, AirConditionerState> actual = gson.fromJson(json, AggregatedResult.class);

        Assert.assertNotNull(actual);
        Assert.assertEquals(expect.getTimeRange(), actual.getTimeRange());
        Assert.assertEquals(expect.getValue(), actual.getValue());
        Assert.assertEquals(expect.getAggregatedObjects(), actual.getAggregatedObjects());
    }
}
