package com.kii.thingif.command;

import org.json.JSONObject;

import java.util.List;

public final class CommandForm {

    private final String schemaName;
    private final int schemaVersion;
    private final List<Action> actions;
    private String description;
    private JSONObject metadata;

    public CommandForm(String schemaName, int schemaVersion, List<Action> actions) {
        this.schemaName = schemaName;
        this.schemaVersion = schemaVersion;
        this.actions = actions;
    }
}
