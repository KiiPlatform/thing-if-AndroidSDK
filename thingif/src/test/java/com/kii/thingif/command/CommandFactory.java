package com.kii.thingif.command;

import com.kii.thingif.TypedID;

import org.json.JSONObject;

import java.util.List;

public class CommandFactory {
    public static Command newCommand(
            TypedID issuerID,
            List<AliasAction<? extends Action>> aliasActions,
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
