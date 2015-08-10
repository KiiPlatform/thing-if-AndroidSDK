package com.kii.iotcloud.command;

public abstract class Action {
    private final String name;
    public Action(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }
}
