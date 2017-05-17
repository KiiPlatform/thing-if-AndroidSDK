package com.kii.thingiftrait.trigger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.kii.thingiftrait.ServerError;
import com.kii.thingiftrait.internal.gson.JSONObjectAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

public class TriggeredServerCodeResultAdapter implements
        JsonDeserializer<TriggeredServerCodeResult> {
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(
                    JSONObject.class,
                    new JSONObjectAdapter())
            .create();

    @Override
    public TriggeredServerCodeResult deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (jsonElement == null || !jsonElement.isJsonObject()) return null;

        JsonObject json = (JsonObject)jsonElement;
        boolean succeeded = json.get("succeeded").getAsBoolean();
        long executedAt = json.get("executedAt").getAsLong();
        String endpoint = null;
        if (json.has("endpoint")) {
            endpoint = json.get("endpoint").getAsString();
        }
        Object returnedValue = null;
        if (json.has("returnedValue")) {
            if (json.get("returnedValue").isJsonObject()) {
                try {
                    returnedValue = new JSONObject(json.get("returnedValue").getAsJsonObject().toString());
                } catch (JSONException e) {
                    throw new JsonParseException(e);
                }
            } else if (json.get("returnedValue").isJsonArray()) {
                try {
                    returnedValue = new JSONArray(json.get("returnedValue").getAsJsonArray().toString());
                } catch (JSONException e) {
                    throw new JsonParseException(e);
                }
            } else if (json.get("returnedValue").isJsonPrimitive()) {
                JsonPrimitive primitive = json.get("returnedValue").getAsJsonPrimitive();
                if (primitive.isString()) {
                    returnedValue = primitive.getAsString();
                } else if (primitive.isBoolean()) {
                    returnedValue = primitive.getAsBoolean();
                } else if (primitive.isNumber()) {
                   returnedValue = primitive.getAsNumber();
                }
            }
        }
        ServerError error = null;
        if (json.has("error") && !json.get("error").isJsonNull()) {
            try {
                JSONObject e = new JSONObject(json.get("error").getAsJsonObject().toString());
                error = new ServerError(e);
            } catch (JSONException e) {
                throw new JsonParseException(e);
            }
        }

        return new TriggeredServerCodeResult(succeeded, returnedValue, executedAt, endpoint, error);
    }
}
