package com.kii.thingif.internal.gson;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.kii.thingif.clause.query.AllClause;
import com.kii.thingif.clause.query.AndClauseInQuery;
import com.kii.thingif.clause.query.EqualsClauseInQuery;
import com.kii.thingif.clause.query.NotEqualsClauseInQuery;
import com.kii.thingif.clause.query.OrClauseInQuery;
import com.kii.thingif.clause.query.QueryClause;
import com.kii.thingif.clause.query.RangeClauseInQuery;

import java.lang.reflect.Type;

public class QueryClauseAdapter implements
        JsonSerializer<QueryClause>{

    private JsonObject queryClauseToJson(QueryClause clause) {
        String type;
        JsonObject ret;
        if (clause instanceof EqualsClauseInQuery ||
                clause instanceof RangeClauseInQuery ||
                clause instanceof AllClause ) {
            ret = new Gson().toJsonTree(clause).getAsJsonObject();
            if (clause instanceof EqualsClauseInQuery) {
                type = "eq";
            } else if (clause instanceof RangeClauseInQuery){
                type = "range";
            } else {
                type = "all";
            }
        } else {
            ret = new JsonObject();
            if (clause instanceof NotEqualsClauseInQuery) {
                NotEqualsClauseInQuery neq = (NotEqualsClauseInQuery) clause;
                ret.add("clause", queryClauseToJson(neq.getEquals()));
                type = "not";
            } else if (clause instanceof AndClauseInQuery) {
                JsonArray clauses = new JsonArray();
                for (QueryClause subClause : ((AndClauseInQuery) clause).getClauses()) {
                    clauses.add(queryClauseToJson(subClause));
                }
                ret.add("clauses", clauses);
                type = "and";
            } else if (clause instanceof OrClauseInQuery) {
                JsonArray clauses = new JsonArray();
                for (QueryClause subClause : ((OrClauseInQuery) clause).getClauses()) {
                    clauses.add(queryClauseToJson(subClause));
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
    public JsonElement serialize(QueryClause src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) return null;
        return queryClauseToJson(src);
    }
}
