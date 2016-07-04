package com.kii.thingif.internal;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.kii.thingif.KiiApp;
import com.kii.thingif.Owner;
import com.kii.thingif.ServerError;
import com.kii.thingif.StandaloneThing;
import com.kii.thingif.Target;
import com.kii.thingif.TargetState;
import com.kii.thingif.TargetThing;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.ThingIFAPIBuilder;
import com.kii.thingif.TypedID;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.ActionResult;
import com.kii.thingif.exception.UnsupportedActionException;
import com.kii.thingif.gateway.EndNode;
import com.kii.thingif.gateway.Gateway;
import com.kii.thingif.schema.Schema;
import com.kii.thingif.schema.SchemaBuilder;
import com.kii.thingif.trigger.Condition;
import com.kii.thingif.trigger.EventSource;
import com.kii.thingif.trigger.Predicate;
import com.kii.thingif.trigger.ScheduleOncePredicate;
import com.kii.thingif.trigger.SchedulePredicate;
import com.kii.thingif.trigger.StatePredicate;
import com.kii.thingif.trigger.TriggeredServerCodeResult;
import com.kii.thingif.trigger.TriggersWhen;
import com.kii.thingif.trigger.clause.And;
import com.kii.thingif.trigger.clause.Clause;
import com.kii.thingif.trigger.clause.Equals;
import com.kii.thingif.trigger.clause.NotEquals;
import com.kii.thingif.trigger.clause.Or;
import com.kii.thingif.trigger.clause.Range;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Customize json serialization and deserialization for domain model of ThingIFSDK.
 * This class is for internal use only. Do not use it from your application.
 */
public class GsonRepository {

    private static final Map<Pair<String, Integer>, Gson> REPOSITORY = Collections.synchronizedMap(new HashMap<Pair<String, Integer>, Gson>());
    private static final Gson DEFAULT_GSON;
    private static final Gson PURE_GSON = new Gson();

    private static final JsonSerializer<JSONObject> ORG_JSON_OBJECT_SERIALIZER = new JsonSerializer<JSONObject>() {
        @Override
        public JsonElement serialize(JSONObject src, Type typeOfSrc, JsonSerializationContext context) {
            return src == null ? null : new JsonParser().parse(src.toString());
        }
    };
    private static final JsonDeserializer<JSONObject> ORG_JSON_OBJECT_DESERIALIZER = new JsonDeserializer<JSONObject>() {
        @Override
        public JSONObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json == null) {
                return null;
            }
            try {
                return new JSONObject(json.toString());
            } catch (JSONException e) {
                throw new JsonParseException(e);
            }
        }
    };
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
                json.addProperty("schedule", ((SchedulePredicate)src).getSchedule());
            } else if (src.getEventSource() == EventSource.STATES) {
                json.add("condition", context.serialize(((StatePredicate)src).getCondition()));
                json.addProperty("triggersWhen", ((StatePredicate)src).getTriggersWhen().name());
            } else if (src.getEventSource() == EventSource.SCHEDULE_ONCE) {
                json.addProperty("scheduleAt", ((ScheduleOncePredicate)src).getScheduleAt());
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
                predicate = new SchedulePredicate(json.get("schedule").getAsString());
            } else if (eventSource == EventSource.STATES) {
                Condition condition = context.deserialize(new JsonParser().parse(json.get("condition").toString()), Condition.class);
                TriggersWhen triggersWhen = TriggersWhen.valueOf(json.get("triggersWhen").getAsString());
                predicate = new StatePredicate(condition, triggersWhen);
            } else if (eventSource == EventSource.SCHEDULE_ONCE) {
                predicate = new ScheduleOncePredicate(json.get("scheduleAt").getAsLong());
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

    private static final JsonSerializer<ThingIFAPI> IOT_CLOUD_API_SERIALIZER = new JsonSerializer<ThingIFAPI>() {
        @Override
        public JsonElement serialize(ThingIFAPI src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == null) {
                return null;
            }
            JsonObject json = new JsonObject();
            json.add("app", DEFAULT_GSON.toJsonTree(src.getApp()));
            String tag = src.getTag();
            if (!TextUtils.isEmpty(tag)) {
                json.addProperty("tag", tag);
            }
            json.add("owner", DEFAULT_GSON.toJsonTree(src.getOwner()));
            if (src.getTarget() != null) {
                json.add("target", DEFAULT_GSON.toJsonTree(src.getTarget(), src.getTarget().getClass()));
            } else {
                json.add("target", JsonNull.INSTANCE);
            }
            JsonArray schemas = new JsonArray();
            for (Schema schema : src.getSchemas()) {
                schemas.add(DEFAULT_GSON.toJsonTree(schema));
            }
            json.add("schemas", schemas);
            json.addProperty("installationID", src.getInstallationID());
            return json;
        }
    };
    private static final JsonDeserializer<ThingIFAPI> IOT_CLOUD_API_DESERIALIZER = new JsonDeserializer<ThingIFAPI>() {
        @Override
        public ThingIFAPI deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (jsonElement == null) {
                return null;
            }
            JsonObject json = (JsonObject)jsonElement;
            KiiApp app = DEFAULT_GSON.fromJson(json.getAsJsonObject("app"), KiiApp.class);
            String tag = null;
            if (json.has("tag"))
                tag = json.get("tag").getAsString();
            Owner owner = DEFAULT_GSON.fromJson(json.getAsJsonObject("owner"), Owner.class);
            ThingIFAPIBuilder builder = ThingIFAPIBuilder._newBuilder(app, owner);
            if (json.has("target")) {
                Target target = DEFAULT_GSON.fromJson(json.getAsJsonObject("target"), Target.class);
                builder.setTarget(target);
            }
            JsonArray schemasArray = json.getAsJsonArray("schemas");
            for (int i = 0; i < schemasArray.size(); i++) {
                Schema schema = DEFAULT_GSON.fromJson(schemasArray.get(i), Schema.class);
                builder.addSchema(schema);
            }
            if (json.has("installationID")) {
                builder.setInstallationID(json.get("installationID").getAsString());
            }
            builder.setTag(tag);
            return builder.build();
        }
    };
    private static final JsonSerializer<TriggeredServerCodeResult> TRIGGERED_SERVER_CODE_RESULT_SERIALIZER = new JsonSerializer<TriggeredServerCodeResult>() {
        @Override
        public JsonElement serialize(TriggeredServerCodeResult src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == null) {
                return null;
            }
            JsonObject json = new JsonObject();
            json.addProperty("succeeded", src.isSucceeded());
            json.addProperty("executedAt", src.getExecutedAt());
            if (!TextUtils.isEmpty(src.getEndpoint())) {
                json.addProperty("endpoint", src.getEndpoint());
            }
            if (src.hasReturnedValue()) {
                if (src.getReturnedValue() instanceof JSONObject) {
                    json.add("returnedValue", new JsonParser().parse(src.getReturnedValueAsJsonObject().toString()));
                } else if (src.getReturnedValue() instanceof JSONArray) {
                    json.add("returnedValue", new JsonParser().parse(src.getReturnedValueAsJsonArray().toString()));
                } else if (src.getReturnedValue() instanceof String) {
                    json.addProperty("returnedValue", src.getReturnedValueAsString());
                } else if (src.getReturnedValue() instanceof Boolean) {
                    json.addProperty("returnedValue", src.getReturnedValueAsBoolean());
                } else if (src.getReturnedValue() instanceof Long) {
                    json.addProperty("returnedValue", src.getReturnedValueAsNumber());
                }
            }
            if (src.getError() != null) {
                JsonObject error = new JsonObject();
                error.addProperty("errorMessage", src.getError().getErrorMessage());
                JsonObject details = new JsonObject();
                details.addProperty("errorCode", src.getError().getErrorCode());
                details.addProperty("message", src.getError().getDetailMessage());
                error.add("details", details);
                json.add("error", error);
            }
            return json;
        }
    };
    private static final JsonDeserializer<TriggeredServerCodeResult> TRIGGERED_SERVER_CODE_RESULT_DESERIALIZER = new JsonDeserializer<TriggeredServerCodeResult>() {
        @Override
        public TriggeredServerCodeResult deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (jsonElement == null) {
                return null;
            }
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
                    } catch (JSONException ignore) {
                    }
                } else if (json.get("returnedValue").isJsonArray()) {
                    try {
                        returnedValue = new JSONArray(json.get("returnedValue").getAsJsonArray().toString());
                    } catch (JSONException ignore) {
                    }
                } else if (json.get("returnedValue").isJsonPrimitive()) {
                    JsonPrimitive primitive = json.get("returnedValue").getAsJsonPrimitive();
                    if (primitive.isString()) {
                        returnedValue = primitive.getAsString();
                    } else if (primitive.isBoolean()) {
                        returnedValue = primitive.getAsBoolean();
                    } else if (primitive.isNumber()) {
                        String numberStringValue = primitive.getAsNumber().toString();
                        if (numberStringValue.contains(".")) {
                            returnedValue = Double.parseDouble(numberStringValue);
                        } else {
                            try {
                                returnedValue = Integer.parseInt(numberStringValue);
                            } catch (NumberFormatException e) {
                                returnedValue = Long.parseLong(numberStringValue);
                            }
                        }
                    }
                }
            }
            ServerError error = null;
            if (json.has("error") && !json.get("error").isJsonNull()) {
                try {
                    JSONObject e = new JSONObject(json.get("error").getAsJsonObject().toString());
                    error = new ServerError(e);
                } catch (JSONException ignore) {
                }
            }
            TriggeredServerCodeResult result = new TriggeredServerCodeResult(succeeded, returnedValue, executedAt, endpoint, error);
            return result;
        }
    };
    private static final JsonSerializer<Target> TARGET_SERIALIZER = new JsonSerializer<Target>() {
        @Override
        public JsonElement serialize(Target src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == null) {
                return null;
            }
            JsonObject json = new JsonObject();
            json.addProperty("typedID", src.getTypedID().toString());
            json.addProperty("class", src.getClass().getName());
            if (!TextUtils.isEmpty(src.getAccessToken())) {
                json.addProperty("accessToken", src.getAccessToken());
            }
            if (src instanceof TargetThing) {
                json.addProperty("vendorThingID", ((TargetThing)src).getVendorThingID());
            }
            return json;
        }
    };
    private static final JsonDeserializer<Target> TARGET_DESERIALIZER = new JsonDeserializer<Target>() {
        @Override
        public Target deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (jsonElement == null) {
                return null;
            }
            JsonObject json = (JsonObject)jsonElement;
            String accessToken = json.has("accessToken") ? json.get("accessToken").getAsString() : null;
            String vendorThingID = json.has("vendorThingID") ? json.get("vendorThingID").getAsString() : null;
            String className = json.get("class").getAsString();
            TypedID typedID = TypedID.fromString(json.get("typedID").getAsString());
            if (StandaloneThing.class.getName().equals(className)) {
                return new StandaloneThing(typedID.getID(), vendorThingID, accessToken);
            } else if (Gateway.class.getName().equals(className)) {
                return new Gateway(typedID.getID(), vendorThingID);
            } else if (EndNode.class.getName().equals(className)) {
                return new EndNode(typedID.getID(), vendorThingID, accessToken);
            }
            throw new JsonParseException("Detected unknown type " + className);
        }
    };

    private static final JsonSerializer<Uri> URI_SERIALIZER = new JsonSerializer<Uri>() {
        @Override
        public JsonElement serialize(Uri src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            json.addProperty("uri", src.toString());
            return json;
        }
    };

    private static final JsonDeserializer<Uri> URI_DESERIALIZER = new JsonDeserializer<Uri>() {
        @Override
        public Uri deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject json = (JsonObject)jsonElement;
            if (json.has("uri")) {
                return Uri.parse(json.get("uri").getAsString());
            }
            return null;
        }
    };

    static {
        DEFAULT_GSON = new GsonBuilder()
                .registerTypeAdapter(JSONObject.class, ORG_JSON_OBJECT_SERIALIZER)
                .registerTypeAdapter(JSONObject.class, ORG_JSON_OBJECT_DESERIALIZER)
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
                .registerTypeAdapter(ThingIFAPI.class, IOT_CLOUD_API_SERIALIZER)
                .registerTypeAdapter(ThingIFAPI.class, IOT_CLOUD_API_DESERIALIZER)
                .registerTypeAdapter(TriggeredServerCodeResult.class, TRIGGERED_SERVER_CODE_RESULT_SERIALIZER)
                .registerTypeAdapter(TriggeredServerCodeResult.class, TRIGGERED_SERVER_CODE_RESULT_DESERIALIZER)
                .registerTypeAdapter(Target.class, TARGET_DESERIALIZER)
                .registerTypeAdapter(StandaloneThing.class, TARGET_SERIALIZER)
                .registerTypeAdapter(Gateway.class, TARGET_SERIALIZER)
                .registerTypeAdapter(EndNode.class, TARGET_SERIALIZER)
                .registerTypeAdapter(Uri.class, URI_SERIALIZER)
                .registerTypeAdapter(Uri.class, URI_DESERIALIZER)
                .create();
    }

    /**
     * Returns the default Gson instance.
     *
     * @return
     */
    public static Gson gson() {
        return gson(null);
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
                    .registerTypeAdapter(JSONObject.class, ORG_JSON_OBJECT_SERIALIZER)
                    .registerTypeAdapter(JSONObject.class, ORG_JSON_OBJECT_DESERIALIZER)
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
                    .registerTypeAdapter(ThingIFAPI.class, IOT_CLOUD_API_SERIALIZER)
                    .registerTypeAdapter(ThingIFAPI.class, IOT_CLOUD_API_DESERIALIZER)
                    .registerTypeAdapter(TriggeredServerCodeResult.class, TRIGGERED_SERVER_CODE_RESULT_SERIALIZER)
                    .registerTypeAdapter(TriggeredServerCodeResult.class, TRIGGERED_SERVER_CODE_RESULT_DESERIALIZER)
                    .registerTypeAdapter(Target.class, TARGET_DESERIALIZER)
                    .registerTypeAdapter(StandaloneThing.class, TARGET_SERIALIZER)
                    .registerTypeAdapter(Gateway.class, TARGET_SERIALIZER)
                    .registerTypeAdapter(EndNode.class, TARGET_SERIALIZER)
                    .registerTypeAdapter(Uri.class, URI_SERIALIZER)
                    .registerTypeAdapter(Uri.class, URI_DESERIALIZER)
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
