package com.kii.thing_if.exception;

import com.kii.thing_if.command.ActionResult;

public class ActionExecutionException extends ThingIFException {
    private ActionResult result;
    public ActionExecutionException(ActionResult result) {
        super(result.getErrorMessage());
        this.result = result;
    }

    public ActionResult getResult() {
        return result;
    }
}
