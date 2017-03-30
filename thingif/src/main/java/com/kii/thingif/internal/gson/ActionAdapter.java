package com.kii.thingif.internal.gson;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.kii.thingif.command.Action;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
        if (src == null) return null;
        Gson gson = new Gson();
        JsonObject json = gson.toJsonTree(src).getAsJsonObject();
        JsonObject singleAction = new JsonObject();
        if (TextUtils.isEmpty(src.getActionName())) {
            throw new JsonParseException("getActionName() method return null or empty");
        }
        String actionName = src.getActionName();

        if (!json.entrySet().iterator().hasNext()) {
            throw new JsonParseException("action doesn't have field, or value of filed is null");
        }
        // get the value of first key from json
        Map.Entry<String, JsonElement> firstEntry = json.entrySet().iterator().next();
        JsonElement value = firstEntry.getValue();

        if (value.isJsonPrimitive()) {
            JsonPrimitive primVale = (JsonPrimitive)value;
            if (primVale.isString()) {
                singleAction.addProperty(actionName, primVale.getAsString());
            }else if (primVale.isBoolean()){
                singleAction.addProperty(actionName, primVale.getAsBoolean());
            }else if (primVale.isNumber()) {
                singleAction.addProperty(actionName, primVale.getAsNumber());
            }
        }else{
            singleAction.add(actionName, value);
        }
        return singleAction;
    }

    @Override
    public Action deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (jsonElement == null || !jsonElement.isJsonObject()) return null;

        Map.Entry<String, JsonElement> firstEntry = jsonElement.getAsJsonObject().entrySet().iterator().next();
        JsonElement actionValue = firstEntry.getValue();

        String fieldName = null;

        if (this.actionClass.getEnclosingClass() != null &&
                !Modifier.isStatic(this.actionClass.getModifiers())) {
            // for non static inner class skip the default field
            for (Field field : this.actionClass.getDeclaredFields()) {
                if (!field.getName().equals("this$0")) {
                    fieldName = field.getName();
                    break;
                }
            }
        }else if (this.actionClass.getDeclaredFields().length == 1){
            fieldName = this.actionClass.getDeclaredFields()[0].getName();
        }
        JsonObject actionJson = new JsonObject();
        if (fieldName == null) {
            throw new JsonParseException("can not find a user defined field");
        }

        if (actionValue.isJsonPrimitive()) {
            JsonPrimitive primValue = actionValue.getAsJsonPrimitive();
            if (primValue.isNumber()) {
                actionJson.addProperty(fieldName, primValue.getAsNumber());
            }else if (primValue.isBoolean()) {
                actionJson.addProperty(fieldName, primValue.getAsBoolean());
            }else if (primValue.isString()) {
                actionJson.addProperty(fieldName, primValue.getAsString());
            }
        }else{
            actionJson.add(fieldName, actionValue);
        }
        return new Gson().fromJson(actionJson, this.actionClass);
    }
}
