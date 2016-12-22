package com.kii.thingif.query;

import android.support.annotation.NonNull;

public class AggregatedHistoryStatesQuery<T> {
    private final @NonNull GroupedHistoryStatesQuery groupedQuery;
    private final @NonNull Aggregation<T> aggregation;

    public AggregatedHistoryStatesQuery(
            @NonNull GroupedHistoryStatesQuery groupedQuery,
            @NonNull Aggregation<T> aggregation) {
        this.groupedQuery = groupedQuery;
        this.aggregation = aggregation;
    }

    @NonNull
    public GroupedHistoryStatesQuery getGroupedQuery() {
        return this.groupedQuery;
    }

    @NonNull
    public Aggregation<T> getAggregation() {
        return this.aggregation;
    }
}

