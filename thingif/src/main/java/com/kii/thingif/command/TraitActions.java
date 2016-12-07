package com.kii.thingif.command;

import java.io.Serializable;
import java.util.List;

public abstract class TraitActions extends CommandActionItem implements Serializable {

    public abstract String getAlias();

    public abstract List<Action> getActions();
}
