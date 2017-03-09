package com.kii.thingif.internal.gson;

import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.kii.thingif.clause.query.AndClauseInQuery;
import com.kii.thingif.clause.query.QueryClause;
import com.kii.thingif.query.Aggregation;
import com.kii.thingif.query.GroupedHistoryStatesQuery;

import java.lang.reflect.Type;

public class GroupedHistoryStatesQueryAdapter implements JsonSerializer<GroupedHistoryStatesQuery> {

    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(QueryClause.class, new QueryClauseAdapter())
            .create();

    private Aggregation aggregation;

    public GroupedHistoryStatesQueryAdapter(@Nullable Aggregation aggregation) {
        this.aggregation = aggregation;
    }

    @Override
    public JsonElement serialize(GroupedHistoryStatesQuery src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) {
            return null;
        }
        JsonObject json = new JsonObject();
        JsonObject query = new JsonObject();
        JsonObject timeRangeClause = new JsonObject();
        timeRangeClause.addProperty("type", "withinTimeRange");
        timeRangeClause.addProperty("lowerLimit", src.getTimeRange().getFrom().getTime());
        timeRangeClause.addProperty("upperLimit", src.getTimeRange().getTo().getTime());
        if (src.getClause() != null) {
            AndClauseInQuery andClause = new AndClauseInQuery(src.getClause());
            JsonObject clause = gson.toJsonTree(andClause, QueryClause.class).getAsJsonObject();
            JsonArray clauses = clause.getAsJsonArray("clauses");
            clauses.add(timeRangeClause);
            query.add("clause", clause);
        } else {
            query.add("clause", timeRangeClause);
        }
        query.addProperty("grouped", true);
        if (this.aggregation != null) {
            JsonArray aggregations = new JsonArray();
            JsonObject aggregation = gson.toJsonTree(this.aggregation, Aggregation.class).getAsJsonObject();
            aggregation.addProperty("putAggregationInto", this.aggregation.getFunction().name().toLowerCase());
            aggregations.add(aggregation);
            query.add("aggregations", aggregations);
        }
        json.add("query", query);
        if (src.getFirmwareVersion() != null) {
            json.addProperty("firmwareVersion", src.getFirmwareVersion());
        }
        return json;
    }
}
