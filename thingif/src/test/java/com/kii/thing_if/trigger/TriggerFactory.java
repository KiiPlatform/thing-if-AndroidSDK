package com.kii.thing_if.trigger;

import com.kii.thing_if.TypedID;
import com.kii.thing_if.command.Command;

import org.json.JSONObject;

public class TriggerFactory {

    public static Trigger createTrigger(
            String triggerID,
            TypedID targetID,
            Predicate predicate,
            Command command,
            ServerCode serverCode,
            boolean disabled,
            String disabledReason,
            String title,
            String description,
            JSONObject metadata) {
        return new Trigger(
                triggerID,
                targetID,
                predicate,
                command,
                serverCode,
                disabled,
                disabledReason,
                title,
                description,
                metadata);
    }
}
