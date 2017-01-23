package com.kii.thingif.clause.trigger;

import com.kii.thingif.clause.base.BaseOr;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrClauseInTrigger implements BaseOr<TriggerClause>, TriggerClause {
    private List<TriggerClause> clauses = new ArrayList<>();

    public OrClauseInTrigger(TriggerClause ...clauses){
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
    public OrClauseInTrigger addClause(TriggerClause clause) {
        this.clauses.add(clause);
        return this;
    }

    @Override
    public JSONObject toJSONObject() {
        // TODO: implement me
        return null;
    }
}
