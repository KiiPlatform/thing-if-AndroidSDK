package com.kii.thingif.clause.base;

import java.util.List;

public interface BaseOr<T extends BaseClause> extends BaseClause {
    List<T> getClauses();
    BaseOr<T> addClause(T clause);
}
