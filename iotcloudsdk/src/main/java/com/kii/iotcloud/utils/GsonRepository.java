package com.kii.iotcloud.utils;

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
import com.kii.iotcloud.trigger.Condition;
import com.kii.iotcloud.trigger.EventSource;
import com.kii.iotcloud.trigger.Predicate;
import com.kii.iotcloud.trigger.Schedule;
import com.kii.iotcloud.trigger.SchedulePredicate;
import com.kii.iotcloud.trigger.StatePredicate;
import com.kii.iotcloud.trigger.TriggersWhen;
import com.kii.iotcloud.trigger.statement.And;
import com.kii.iotcloud.trigger.statement.Equals;
import com.kii.iotcloud.trigger.statement.GreaterThan;
import com.kii.iotcloud.trigger.statement.GreaterThanOrEquals;
import com.kii.iotcloud.trigger.statement.LessThan;
import com.kii.iotcloud.trigger.statement.LessThanOrEquals;
import com.kii.iotcloud.trigger.statement.NotEquals;
import com.kii.iotcloud.trigger.statement.Or;
import com.kii.iotcloud.trigger.statement.Statement;

import org.json.JSONArray;

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
    private static final JsonSerializer<Predicate> PREDICATE_SERIALIZER = new JsonSerializer<Predicate>() {
        @Override
        public JsonElement serialize(Predicate src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == null) {
                return null;
            }
            JsonObject json = new JsonObject();
            json.addProperty("eventSource", src.getEventSource().name());
            if (src.getEventSource() == EventSource.SCHEDULE) {
                json.addProperty("schedule", ((SchedulePredicate)src).getSchedule().getCronExpression());
            } else if (src.getEventSource() == EventSource.STATE) {
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
            EventSource eventSource = EventSource.valueOf(json.get("eventSource").getAsString());
            Predicate predicate = null;
            if (eventSource == EventSource.SCHEDULE) {
                predicate = new SchedulePredicate(new Schedule(json.get("schedule").getAsString()));
            } else if (eventSource == EventSource.STATE) {
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
            return context.serialize(src.getStatement());
        }
    };
    private static final JsonDeserializer<Condition> CONDITION_DESERIALIZER = new JsonDeserializer<Condition>() {
        @Override
        public Condition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json == null) {
                return null;
            }
            Statement statement = context.deserialize(json, Statement.class);
            return new Condition(statement);
        }
    };
    private static final JsonSerializer<Statement> STATEMENT_SERIALIZER = new JsonSerializer<Statement>() {
        @Override
        public JsonElement serialize(Statement src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == null) {
                return null;
            }
            return new JsonParser().parse(src.toJSONObject().toString());
        }
    };
    private static final JsonDeserializer<Statement> STATEMENT_DESERIALIZER = new JsonDeserializer<Statement>() {
        @Override
        public Statement deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
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
                }
            } else if (TextUtils.equals("not", type)) {
                Equals eq = context.deserialize(new JsonParser().parse(json.get("clause").toString()), Equals.class);
                return new NotEquals(eq);
            } else if (TextUtils.equals("and", type) || TextUtils.equals("or", type)) {
                List<Statement> statements = new ArrayList<Statement>();
                JsonArray clauses = json.getAsJsonArray("clauses");
                for (int i = 0; i < clauses.size(); i++) {
                    Statement statement = context.deserialize(clauses.get(i), Statement.class);
                    statements.add(statement);
                }
                if (TextUtils.equals("and", type)) {
                    return new And(statements.toArray(new Statement[statements.size()]));
                } else if (TextUtils.equals("or", type)) {
                    return new Or(statements.toArray(new Statement[statements.size()]));
                }
            } else if (TextUtils.equals("range", type)) {
                String field = json.get("field").getAsString();
                if (json.has("upperLimit")) {
                    long upperLimit = json.get("upperLimit").getAsLong();
                    if (json.get("upperLimitIncluded").getAsBoolean()) {
                        return new GreaterThanOrEquals(field, upperLimit);
                    } else {
                        return new GreaterThan(field, upperLimit);
                    }
                } else if (json.has("lowerLimit")) {
                    long lowerLimit = json.get("lowerLimit").getAsLong();
                    if (json.get("lowerLimitIncluded").getAsBoolean()) {
                        return new LessThanOrEquals(field, lowerLimit);
                    } else {
                        return new LessThan(field, lowerLimit);
                    }
                }
            }
            throw new JsonParseException(jsonElement.toString());
        }
    };


    static {
        DEFAULT_GSON = new GsonBuilder()
                .registerTypeAdapter(TypedID.class, TYPED_ID_SERIALIZER)
                .registerTypeAdapter(TypedID.class, TYPED_ID_DESERIALIZER)
                .registerTypeAdapter(Action.class, ACTION_SERIALIZER)
                .registerTypeAdapter(ActionResult.class, ACTION_RESULT_SERIALIZER)
                .registerTypeHierarchyAdapter(Statement.class, STATEMENT_SERIALIZER)
                .registerTypeHierarchyAdapter(Statement.class, STATEMENT_DESERIALIZER)
//                .registerTypeAdapter(And.class, STATEMENT_SERIALIZER)
//                .registerTypeAdapter(And.class, STATEMENT_DESERIALIZER)
                .registerTypeAdapter(Condition.class, CONDITION_SERIALIZER)
                .registerTypeAdapter(Condition.class, CONDITION_DESERIALIZER)
                .registerTypeAdapter(SchedulePredicate.class, PREDICATE_SERIALIZER)
                .registerTypeAdapter(SchedulePredicate.class, PREDICATE_DESERIALIZER)
                .create();
    }

    /**
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
                    .registerTypeAdapter(Action.class, ACTION_SERIALIZER)
                    .registerTypeAdapter(ActionResult.class, ACTION_RESULT_SERIALIZER)
                    .registerTypeHierarchyAdapter(Statement.class, STATEMENT_SERIALIZER)
                    .registerTypeHierarchyAdapter(Statement.class, STATEMENT_DESERIALIZER)
//                    .registerTypeAdapter(And.class, STATEMENT_SERIALIZER)
//                    .registerTypeAdapter(And.class, STATEMENT_DESERIALIZER)
                    .registerTypeAdapter(Condition.class, CONDITION_SERIALIZER)
                    .registerTypeAdapter(Condition.class, CONDITION_DESERIALIZER)
                    .registerTypeAdapter(Predicate.class, PREDICATE_SERIALIZER)
                    .registerTypeAdapter(Predicate.class, PREDICATE_DESERIALIZER)
                    .registerTypeAdapter(Action.class, ACTION_DESERIALIZER)
                    .registerTypeAdapter(ActionResult.class, ACTION_RESULT_DESERIALIZER)
                    .create();
            REPOSITORY.put(new Pair<String, Integer>(schema.getSchemaName(), schema.getSchemaVersion()), gson);
        }
        return gson;
    }
}
