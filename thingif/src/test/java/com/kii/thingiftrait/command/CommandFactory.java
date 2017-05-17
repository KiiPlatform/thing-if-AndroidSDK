package com.kii.thingiftrait.command;

import com.kii.thingiftrait.TypedID;

import org.json.JSONObject;

import java.util.List;

public class CommandFactory {
    public static Command newTriggeredCommand(
            TypedID issuerID,
            List<AliasAction> aliasActions,
            TypedID targetID,
            String title,
            String description,
            JSONObject metadata) {
        return new Command(
                issuerID,
                aliasActions,
                null,
                targetID,
                null,
                null,
                null,
                null,
                null,
                title,
                description,
                metadata);
    }

    public static Command newCommand(
            TypedID issuerID,
            List<AliasAction> aliasActions,
            String commandID,
            TypedID targetID,
            List<AliasActionResult> aliasActionResults,
            CommandState commandState,
            String firedByTriggerID,
            Long created,
            Long modified,
            String title,
            String description,
            JSONObject metadata) {
        return new Command(
                issuerID,
                aliasActions,
                commandID,
                targetID,
                aliasActionResults,
                commandState,
                firedByTriggerID,
                created,
                modified,
                title,
                description,
                metadata);
    }

}
