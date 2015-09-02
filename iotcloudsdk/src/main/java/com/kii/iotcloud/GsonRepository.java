package com.kii.iotcloud;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.kii.iotcloud.TypedID;
import com.kii.iotcloud.command.Action;
import com.kii.iotcloud.command.ActionResult;
import com.kii.iotcloud.exception.UnsupportedActionException;
import com.kii.iotcloud.schema.Schema;
import com.kii.iotcloud.schema.SchemaBuilder;
import com.kii.iotcloud.trigger.Condition;
import com.kii.iotcloud.trigger.EventSource;
import com.kii.iotcloud.trigger.Predicate;
import com.kii.iotcloud.trigger.Schedule;
import com.kii.iotcloud.trigger.SchedulePredicate;
import com.kii.iotcloud.trigger.StatePredicate;
import com.kii.iotcloud.trigger.TriggersWhen;
import com.kii.iotcloud.trigger.clause.And;
import com.kii.iotcloud.trigger.clause.Equals;
import com.kii.iotcloud.trigger.clause.NotEquals;
import com.kii.iotcloud.trigger.clause.Or;
import com.kii.iotcloud.trigger.clause.Clause;
import com.kii.iotcloud.trigger.clause.Range;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Customize json serialization and deserialization for domain model of IoTCloudSDK.
 * This class is for internal use only. Do not use it from your application.
 */
public class GsonRepository {

    private static final Map<Pair<String, Integer>, Gson> REPOSITORY = Collections.synchronizedMap(new HashMap<Pair<String, Integer>, Gson>());
    private static final Gson DEFAULT_GSON;
    private static final Gson PURE_GSON = new Gson();

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
            json.add(src.getActionName(), PURE_GSON.toJsonTree(src));
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
            json.add(src.getActionName(), PURE_GSON.toJsonTree(src));
            return json;
        }
    };
    private static final JsonSerializer<Predicate> PREDICATE_SERIALIZER = new JsonSerializer<Predicate>() {
        @Override
        public JsonElement serialize(Predicate src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == null) {
                return null;
            }
            JsonObject json = new JsonObject();
            json.addProperty("eventSource", src.getEventSource().getValue());
            if (src.getEventSource() == EventSource.SCHEDULE) {
                json.addProperty("schedule", ((SchedulePredicate)src).getSchedule().getCronExpression());
            } else if (src.getEventSource() == EventSource.STATES) {
                json.add("condition", context.serialize(((StatePredicate)src).getCondition()));
                json.addProperty("triggersWhen", ((StatePredicate)src).getTriggersWhen().name());
            }
            return json;
        }
    };
    private static final JsonDeserializer<Predicate> PREDICATE_DESERIALIZER = new JsonDeserializer<Predicate>() {
        @Override
        public Predicate deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (jsonElement == null) {
                return null;
            }
            JsonObject json = (JsonObject)jsonElement;
            EventSource eventSource = EventSource.fromValue(json.get("eventSource").getAsString());
            Predicate predicate = null;
            if (eventSource == EventSource.SCHEDULE) {
                predicate = new SchedulePredicate(new Schedule(json.get("schedule").getAsString()));
            } else if (eventSource == EventSource.STATES) {
                Condition condition = context.deserialize(new JsonParser().parse(json.get("condition").toString()), Condition.class);
                TriggersWhen triggersWhen = TriggersWhen.valueOf(json.get("triggersWhen").getAsString());
                predicate = new StatePredicate(condition, triggersWhen);
            }
            return predicate;
        }
    };
    private static final JsonSerializer<Condition> CONDITION_SERIALIZER = new JsonSerializer<Condition>() {
        @Override
        public JsonElement serialize(Condition src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == null) {
                return null;
            }
            return context.serialize(src.getClause());
        }
    };
    private static final JsonDeserializer<Condition> CONDITION_DESERIALIZER = new JsonDeserializer<Condition>() {
        @Override
        public Condition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json == null) {
                return null;
            }
            Clause clause = context.deserialize(json, Clause.class);
            return new Condition(clause);
        }
    };
    private static final JsonSerializer<Clause> STATEMENT_SERIALIZER = new JsonSerializer<Clause>() {
        @Override
        public JsonElement serialize(Clause src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == null) {
                return null;
            }
            return new JsonParser().parse(src.toJSONObject().toString());
        }
    };
    private static final JsonDeserializer<Clause> STATEMENT_DESERIALIZER = new JsonDeserializer<Clause>() {
        @Override
        public Clause deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (jsonElement == null) {
                return null;
            }
            JsonObject json = (JsonObject)jsonElement;
            String type = json.get("type").getAsString();
            if (TextUtils.equals("eq", type)) {
                if (((JsonPrimitive)json.get("value")).isString()) {
                    return new Equals(json.get("field").getAsString(), json.get("value").getAsString());
                } else if (((JsonPrimitive)json.get("value")).isNumber()) {
                    return new Equals(json.get("field").getAsString(), json.get("value").getAsLong());
                } else if (((JsonPrimitive)json.get("value")).isBoolean()) {
                    return new Equals(json.get("field").getAsString(), json.get("value").getAsBoolean());
                } else {
                    // Won't happens
                    throw new AssertionError("Detected unexpected type of value");
                }
            } else if (TextUtils.equals("not", type)) {
                Equals eq = context.deserialize(new JsonParser().parse(json.get("clause").toString()), Equals.class);
                return new NotEquals(eq);
            } else if (TextUtils.equals("and", type) || TextUtils.equals("or", type)) {
                List<Clause> statements = new ArrayList<Clause>();
                JsonArray clauses = json.getAsJsonArray("clauses");
                for (int i = 0; i < clauses.size(); i++) {
                    Clause clause = context.deserialize(clauses.get(i), Clause.class);
                    statements.add(clause);
                }
                if (TextUtils.equals("and", type)) {
                    return new And(statements.toArray(new Clause[statements.size()]));
                } else if (TextUtils.equals("or", type)) {
                    return new Or(statements.toArray(new Clause[statements.size()]));
                }
            } else if (TextUtils.equals("range", type)) {
                return PURE_GSON.fromJson(json, Range.class);
            }
            throw new JsonParseException(jsonElement.toString());
        }
    };

    private static final JsonSerializer<Schema> SCHEMA_SERIALIZER = new JsonSerializer<Schema>() {
        @Override
        public JsonElement serialize(Schema src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == null) {
                return null;
            }
            JsonObject json = new JsonObject();
            json.addProperty("thingType", src.getThingType());
            json.addProperty("schemaName", src.getSchemaName());
            json.addProperty("schemaVersion", src.getSchemaVersion());
            json.addProperty("stateClass", src.getStateClass().getName());
            JsonArray actionClasses = new JsonArray();
            for (Class<? extends Action> actionClass : src.getActionClasses()) {
                actionClasses.add(new JsonPrimitive(actionClass.getName()));
            }
            json.add("actionClasses", actionClasses);
            JsonArray actionResultClasses = new JsonArray();
            for (Class<? extends ActionResult> actionResultClass : src.getActionResultClasses()) {
                actionResultClasses.add(new JsonPrimitive(actionResultClass.getName()));
            }
            json.add("actionResultClasses", actionResultClasses);
            return json;
        }
    };
    private static final JsonDeserializer<Schema> SCHEMA_DESERIALIZER = new JsonDeserializer<Schema>() {
        @Override
        public Schema deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (jsonElement == null) {
                return null;
            }
            try {
                JsonObject json = (JsonObject)jsonElement;
                String thingType = json.get("thingType").getAsString();
                String schemaName = json.get("schemaName").getAsString();
                int schemaVersion = json.get("schemaVersion").getAsInt();
                Class<? extends TargetState> stateClass = (Class<? extends TargetState>) Class.forName(json.get("stateClass").getAsString());
                SchemaBuilder sb = SchemaBuilder.newSchemaBuilder(thingType, schemaName, schemaVersion, stateClass);
                JsonArray actionClasses = json.getAsJsonArray("actionClasses");
                JsonArray actionResultClasses = json.getAsJsonArray("actionResultClasses");
                for (int i = 0; i < actionClasses.size(); i++) {
                    sb.addActionClass(
                            (Class<? extends Action>)Class.forName(actionClasses.get(i).getAsString()),
                            (Class<? extends ActionResult>)Class.forName(actionResultClasses.get(i).getAsString()));
                }
                return sb.build();
            } catch (Exception e) {
                throw new JsonParseException(e);
            }
        }
    };

    private static final JsonSerializer<IoTCloudAPI> IOT_CLOUD_API_SERIALIZER = new JsonSerializer<IoTCloudAPI>() {
        @Override
        public JsonElement serialize(IoTCloudAPI src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == null) {
                return null;
            }
            JsonObject json = new JsonObject();
            json.addProperty("appID", src.getAppID());
            json.addProperty("appKey", src.getAppKey());
            json.addProperty("baseUrl", src.getBaseUrl());
            json.add("owner", DEFAULT_GSON.toJsonTree(src.getOwner()));
            json.add("target", DEFAULT_GSON.toJsonTree(src.getTarget()));
            JsonArray schemas = new JsonArray();
            for (Schema schema : src.getSchemas()) {
                schemas.add(DEFAULT_GSON.toJsonTree(schema));
            }
            json.add("schemas", schemas);
            json.addProperty("installationID", src.getInstallationID());
            return json;
        }
    };
    private static final JsonDeserializer<IoTCloudAPI> IOT_CLOUD_API_DESERIALIZER = new JsonDeserializer<IoTCloudAPI>() {
        @Override
        public IoTCloudAPI deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (jsonElement == null) {
                return null;
            }
            return null;
        }
    };
    static {
        DEFAULT_GSON = new GsonBuilder()
                .registerTypeAdapter(TypedID.class, TYPED_ID_SERIALIZER)
                .registerTypeAdapter(TypedID.class, TYPED_ID_DESERIALIZER)
                .registerTypeHierarchyAdapter(Action.class, ACTION_SERIALIZER)
                .registerTypeHierarchyAdapter(ActionResult.class, ACTION_RESULT_SERIALIZER)
                .registerTypeHierarchyAdapter(Clause.class, STATEMENT_SERIALIZER)
                .registerTypeHierarchyAdapter(Clause.class, STATEMENT_DESERIALIZER)
                .registerTypeAdapter(Condition.class, CONDITION_SERIALIZER)
                .registerTypeAdapter(Condition.class, CONDITION_DESERIALIZER)
                .registerTypeHierarchyAdapter(Predicate.class, PREDICATE_SERIALIZER)
                .registerTypeHierarchyAdapter(Predicate.class, PREDICATE_DESERIALIZER)
                .registerTypeAdapter(Schema.class, SCHEMA_SERIALIZER)
                .registerTypeAdapter(Schema.class, SCHEMA_DESERIALIZER)
                .registerTypeAdapter(IoTCloudAPI.class, IOT_CLOUD_API_SERIALIZER)
                .registerTypeAdapter(IoTCloudAPI.class, IOT_CLOUD_API_DESERIALIZER)
                .create();
    }

    /**
     * Returns the Gson instance that can handle Action class and ActionResult class that are defined specified schema.
     * Action class and ActionResult class depend on the schema name and version.
     *
     * @param schema
     * @return
     */
    public static Gson gson(@Nullable final Schema schema) {
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
                    .registerTypeHierarchyAdapter(Action.class, ACTION_SERIALIZER)
                    .registerTypeHierarchyAdapter(ActionResult.class, ACTION_RESULT_SERIALIZER)
                    .registerTypeHierarchyAdapter(Clause.class, STATEMENT_SERIALIZER)
                    .registerTypeHierarchyAdapter(Clause.class, STATEMENT_DESERIALIZER)
                    .registerTypeAdapter(Condition.class, CONDITION_SERIALIZER)
                    .registerTypeAdapter(Condition.class, CONDITION_DESERIALIZER)
                    .registerTypeHierarchyAdapter(Predicate.class, PREDICATE_SERIALIZER)
                    .registerTypeHierarchyAdapter(Predicate.class, PREDICATE_DESERIALIZER)
                    .registerTypeAdapter(Action.class, ACTION_DESERIALIZER)
                    .registerTypeAdapter(ActionResult.class, ACTION_RESULT_DESERIALIZER)
                    .registerTypeAdapter(Schema.class, SCHEMA_SERIALIZER)
                    .registerTypeAdapter(Schema.class, SCHEMA_DESERIALIZER)
                    .registerTypeAdapter(IoTCloudAPI.class, IOT_CLOUD_API_SERIALIZER)
                    .registerTypeAdapter(IoTCloudAPI.class, IOT_CLOUD_API_DESERIALIZER)
                    .create();
            REPOSITORY.put(new Pair<String, Integer>(schema.getSchemaName(), schema.getSchemaVersion()), gson);
        }
        return gson;
    }
    /**
     * This method is for unit tests use only. Do not use it from SDK code.
     */
    static void clearCache() {
        REPOSITORY.clear();
    }
}
