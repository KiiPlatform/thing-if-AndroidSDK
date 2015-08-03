package com.kii.iotcloud.command;

public abstract class ActionResult {
    public String errorMessage;
    public boolean succeeded;
    public boolean succeeded() {
        return this.succeeded;
    }
    public String getErrorMessage() {
        return this.errorMessage;
    }
}
