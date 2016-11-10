package com.kii.thingif.query;

import com.kii.thingif.TargetState;

import java.util.Date;
import java.util.List;

public class GroupedHistoryStatesResponse extends HistoryStatesResponse {
    public class GroupedResult {
        private Date rangeFrom;
        private Date rangeTo;
        private List<TargetState> states;
        public GroupedResult(
                Date rangeFrom,
                Date rangeTo,
                List<TargetState> states
        ){
            this.rangeFrom = rangeFrom;
            this.rangeTo = rangeTo;
            this.states = states;
        }
    }

    public GroupedHistoryStatesResponse(
            String queryDescription,
            boolean grouped,
            List<GroupedResult> results,
            List<AggregationResult> aggregations){
        this.queryDescription = queryDescription;
        this.grouped = grouped;
        this.results = results;
        this.aggregations = aggregations;
    }
}
