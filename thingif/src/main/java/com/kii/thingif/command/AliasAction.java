package com.kii.thingif.command;

public class AliasAction<T extends Action> {
    private String alias;
    private T action;

    public AliasAction(String alias, T action) {
        this.alias = alias;
        this.action = action;
    }

    public String getAlias() {
        return alias;
    }

    public T getAction() {
        return action;
    }
}
