package com.kii.iotcloud.command;

import java.io.Serializable;

public abstract class ActionResult implements Serializable {
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
