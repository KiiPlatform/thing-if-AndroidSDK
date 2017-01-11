package com.kii.thingif.command;

public class AliasAction {
    private String alias;
    private Action action;

    public AliasAction(String alias, Action action) {
        this.alias = alias;
        this.action = action;
    }

    public String getAlias() {
        return alias;
    }

    public Action getAction() {
        return action;
    }
}
