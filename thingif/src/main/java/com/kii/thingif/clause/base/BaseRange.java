package com.kii.thingif.clause.base;

public interface BaseRange extends BaseClause {

    String getField();
    Long getUpperLimit();
    Long getLowerLimit();
    Boolean getUpperIncluded();
    Boolean getLowerIncluded();
}
