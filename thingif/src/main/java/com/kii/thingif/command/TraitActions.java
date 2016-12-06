package com.kii.thingif.command;

import java.io.Serializable;
import java.util.List;

public abstract class TraitActions extends CommandActionItem implements Serializable {
    protected transient String alias;
    protected transient List<Action> actions;

    public String getAlias() {
        return alias;
    }

    public List<Action> getActions() {
        return actions;
    }
}
