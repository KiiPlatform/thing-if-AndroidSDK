package com.kii.thingif.internal.clause;

import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public abstract class ContainerClause implements Clause {
    protected final List<Clause> clauses = new ArrayList<>();
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
