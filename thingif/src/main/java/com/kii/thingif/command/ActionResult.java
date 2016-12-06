package com.kii.thingif.command;

import java.io.Serializable;

/**
 * Represents base cass of ActionResult.
 * Subclass must have the default constructor.
 */
public abstract class ActionResult extends CommandActionResultItem implements Serializable {
    public String errorMessage;
    public boolean succeeded;

    public boolean succeeded() {
        return this.succeeded;
    }
    public String getErrorMessage() {
        return this.errorMessage;
    }
    public abstract String getActionName();
}
