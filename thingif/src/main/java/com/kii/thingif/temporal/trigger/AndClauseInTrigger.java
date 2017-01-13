package com.kii.thingif.temporal.trigger;

import android.os.Parcel;
import android.os.Parcelable;

import com.kii.thingif.temporal.base.BaseAnd;
import com.kii.thingif.temporal.base.BaseClause;

import org.json.JSONObject;

import java.util.List;

public class AndClauseInTrigger extends BaseAnd<TriggerClause> implements TriggerClause {

    public AndClauseInTrigger(TriggerClause... clauses) {
        super(clauses);
    }

    @Override
    public List<TriggerClause> getClauses() {
        return super.getClauses();
    }

    @Override
    public void addClause(TriggerClause clause) {
        super.addClause(clause);
    }

    @Override
    public JSONObject toJSONObject() {
        return null;
    }

    private AndClauseInTrigger(Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<AndClauseInTrigger> CREATOR = new Parcelable.Creator<AndClauseInTrigger>() {
        @Override
        public AndClauseInTrigger createFromParcel(Parcel in) {
            return new AndClauseInTrigger(in);
        }

        @Override
        public AndClauseInTrigger[] newArray(int size) {
            return new AndClauseInTrigger[size];
        }
    };
}
