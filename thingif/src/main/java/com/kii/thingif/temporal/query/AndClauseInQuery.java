package com.kii.thingif.temporal.query;

import android.os.Parcel;
import android.os.Parcelable;

import com.kii.thingif.query.clause.And;
import com.kii.thingif.temporal.base.BaseAnd;
import com.kii.thingif.temporal.base.BaseClause;
import com.kii.thingif.temporal.trigger.*;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AndClauseInQuery extends BaseAnd<QueryClause> implements QueryClause {

    public AndClauseInQuery(QueryClause... clauses) {
        super(clauses);
    }
    @Override
    public List<QueryClause> getClauses() {
        return super.getClauses();
    }

    @Override
    public void addClause(QueryClause clause) {
        super.addClause(clause);
    }

    @Override
    public JSONObject toJSONObject() {
        return null;
    }

    private AndClauseInQuery(Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<AndClauseInQuery> CREATOR = new Parcelable.Creator<AndClauseInQuery>() {
        @Override
        public AndClauseInQuery createFromParcel(Parcel in) {
            return new AndClauseInQuery(in);
        }

        @Override
        public AndClauseInQuery[] newArray(int size) {
            return new AndClauseInQuery[size];
        }
    };

}
