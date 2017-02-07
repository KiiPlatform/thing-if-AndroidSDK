package com.kii.thingif.internal.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.kii.thingif.TargetState;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class StateTypeMapAdapter implements
        JsonSerializer<Map<String, Class<? extends TargetState>>>,
        JsonDeserializer<Map<String, Class<? extends TargetState>>>{
    @Override
    public JsonElement serialize(Map<String, Class<? extends TargetState>> src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) {
            return null;
        }
        JsonObject json = new JsonObject();
        for (Map.Entry<String, Class<? extends TargetState>> entry: src.entrySet()) {
            json.add(entry.getKey(), new JsonPrimitive(entry.getValue().getName()));
        }
        return json;
    }

    @Override
    public Map<String, Class<? extends TargetState>> deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (jsonElement == null) {
            return null;
        }
        try {
            JsonObject json = jsonElement.getAsJsonObject();
            Map<String, Class<? extends TargetState>> ret = new HashMap<>();
            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                String alias = entry.getKey();
                String className = entry.getValue().getAsString();
                ret.put(alias, Class.forName(className).asSubclass(TargetState.class));
            }
            return ret;
        }catch (Exception e) {
            throw new JsonParseException(e);
        }
    }
}
