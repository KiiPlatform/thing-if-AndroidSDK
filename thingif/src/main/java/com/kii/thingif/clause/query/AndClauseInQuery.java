package com.kii.thingif.clause.query;

import android.support.annotation.NonNull;

import com.kii.thingif.clause.base.BaseAnd;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AndClauseInQuery implements BaseAnd<QueryClause>, QueryClause {
    private List<QueryClause> clauses = new ArrayList<>();

    public AndClauseInQuery(@NonNull QueryClause ...clauses){
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
    public AndClauseInQuery addClause(@NonNull QueryClause clause) {
        this.clauses.add(clause);
        return this;
    }
}
