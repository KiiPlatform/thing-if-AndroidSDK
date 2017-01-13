package com.kii.thingif.temporal.base;

import java.util.List;

public interface BaseAnd<T> {
    List<T> getClauses();
    void addClause(T clause);
}