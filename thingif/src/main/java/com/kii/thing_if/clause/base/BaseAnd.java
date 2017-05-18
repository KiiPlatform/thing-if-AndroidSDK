package com.kii.thing_if.clause.base;

import java.util.List;

public interface BaseAnd<T extends BaseClause> extends BaseClause {
    List<T> getClauses();
    BaseAnd<T> addClause(T clause);
}
