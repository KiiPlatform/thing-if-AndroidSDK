package com.kii.thingif.internal.gson;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.AliasAction;

import java.lang.reflect.Type;
import java.util.Map;

public class AliasActionAdapter implements
        JsonSerializer<AliasAction>,
        JsonDeserializer<AliasAction> {

    private Map<String, Class<? extends Action>> actionTypes;

    public AliasActionAdapter(Map<String, Class<? extends Action>> actionTypes) {
        this.actionTypes = actionTypes;
    }
    @Override
    public JsonElement serialize(AliasAction src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) return null;

        JsonObject json = new JsonObject();
        json.add(
                src.getAlias(),
                new Gson().toJsonTree(src.getAction()));
        return json;
    }

    @Override
    public AliasAction deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (jsonElement == null) return null;

        JsonObject json = jsonElement.getAsJsonObject();

        for (Map.Entry<String, Class<? extends Action>> entry: this.actionTypes.entrySet()) {
            String alias = entry.getKey();
            Class<? extends Action> actionClass = entry.getValue();
            if (json.has(alias)) {
                JsonObject actionJson = json.getAsJsonObject(alias);
                Action action = new Gson().fromJson(actionJson, actionClass);
                return new AliasAction(alias, action);
            }
        }
        return null;
    }
}
