package com.kii.thingif.command;

import org.mockito.internal.util.reflection.Whitebox;

public class CommandUtils {
    public static void setCommandState(Command command, CommandState state) {
        Whitebox.setInternalState(command, "commandState", state);
    }
    public static void setFiredByTriggerID(Command command, String firedByTriggerID) {
        Whitebox.setInternalState(command, "firedByTriggerID", firedByTriggerID);
    }
    public static void setCreated(Command command, Long created) {
        Whitebox.setInternalState(command, "created", created);
    }
    public static void setModified(Command command, Long modified) {
        Whitebox.setInternalState(command, "modified", modified);
    }
}
