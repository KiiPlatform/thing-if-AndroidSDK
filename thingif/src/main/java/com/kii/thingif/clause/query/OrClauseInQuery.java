package com.kii.thingif.clause.query;

import com.kii.thingif.clause.base.BaseOr;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrClauseInQuery implements BaseOr<QueryClause>, QueryClause {
    private List<QueryClause> clauses = new ArrayList<>();

    public OrClauseInQuery(QueryClause ...clauses){
        if(clauses != null) {
            for(QueryClause clause : clauses) {
                this.clauses.add(clause);
            }
        }
    }

    @Override
    public List<QueryClause> getClauses() {
        return this.clauses;
    }

    @Override
    public void addClause(QueryClause clause) {
        this.clauses.add(clause);
    }

    @Override
    public JSONObject toJSONObject() {
        // TODO: implement me
        return null;
    }
}
