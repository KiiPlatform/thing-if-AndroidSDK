package com.kii.thingif.command;

import java.util.List;

public class TraitActionResults {
    private String alias;
    private List<ActionResult> actionResults;

    public TraitActionResults(String alias, List<ActionResult> actionResults) {
        this.actionResults = actionResults;
        this.alias = alias;
    }

    public List<ActionResult> getActionResults() {
        return actionResults;
    }

    public String getAlias() {
        return alias;
    }
}
