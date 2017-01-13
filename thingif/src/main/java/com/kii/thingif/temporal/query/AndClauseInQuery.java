package com.kii.thingif.temporal.query;

import com.kii.thingif.temporal.base.BaseAnd;
import com.kii.thingif.temporal.base.BaseClause;
import com.kii.thingif.temporal.trigger.*;

import org.json.JSONObject;

import java.util.List;

public class AndClauseInQuery implements BaseAnd<QueryClause>, QueryClause {
    public void add(QueryClause clause) {

    }
    @Override
    public List<QueryClause> getClauses() {
        return null;
    }

    @Override
    public void addClause(QueryClause clause) {

    }

    @Override
    public JSONObject toJSONObject() {
        return null;
    }

}
