package com.kii.thingif.temporal.trigger;

import com.kii.thingif.temporal.base.BaseAnd;
import com.kii.thingif.temporal.base.BaseClause;

import org.json.JSONObject;

import java.util.List;

public class AndClauseInTrigger implements BaseAnd<TriggerClause>, TriggerClause {

    @Override
    public List<TriggerClause> getClauses() {
        return null;
    }

    @Override
    public void addClause(TriggerClause clause) {

    }

    @Override
    public JSONObject toJSONObject() {
        return null;
    }
}
