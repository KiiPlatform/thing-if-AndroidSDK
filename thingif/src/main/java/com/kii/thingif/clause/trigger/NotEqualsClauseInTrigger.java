package com.kii.thingif.clause.trigger;

import com.kii.thingif.clause.base.BaseNotEquals;

import org.json.JSONObject;

public class NotEqualsClauseInTrigger implements BaseNotEquals, TriggerClause {
    private EqualsClauseInTrigger equals;

    public NotEqualsClauseInTrigger(EqualsClauseInTrigger equals) {
        this.equals = equals;
    }

    @Override
    public EqualsClauseInTrigger getEquals() {
        return this.equals;
    }

    @Override
    public JSONObject toJSONObject() {
        //TODO: implement me
        return null;
    }
}
