package com.kii.thingif.query;

import com.kii.thingif.TargetState;

import java.util.List;

public class HistoryStatesResponse {
    private String queryDescription;
    private List results;

    public HistoryStatesResponse(
            String queryDescription,
            boolean grouped,
            List<TargetState> states
    ){
        this.queryDescription = queryDescription;
        this.results = states;
    }
}
