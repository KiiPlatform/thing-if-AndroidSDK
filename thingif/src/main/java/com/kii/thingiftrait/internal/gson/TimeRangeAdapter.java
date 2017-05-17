package com.kii.thingiftrait.internal.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.kii.thingiftrait.query.TimeRange;

import java.lang.reflect.Type;
import java.util.Date;

public class TimeRangeAdapter implements JsonDeserializer<TimeRange> {

    @Override
    public TimeRange deserialize(
            JsonElement jsonElement,
            Type typeOfT,
            JsonDeserializationContext context)
        throws JsonParseException
    {
        if (jsonElement == null) return null;

        JsonObject json = jsonElement.getAsJsonObject();

        if (!json.has("from") || !json.has("to")) {
            throw new JsonParseException(
                    "son doesn't contain both of 2 necessary fields: from and to.");
        }

        Date from = new Date(json.getAsJsonPrimitive("from").getAsLong());
        Date to = new Date(json.getAsJsonPrimitive("to").getAsLong());
        return new TimeRange(from, to);
    }
}
