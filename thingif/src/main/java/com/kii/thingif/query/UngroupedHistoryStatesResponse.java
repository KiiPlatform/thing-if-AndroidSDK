package com.kii.thingif.query;

import com.kii.thingif.TargetState;

import java.util.List;

public class UngroupedHistoryStatesResponse extends HistoryStatesResponse {

    public UngroupedHistoryStatesResponse(
            String queryDescription,
            boolean grouped,
            List<TargetState> states,
            List<AggregationResult> aggregations
    ){
        this.queryDescription = queryDescription;
        this.grouped = grouped;
        this.results = states;
        this.aggregations = aggregations;
    }
}
