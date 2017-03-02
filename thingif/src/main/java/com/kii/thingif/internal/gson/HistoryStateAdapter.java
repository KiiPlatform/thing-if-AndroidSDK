package com.kii.thingif.internal.gson;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.kii.thingif.TargetState;
import com.kii.thingif.query.HistoryState;

import java.lang.reflect.Type;
import java.util.Date;

public class HistoryStateAdapter implements JsonDeserializer<HistoryState> {

    private Class<? extends TargetState> stateClass;

    public HistoryStateAdapter(Class<? extends TargetState> stateClass) {
        this.stateClass = stateClass;
    }

    @Override
    public HistoryState deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (jsonElement == null || !jsonElement.isJsonObject())return null;
        JsonObject json = jsonElement.getAsJsonObject();
        TargetState state = new Gson().fromJson(json, stateClass);
        Long createdAt = json.get("_created").getAsLong();
        return new HistoryState(state, new Date(createdAt));
    }
}
