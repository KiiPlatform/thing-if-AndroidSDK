package com.kii.thingiftrait.clause.base;

public interface BaseRange extends BaseClause {

    String getField();
    Number getUpperLimit();
    Number getLowerLimit();
    Boolean getUpperIncluded();
    Boolean getLowerIncluded();
}
