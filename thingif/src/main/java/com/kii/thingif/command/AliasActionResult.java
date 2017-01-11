package com.kii.thingif.command;

import java.util.List;

public class AliasActionResult {
    private String alias;
    private List<ActionResult> results;

    public AliasActionResult(
            String alias,
            List<ActionResult> results) {
        this.alias = alias;
        this.results = results;
    }

    public List<ActionResult> getResults() {
        return results;
    }

    public String getAlias() {
        return alias;
    }
}
