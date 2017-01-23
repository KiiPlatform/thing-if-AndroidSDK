package com.kii.thingif.clause.base;

import java.util.List;

public interface BaseAnd<T extends BaseClause> extends BaseClause {
    List<T> getClauses();
    void addClause(T clause);
}
