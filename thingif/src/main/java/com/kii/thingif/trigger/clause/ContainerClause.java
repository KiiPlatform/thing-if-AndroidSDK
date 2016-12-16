package com.kii.thingif.trigger.clause;

import com.kii.thingif.Alias;

import java.util.ArrayList;
import java.util.List;

public abstract class ContainerClause<T extends Alias> extends Clause<T> {
    protected final List<Clause<T>> clauses = new ArrayList<>();
    public ContainerClause(Clause<T>... clauses) {
        if (clauses != null) {
            for (Clause<T> clause : clauses) {
                this.clauses.add(clause);
            }
        }
    }
    public Clause[] getClauses() {
        return this.clauses.toArray(new Clause[this.clauses.size()]);
    }
    public void addClause(Clause<T> clause) {
        this.clauses.add(clause);
    }
    public boolean hasClause() {
        return this.clauses.size() > 0;
    }
}
