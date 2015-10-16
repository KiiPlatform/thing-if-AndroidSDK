package com.kii.thingif.trigger.clause;

import java.util.ArrayList;
import java.util.List;

public abstract class ContainerClause extends Clause {
    protected final List<Clause> clauses = new ArrayList<Clause>();
    public ContainerClause(Clause... clauses) {
        if (clauses != null) {
            for (Clause clause : clauses) {
                this.clauses.add(clause);
            }
        }
    }
    public Clause[] getClauses() {
        return this.clauses.toArray(new Clause[this.clauses.size()]);
    }
    public void addClause(Clause clause) {
        this.clauses.add(clause);
    }
    public boolean hasClause() {
        return this.clauses.size() > 0;
    }
}
