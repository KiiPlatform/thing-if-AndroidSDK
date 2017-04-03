package com.kii.thingif.command;

import org.json.JSONObject;

public class ActionResultFactory {
    public static ActionResult newActionResult(
            String actionName,
            boolean succeeded,
            String errorMessage,
            JSONObject data) {
        return new ActionResult(actionName, succeeded, errorMessage, data);
    }
}
