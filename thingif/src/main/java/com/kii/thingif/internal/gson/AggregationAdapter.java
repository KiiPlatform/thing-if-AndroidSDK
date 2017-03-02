package com.kii.thingif.internal.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.kii.thingif.query.Aggregation;

import java.lang.reflect.Type;

public class AggregationAdapter implements JsonSerializer<Aggregation> {
    @Override
    public JsonElement serialize(Aggregation src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) return null;

        JsonObject json = new JsonObject();
        json.addProperty("type", src.getFunction().name());
        json.addProperty("field", src.getField());
        json.addProperty("fieldType", src.getFieldType().name());

        return json;
    }
}
