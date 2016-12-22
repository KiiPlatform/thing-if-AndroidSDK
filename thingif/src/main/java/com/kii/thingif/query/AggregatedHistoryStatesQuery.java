package com.kii.thingif.query;

import android.support.annotation.NonNull;

public class AggregatedHistoryStatesQuery {
    private final @NonNull GroupedHistoryStatesQuery groupedQuery;
    private final @NonNull Aggregation aggregation;

    public AggregatedHistoryStatesQuery(
            @NonNull GroupedHistoryStatesQuery groupedQuery,
            @NonNull Aggregation aggregation) {
        this.groupedQuery = groupedQuery;
        this.aggregation = aggregation;
    }

    @NonNull
    public GroupedHistoryStatesQuery getGroupedQuery() {
        return this.groupedQuery;
    }

    @NonNull
    public Aggregation getAggregation() {
        return this.aggregation;
    }
}

