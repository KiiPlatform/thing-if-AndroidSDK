package com.kii.thingif.internal.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

public class JSONObjectAdapter implements
        JsonSerializer<JSONObject>,
        JsonDeserializer<JSONObject>{

    @Override
    public JsonElement serialize(JSONObject src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) return null;
        return new JsonParser().parse(src.toString());
    }

    @Override
    public JSONObject deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (jsonElement == null || !jsonElement.isJsonObject())return null;
        try {
            return new JSONObject(jsonElement.toString());
        }catch (JSONException ex) {
            // never happen
            return null;
        }
    }
}
