package com.kii.thingif.internal.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kii.thingif.TargetState;
import com.kii.thingif.query.HistoryState;
import com.kii.thingif.states.AirConditionerState;
import com.kii.thingif.states.HumidityState;
import com.kii.thingif.utils.JsonUtil;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Date;

@RunWith(RobolectricTestRunner.class)
public class HistoryStateAdapterTest {
    @Test
    public void deserializationTest() {
        HistoryState[] states = {
                new HistoryState<>(new AirConditionerState(true, 23), new Date()),
                new HistoryState<>(new AirConditionerState(null, 23), new Date()),
                new HistoryState<>(new AirConditionerState(true, null), new Date()),
                new HistoryState<>(new HumidityState(45), new Date()),
        };

        for (int i=0; i<states.length; i++) {
            HistoryState historyState = states[i];
            Class<? extends TargetState> stateClass = historyState.getState().getClass();
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(
                            HistoryState.class,
                            new HistoryStateAdapter(stateClass))
                    .create();
            HistoryState deserializedHistoryState =
                    gson.fromJson(
                            JsonUtil.historyStateToJson(historyState).toString(),
                            HistoryState.class);
            Assert.assertTrue(historyState.getState().equals(deserializedHistoryState.getState()));
            Assert.assertTrue(historyState.getCreatedAt().equals(deserializedHistoryState.getCreatedAt()));
        }
    }
}
