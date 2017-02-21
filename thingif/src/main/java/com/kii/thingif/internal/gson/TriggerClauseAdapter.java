package com.kii.thingif.internal.gson;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.kii.thingif.clause.trigger.AndClauseInTrigger;
import com.kii.thingif.clause.trigger.EqualsClauseInTrigger;
import com.kii.thingif.clause.trigger.NotEqualsClauseInTrigger;
import com.kii.thingif.clause.trigger.OrClauseInTrigger;
import com.kii.thingif.clause.trigger.RangeClauseInTrigger;
import com.kii.thingif.clause.trigger.TriggerClause;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

class TriggerClauseAdapter implements
        JsonSerializer<TriggerClause>,
        JsonDeserializer<TriggerClause>{

    private JSONObject triggerClauseToJson(TriggerClause clause) throws JSONException {
        JSONObject ret = new JSONObject();
        if (clause instanceof EqualsClauseInTrigger) {
            EqualsClauseInTrigger eq = (EqualsClauseInTrigger)clause;
            ret.put("type", "eq");
            ret.put("alias", eq.getAlias());
            ret.put("field", eq.getField());
            ret.put("value", eq.getValue());
        }else if (clause instanceof RangeClauseInTrigger) {
            RangeClauseInTrigger range = (RangeClauseInTrigger)clause;
            ret.put("type", "range");
            ret.put("alias", range.getAlias());
            ret.put("field", range.getField());
            ret.putOpt("lowerLimit", range.getLowerLimit());
            ret.putOpt("upperLimit", range.getUpperLimit());
            ret.putOpt("lowerIncluded", range.getLowerIncluded());
            ret.putOpt("upperIncluded", range.getUpperIncluded());
        } else if(clause instanceof NotEqualsClauseInTrigger) {
            NotEqualsClauseInTrigger neq = (NotEqualsClauseInTrigger)clause;
            ret.put("type", "not").put("clause", triggerClauseToJson(neq.getEquals()));
        } else if (clause instanceof AndClauseInTrigger) {
            JSONArray clauses = new JSONArray();
            for (TriggerClause subClause : ((AndClauseInTrigger)clause).getClauses()) {
                clauses.put(triggerClauseToJson(subClause));
            }
            ret.put("type", "and").put("clauses", clauses);
        } else if (clause instanceof OrClauseInTrigger) {
            JSONArray clauses = new JSONArray();
            for (TriggerClause subClause : ((OrClauseInTrigger) clause).getClauses()) {
                clauses.put(triggerClauseToJson(subClause));
            }
            ret.put("type", "or").put("clauses", clauses);
        }else{
            throw new RuntimeException("not support trigger clause");
        }
        return ret;
    }

    @Override
    public JsonElement serialize(TriggerClause src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) return null;
        try{
            return new Gson()
                    .toJsonTree(new JsonParser().parse(triggerClauseToJson(src).toString()));
        }catch (JSONException e) {
            throw new RuntimeException(e);
        }
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
