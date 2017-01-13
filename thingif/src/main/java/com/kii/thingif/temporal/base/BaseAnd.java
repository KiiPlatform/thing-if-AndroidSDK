package com.kii.thingif.temporal.base;

import java.util.List;

public interface BaseAnd<T extends BaseClause> {
    List<T> getClauses();
    void addClause(T clause);
}