package com.kii.thingif.query;

import java.util.List;

public abstract class HistoryStatesResponse {
    protected String queryDescription;
    protected boolean grouped;
    protected List results;
    protected List<AggregationResult> aggregations;
}