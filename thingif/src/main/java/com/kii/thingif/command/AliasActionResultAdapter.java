package com.kii.thingif.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SDK uses this class to parse alias action result of json format.
 * It is internal only.
 */
public class AliasActionResultAdapter implements
        JsonSerializer<AliasActionResult>,
        JsonDeserializer<AliasActionResult>{

    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(
                    ActionResult.class,
                    new ActionResultAdapter())
            .create();

    @Override
    public JsonElement serialize(AliasActionResult src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) return null;
        JsonObject json = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        for (ActionResult result : src.getResults()) {
            JsonObject resultObject = gson.toJsonTree(result).getAsJsonObject();
            jsonArray.add(resultObject);
        }
        json.add(src.getAlias(), jsonArray);
        return json;
    }

    @Override
    public AliasActionResult deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (jsonElement == null) return null;
        JsonObject json = jsonElement.getAsJsonObject();
        if (!json.entrySet().iterator().hasNext()) {
            throw new JsonParseException("json has not entry");
        }
        Map.Entry<String, JsonElement> entry = json.entrySet().iterator().next();
        String alias = entry.getKey();
        if (!entry.getValue().isJsonArray()){
            throw new JsonParseException("invalid format: value of alias is not array");
        }
        JsonArray resultJsonArray = (JsonArray) entry.getValue();
        List<ActionResult> results = new ArrayList<>();
        // deserialize actionResult from raw json
        for (JsonElement resultJson: resultJsonArray) {
            ActionResult result = gson.fromJson(resultJson, ActionResult.class);
            results.add(result);
        }

        return new AliasActionResult(alias, results);
    }
}
