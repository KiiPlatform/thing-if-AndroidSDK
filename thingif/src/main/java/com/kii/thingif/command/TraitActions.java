package com.kii.thingif.command;

import java.util.List;

public class TraitActions {
    private String alias;
    private List<Action> actions;

    public TraitActions(String alias, List<Action> actions) {
        this.alias = alias;
        this.actions = actions;
    }

    public String getAlias() {
        return alias;
    }

    public List<Action> getActions() {
        return actions;
    }
}
