package com.kii.iotcloud.utils;

import android.util.Pair;

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
import com.kii.iotcloud.TypedID;
import com.kii.iotcloud.command.Action;
import com.kii.iotcloud.command.ActionResult;
import com.kii.iotcloud.exception.UnsupportedActionException;
import com.kii.iotcloud.schema.Schema;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Customize json serialization and deserialization for domain model of IoTCloudSDK.
 * This class is for internal use only. Do not use it from your application.
 */
public class GsonRepository {

    private static final Map<Pair<String, Integer>, Gson> REPOSITORY = Collections.synchronizedMap(new HashMap<Pair<String, Integer>, Gson>());
    private static final Gson DEFAULT_GSON;

    private static final JsonSerializer<TypedID> TYPED_ID_SERIALIZER = new JsonSerializer<TypedID>() {
        @Override
        public JsonElement serialize(TypedID src, Type typeOfSrc, JsonSerializationContext context) {
            return src == null ? null : new JsonPrimitive(src.toString());
        }
    };
    private static final JsonDeserializer<TypedID> TYPED_ID_DESERIALIZER = new JsonDeserializer<TypedID>() {
        @Override
        public TypedID deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json == null) {
                return null;
            }
            return TypedID.fromString(json.getAsString());
        }
    };
    private static final JsonSerializer<Action> ACTION_SERIALIZER = new JsonSerializer<Action>() {
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
    private static final JsonSerializer<ActionResult> ACTION_RESULT_SERIALIZER = new JsonSerializer<ActionResult>() {
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

    static {
        DEFAULT_GSON = new GsonBuilder()
                .registerTypeAdapter(TypedID.class, TYPED_ID_SERIALIZER)
                .registerTypeAdapter(TypedID.class, TYPED_ID_DESERIALIZER)
                .registerTypeAdapter(Action.class, ACTION_SERIALIZER)
                .registerTypeAdapter(ActionResult.class, ACTION_RESULT_SERIALIZER)
                .create();
    }

    /**
     * Action class and ActionResult class depend on the schema name and version.
     *
     * @param schema
     * @return
     */
    public static Gson gson(final Schema schema) {
        if (schema == null) {
            return DEFAULT_GSON;
        }
        Gson gson = REPOSITORY.get(new Pair<String, Integer>(schema.getSchemaName(), schema.getSchemaVersion()));
        if (gson == null) {
            JsonDeserializer<Action> ACTION_DESERIALIZER = new JsonDeserializer<Action>() {
                @Override
                public Action deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    if (json == null) {
                        return null;
                    }
                    // {"actionName":{"actionProperty1":"", "actionProperty2":"" ...}}
                    Set<Map.Entry<String, JsonElement>> jsonProperties = ((JsonObject) json).entrySet();
                    if (jsonProperties.size() != 1) {
                        throw new JsonParseException(json.toString() + " is unexpected format for Action");
                    }
                    Map.Entry<String, JsonElement> jsonProperty = jsonProperties.iterator().next();
                    String actionName = jsonProperty.getKey();
                    Class<? extends Action> actionClass = schema.getActionClass(actionName);
                    if (actionClass == null) {
                        throw new JsonParseException(new UnsupportedActionException(schema.getSchemaName(), schema.getSchemaVersion(), actionName));
                    }
                    return context.deserialize(jsonProperty.getValue(), actionClass);
                }
            };
            JsonDeserializer<ActionResult> ACTION_RESULT_DESERIALIZER = new JsonDeserializer<ActionResult>() {
                @Override
                public ActionResult deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    if (json == null) {
                        return null;
                    }
                    // {"actionResultName":{"actionResultProperty1":"", "actionResultProperty2":"" ...}}
                    Set<Map.Entry<String, JsonElement>> jsonProperties = ((JsonObject) json).entrySet();
                    if (jsonProperties.size() != 1) {
                        throw new JsonParseException(json.toString() + " is unexpected format for ActionResult");
                    }
                    Map.Entry<String, JsonElement> jsonProperty = jsonProperties.iterator().next();
                    String actionName = jsonProperty.getKey();
                    Class<? extends ActionResult> actionResultClass = schema.getActionResultClass(actionName);
                    if (actionResultClass == null) {
                        throw new JsonParseException(new UnsupportedActionException(schema.getSchemaName(), schema.getSchemaVersion(), actionName));
                    }
                    return context.deserialize(jsonProperty.getValue(), actionResultClass);
                }
            };
            gson = new GsonBuilder()
                    .registerTypeAdapter(TypedID.class, TYPED_ID_SERIALIZER)
                    .registerTypeAdapter(TypedID.class, TYPED_ID_DESERIALIZER)
                    .registerTypeAdapter(Action.class, ACTION_SERIALIZER)
                    .registerTypeAdapter(Action.class, ACTION_DESERIALIZER)
                    .registerTypeAdapter(ActionResult.class, ACTION_RESULT_SERIALIZER)
                    .registerTypeAdapter(ActionResult.class, ACTION_RESULT_DESERIALIZER)
                    .create();
            REPOSITORY.put(new Pair<String, Integer>(schema.getSchemaName(), schema.getSchemaVersion()), gson);
        }
        return gson;
    }
}
