package com.kii.thingif.command;

import java.io.Serializable;

/**
 * Represents ActionResult.
 */
public final class ActionResult implements Serializable {
    private String errorMessage;
    private boolean succeeded;
    private String actionName;

    public ActionResult(
            String actionName,
            String errorMessage,
            boolean succeeded) {
        this.actionName = actionName;
        this.errorMessage = errorMessage;
        this.succeeded = succeeded;
    }

    public boolean isSucceeded() {
        return this.succeeded;
    }
    public String getErrorMessage() {
        return this.errorMessage;
    }
    public String getActionName() {
        return this.actionName;
    }
}
