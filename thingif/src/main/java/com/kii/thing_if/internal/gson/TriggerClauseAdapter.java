package com.kii.thing_if.internal.gson;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.kii.thing_if.clause.trigger.AndClauseInTrigger;
import com.kii.thing_if.clause.trigger.EqualsClauseInTrigger;
import com.kii.thing_if.clause.trigger.NotEqualsClauseInTrigger;
import com.kii.thing_if.clause.trigger.OrClauseInTrigger;
import com.kii.thing_if.clause.trigger.RangeClauseInTrigger;
import com.kii.thing_if.clause.trigger.TriggerClause;

import java.lang.reflect.Type;

class TriggerClauseAdapter implements
        JsonSerializer<TriggerClause>,
        JsonDeserializer<TriggerClause>{

    private JsonObject triggerClauseToJson(TriggerClause clause) {
        String type;
        JsonObject ret;
        if (clause instanceof EqualsClauseInTrigger ||
                clause instanceof RangeClauseInTrigger) {
            ret = new Gson().toJsonTree(clause).getAsJsonObject();
            if (clause instanceof EqualsClauseInTrigger) {
                type = "eq";
            }else {
                type = "range";
            }
        } else {
            ret = new JsonObject();
            if (clause instanceof NotEqualsClauseInTrigger) {
                NotEqualsClauseInTrigger neq = (NotEqualsClauseInTrigger) clause;
                ret.add("clause", triggerClauseToJson(neq.getEquals()));
                type = "not";
            } else if (clause instanceof AndClauseInTrigger) {
                JsonArray clauses = new JsonArray();
                for (TriggerClause subClause : ((AndClauseInTrigger) clause).getClauses()) {
                    clauses.add(triggerClauseToJson(subClause));
                }
                ret.add("clauses", clauses);
                type = "and";
            } else if (clause instanceof OrClauseInTrigger) {
                JsonArray clauses = new JsonArray();
                for (TriggerClause subClause : ((OrClauseInTrigger) clause).getClauses()) {
                    clauses.add(triggerClauseToJson(subClause));
                }
                ret.add("clauses", clauses);
                type = "or";
            } else {
                throw new RuntimeException("not support trigger clause");
            }
        }
        ret.addProperty("type", type);
        return ret;
    }

    @Override
    public JsonElement serialize(TriggerClause src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) return null;
        return triggerClauseToJson(src);
    }

    @Override
    public TriggerClause deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (jsonElement == null || !jsonElement.isJsonObject())return null;
        JsonObject json = jsonElement.getAsJsonObject();
        String type = json.get("type").getAsString();
        switch (type){
            case "eq":
                return new Gson().fromJson(json, EqualsClauseInTrigger.class);
            case "not":
                EqualsClauseInTrigger eq =
                        context.deserialize(
                                json.get("clause").getAsJsonObject(),
                                EqualsClauseInTrigger.class);
                return new NotEqualsClauseInTrigger(eq);
            case "range":
                return new Gson().fromJson(json, RangeClauseInTrigger.class);
            case "and":
                AndClauseInTrigger andClause = new AndClauseInTrigger();
                JsonArray clauses = json.getAsJsonArray("clauses");
                for (JsonElement clause: clauses){
                    TriggerClause deserializedClause =
                            context.deserialize(clause, TriggerClause.class);
                    andClause.addClause(deserializedClause);
                }
                return andClause;
            case "or":
                OrClauseInTrigger orClause = new OrClauseInTrigger();
                JsonArray subClauses = json.getAsJsonArray("clauses");
                for (JsonElement clause: subClauses){
                    TriggerClause deserializedClause =
                            context.deserialize(clause, TriggerClause.class);
                    orClause.addClause(deserializedClause);
                }
                return orClause;
            default:
                throw new JsonParseException("not supported clause");
        }
    }
}
