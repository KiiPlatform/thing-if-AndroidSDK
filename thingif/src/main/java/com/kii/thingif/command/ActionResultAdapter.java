package com.kii.thingif.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.kii.thingif.command.ActionResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Map;

public class ActionResultAdapter implements
        JsonSerializer<ActionResult>,
        JsonDeserializer<ActionResult>{

    @Override
    public JsonElement serialize(ActionResult src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) return null;
        JsonObject json = new JsonObject();
        json.addProperty("succeeded", src.isSucceeded());
        if (src.getErrorMessage() != null) {
            json.addProperty("errorMessage", src.getErrorMessage());
        }
        if (src.getData() != null) {
            json.addProperty("data", src.getData().toString());
        }
        return json;
    }

    @Override
    public ActionResult deserialize(final JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (jsonElement == null) return null;
        JsonObject json = jsonElement.getAsJsonObject();
        if (!json.entrySet().iterator().hasNext()) {
            return null;
        }
        Map.Entry<String, JsonElement> resultEntry = json.entrySet().iterator().next();
        String actionName = resultEntry.getKey();
        JsonObject resultJson = resultEntry.getValue().getAsJsonObject();
        resultJson.addProperty("actionName", actionName);
        JsonDeserializer<JSONObject> metadataDeserializer = new JsonDeserializer<JSONObject>() {
            @Override
            public JSONObject deserialize(JsonElement jsonElement1, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                if (jsonElement1 == null) return null;
                if (jsonElement1.isJsonPrimitive() &&
                        ((JsonPrimitive)jsonElement1).isString()){
                    try {
                        return new JSONObject(jsonElement1.getAsString());
                    }catch (JSONException ex) {
                        throw  new RuntimeException(ex);
                    }
                }
                return null;
            }
        };
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        JSONObject.class,
                        metadataDeserializer)
                .create();
        return gson.fromJson(resultJson, ActionResult.class);
    }
}
