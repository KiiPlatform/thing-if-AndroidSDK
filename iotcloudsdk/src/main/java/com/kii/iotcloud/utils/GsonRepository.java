package com.kii.iotcloud.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.kii.iotcloud.TypedID;
import com.kii.iotcloud.command.Action;
import com.kii.iotcloud.command.ActionResult;

import java.lang.reflect.Type;

/**
 * Customize json serialization and deserialization for domain model of IoTCloudSDK.
 * This class is for internal use only. Do not use it from your application.
 */
public class GsonRepository {
    private static final Gson gson;
    static {
        JsonSerializer<TypedID> typedIDSerializer = new JsonSerializer<TypedID>() {
            @Override
            public JsonElement serialize(TypedID src, Type typeOfSrc, JsonSerializationContext context) {
                return src == null ? null : new JsonPrimitive(src.toString());
            }
        };
        JsonSerializer<Action> actionSerializer = new JsonSerializer<Action>() {
            @Override
            public JsonElement serialize(Action src, Type typeOfSrc, JsonSerializationContext context) {
                if (src == null) {
                    return null;
                }
                JsonObject json = new JsonObject();
                json.add(src.getActionName(), context.serialize(src));
                return json;
            }
        };
        JsonSerializer<ActionResult> actionResultSerializer = new JsonSerializer<ActionResult>() {
            @Override
            public JsonElement serialize(ActionResult src, Type typeOfSrc, JsonSerializationContext context) {
                if (src == null) {
                    return null;
                }
                JsonObject json = new JsonObject();
                json.add(src.getActionName(), context.serialize(src));
                return json;
            }
        };
        gson = new GsonBuilder()
                .registerTypeAdapter(TypedID.class, typedIDSerializer)
                .registerTypeAdapter(Action.class, actionSerializer)
                .registerTypeAdapter(ActionResult.class, actionResultSerializer)
                .create();
    }
    public static Gson gson() {
        return gson;
    }
}
