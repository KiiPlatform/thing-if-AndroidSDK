package com.kii.thingif.clause.trigger;

import android.support.annotation.NonNull;

import com.kii.thingif.clause.base.BaseAnd;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AndClauseInTrigger implements BaseAnd<TriggerClause>, TriggerClause {
    private List<TriggerClause> clauses = new ArrayList<>();

    public AndClauseInTrigger(@NonNull TriggerClause ...clauses){
        if(clauses != null) {
            for(TriggerClause clause : clauses) {
                this.clauses.add(clause);
            }
        }
    }

    @Override
    public List<TriggerClause> getClauses() {
        return this.clauses;
    }

    @Override
    public void addClause(@NonNull TriggerClause clause) {
        this.clauses.add(clause);
    }

    @Override
    public JSONObject toJSONObject() {
        // TODO: implement me
        return null;
    }
}
