package com.kii.thingif.trigger;

import com.kii.thingif.TypedID;
import com.kii.thingif.command.Command;

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
