package com.kii.thingiftrait.internal.gson;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.kii.thingiftrait.command.Action;

import java.lang.reflect.Type;
import java.util.Map;

class ActionAdapter implements JsonSerializer<Action> {

    private String actionName;

    ActionAdapter(String actionName) {
        this.actionName = actionName;
    }
    @Override
    public JsonElement serialize(Action src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) return null;
        Gson gson = new Gson();
        JsonObject json = gson.toJsonTree(src).getAsJsonObject();

        // remove other serialized field, only left the action value field.
        for (Map.Entry<String, JsonElement> entry: json.entrySet()) {
            if (!entry.getKey().equals(this.actionName)) {
                json.remove(entry.getKey());
            }
        }
        return json;
    }
}
