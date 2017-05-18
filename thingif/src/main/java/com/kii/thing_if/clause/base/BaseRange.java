package com.kii.thing_if.clause.base;

public interface BaseRange extends BaseClause {

    String getField();
    Number getUpperLimit();
    Number getLowerLimit();
    Boolean getUpperIncluded();
    Boolean getLowerIncluded();
}
