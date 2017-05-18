package com.kii.thing_if.query;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.kii.thing_if.TargetState;
import com.kii.thing_if.internal.gson.HistoryStateAdapter;
import com.kii.thing_if.internal.gson.TimeRangeAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GroupedHistoryStatesAdapter<T extends TargetState> implements JsonDeserializer<GroupedHistoryStates<T>> {

    private Class<T> stateClass;
    public GroupedHistoryStatesAdapter(Class<T> stateClass) {
        this.stateClass = stateClass;
    }
    @Override
    public GroupedHistoryStates<T> deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (jsonElement == null || !jsonElement.isJsonObject())return null;
        JsonObject json = jsonElement.getAsJsonObject();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(TimeRange.class, new TimeRangeAdapter())
                .registerTypeAdapter(HistoryState.class, new HistoryStateAdapter(this.stateClass))
                .create();

        TimeRange range = gson.fromJson(json.get("range"), TimeRange.class);

        JsonArray states = json.get("objects").getAsJsonArray();

        List<HistoryState<T>> historyStates = new ArrayList<>();
        Type historyStateType = new TypeToken<HistoryState<T>>(){}.getType();
        for (JsonElement stateJson: states) {
            HistoryState<T> historyState = gson.fromJson(stateJson.getAsJsonObject(), historyStateType);
            historyStates.add(historyState);
        }
        return new GroupedHistoryStates<>(range, historyStates);
    }
}
