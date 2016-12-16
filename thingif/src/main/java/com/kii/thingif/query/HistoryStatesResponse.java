package com.kii.thingif.query;

import com.kii.thingif.TargetState;

import java.util.List;

public class HistoryStatesResponse<S extends TargetState> {
    private String queryDescription;
    private List<S> results;

    public HistoryStatesResponse(
            String queryDescription,
            List<S> states
    ){
        this.queryDescription = queryDescription;
        this.results = states;
    }

    public List<S> getResults() {
        return results;
    }

    public String getQueryDescription() {
        return queryDescription;
    }
}
