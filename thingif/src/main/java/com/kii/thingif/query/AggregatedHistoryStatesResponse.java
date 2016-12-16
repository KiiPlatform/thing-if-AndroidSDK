package com.kii.thingif.query;

import com.kii.thingif.TargetState;

import java.util.Date;
import java.util.List;

public class AggregatedHistoryStatesResponse {
    private String queryDescription;
    private List<GroupedResult> results;

    public class GroupedResult {
        private Date rangeFrom;
        private Date rangeTo;
        private Object value;
        private String name;
        private List<TargetState> states;
        public GroupedResult(
                Date rangeFrom,
                Date rangeTo,
                Object value,
                String name,
                List<TargetState> states
        ){
            this.rangeFrom = rangeFrom;
            this.rangeTo = rangeTo;
            this.value = value;
            this.name = name;
            this.states = states;
        }

        public Date getRangeFrom() {
            return rangeFrom;
        }

        public Date getRangeTo() {
            return rangeTo;
        }

        public Object getValue() {
            return value;
        }

        public String getName() {
            return name;
        }

        public List<TargetState> getStates() {
            return states;
        }
    }

    public AggregatedHistoryStatesResponse(
            String queryDescription,
            List<GroupedResult> results){
        this.queryDescription = queryDescription;
        this.results = results;
    }

    public String getQueryDescription() {
        return queryDescription;
    }

    public List<GroupedResult> getResults() {
        return results;
    }
}
