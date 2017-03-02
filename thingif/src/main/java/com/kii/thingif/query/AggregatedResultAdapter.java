package com.kii.thingif.query;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.kii.thingif.TargetState;
import com.kii.thingif.internal.gson.HistoryStateAdapter;
import com.kii.thingif.internal.gson.TimeRangeAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AggregatedResultAdapter<T extends Number, S extends TargetState>
        implements JsonDeserializer<AggregatedResult<T, S>> {
    private Class<S> stateClass;

    public AggregatedResultAdapter(Class<S> cls) {
        this.stateClass = cls;
    }

    @Override
    public AggregatedResult<T, S> deserialize(
            JsonElement jsonElement,
            Type typeOfT,
            JsonDeserializationContext context)
        throws JsonParseException
    {
        if (jsonElement == null) return null;

        JsonObject json = jsonElement.getAsJsonObject();

        if (!json.has("range") || !json.has("value")) {
            throw new JsonParseException("no neceessary fields.");
        }

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(TimeRange.class, new TimeRangeAdapter())
                .registerTypeAdapter(HistoryState.class, new HistoryStateAdapter(this.stateClass))
                .create();

        TimeRange range = gson.fromJson(json.getAsJsonObject("range"), TimeRange.class);
        T value = (T)json.getAsJsonPrimitive("value").getAsNumber();
        List<HistoryState<S>> aggregateObjects = null;
        if (json.has("objects")) {
            aggregateObjects = new ArrayList<>();
            JsonArray objects = json.getAsJsonArray("objects");
            for (int i = 0; i < objects.size(); ++i) {
                aggregateObjects.add(gson.fromJson(objects.get(i), HistoryState.class));
            }
        }
        return new AggregatedResult(range, value, aggregateObjects);
    }
}
