package com.kii.thingif.clause.query;

import com.kii.thingif.clause.base.BaseNotEquals;

import org.json.JSONObject;

public class NotEqualsClauseInQuery implements BaseNotEquals, QueryClause {

    private EqualsClauseInQuery equals;

    public NotEqualsClauseInQuery(EqualsClauseInQuery equals) {
        this.equals = equals;
    }

    @Override
    public EqualsClauseInQuery getEquals() {
        return this.equals;
    }

}
