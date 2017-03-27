package com.kii.thingif.internal.gson;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.kii.thingif.command.Action;

import java.lang.reflect.Type;
import java.util.Map;

class ActionAdapter implements
        JsonSerializer<Action>,
        JsonDeserializer<Action> {

    private Class<? extends Action> actionClass;

    ActionAdapter(Class<? extends Action> actionClass) {
        this.actionClass = actionClass;
    }
    @Override
    public JsonElement serialize(Action src, Type typeOfSrc, JsonSerializationContext context) {
//        if (src == null) return null;
//        Gson gson = new Gson();
//        JsonObject json = gson.toJsonTree(src).getAsJsonObject();
//        JsonArray ret = new JsonArray();
//        for (Map.Entry<String, JsonElement> element: json.entrySet()) {
//            JsonObject singleAction = new JsonObject();
//            String key = element.getKey();
//            JsonElement value = element.getValue();
//            if (value.isJsonPrimitive()) {
//                JsonPrimitive primVale = (JsonPrimitive)value;
//                if (primVale.isString()) {
//                    singleAction.addProperty(key, primVale.getAsString());
//                }else if (primVale.isBoolean()){
//                    singleAction.addProperty(key, primVale.getAsBoolean());
//                }else if (primVale.isNumber()) {
//                    singleAction.addProperty(key, primVale.getAsNumber());
//                }
//            }else{
//                singleAction.add(key, value);
//            }
//            ret.add(singleAction);
//        }
//        return ret;
        //TODO: // FIXME: 2017/03/27
        return null;
    }

    @Override
    public Action deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (jsonElement == null || !jsonElement.isJsonArray()) return null;
        JsonArray singleActionArray = jsonElement.getAsJsonArray();

        JsonObject actionJson = new JsonObject();
        for (JsonElement singleAction: singleActionArray) {
            if (singleAction.getAsJsonObject().entrySet().iterator().hasNext()) {
                Map.Entry<String, JsonElement> firstEntry =
                        singleAction.getAsJsonObject().entrySet().iterator().next();
                JsonElement value = firstEntry.getValue();
                String key = firstEntry.getKey();
                if (value.isJsonPrimitive()) {
                    JsonPrimitive primValue = value.getAsJsonPrimitive();
                    if (primValue.isNumber()) {
                        actionJson.addProperty(key, primValue.getAsNumber());
                    }else if (primValue.isBoolean()) {
                        actionJson.addProperty(key, primValue.getAsBoolean());
                    }else if (primValue.isString()) {
                        actionJson.addProperty(key, primValue.getAsString());
                    }
                }else{
                    actionJson.add(key, value);
                }
            }
        }
        return new Gson().fromJson(actionJson, this.actionClass);
    }
}
