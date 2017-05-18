package com.kii.thing_if.internal.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.kii.thing_if.command.Action;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ActionTypeMapAdapter implements
        JsonSerializer<Map<String, Class<? extends Action>>>,
        JsonDeserializer<Map<String, Class<? extends Action>>>{
    @Override
    public JsonElement serialize(Map<String, Class<? extends Action>> src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) {
            return null;
        }
        JsonObject json = new JsonObject();
        for (Map.Entry<String, Class<? extends Action>> entry: src.entrySet()) {
            json.add(entry.getKey(), new JsonPrimitive(entry.getValue().getName()));
        }
        return json;
    }

    @Override
    public Map<String, Class<? extends Action>> deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (jsonElement == null) {
            return null;
        }
        try {
            JsonObject json = jsonElement.getAsJsonObject();
            Map<String, Class<? extends Action>> ret = new HashMap<>();
            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                String alias = entry.getKey();
                String className = entry.getValue().getAsString();
                ret.put(alias, Class.forName(className).asSubclass(Action.class));
            }
            return ret;
        }catch (Exception e) {
            throw new JsonParseException(e);
        }
    }
}
