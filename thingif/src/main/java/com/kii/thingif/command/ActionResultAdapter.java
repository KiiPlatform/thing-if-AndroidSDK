package com.kii.thingif.command;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * SDK uses this class to parse  action result of json format received from server.
 * It is internal only.
 */
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
        JsonObject ret = new JsonObject();
        ret.add(src.getActionName(), json);
        return ret;
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
        if (!resultJson.has("succeeded")) {
            throw new JsonParseException("json object does not contain succeeded");
        }
        JsonElement succeededJson = resultJson.get("succeeded");
        if (!succeededJson.isJsonPrimitive() ||
                !((JsonPrimitive) succeededJson).isBoolean()) {
            throw new JsonParseException("type of succeeded is not boolean");
        }
        boolean succeeded = succeededJson.getAsBoolean();

        String errorMessage = null;
        if (resultJson.has("errorMessage")) {
            JsonElement messageJson = resultJson.get("errorMessage");
            if (messageJson.isJsonPrimitive()) {
                if (((JsonPrimitive)messageJson).isString()) {
                    errorMessage = messageJson.getAsString();
                }
            }
        }

        JSONObject data = null;
        if (resultJson.has("data")) {
            JsonElement dataJson = resultJson.get("data");
            if (dataJson.isJsonPrimitive()) {
                if (((JsonPrimitive)dataJson).isString())
                try {
                    data = new JSONObject(dataJson.getAsString());
                }catch (JSONException ex) {
                    throw new JsonParseException(ex);
                }
            }
        }
        return new ActionResult(actionName, succeeded, errorMessage, data);
    }
}
