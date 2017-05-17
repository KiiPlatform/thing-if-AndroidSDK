package com.kii.thingiftrait.trigger;

import com.kii.thingiftrait.TypedID;
import com.kii.thingiftrait.command.Command;

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
