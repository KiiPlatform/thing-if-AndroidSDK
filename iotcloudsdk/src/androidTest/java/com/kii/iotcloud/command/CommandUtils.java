package com.kii.iotcloud.command;

public class CommandUtils {
    public static void setCommandState(Command command, CommandState state) {
        command.setCommandState(state);
    }
    public static void setFiredByTriggerID(Command command, String firedByTriggerID) {
        command.setFiredByTriggerID(firedByTriggerID);
    }
    public static void setCreated(Command command, Long created) {
        command.setCreated(created);
    }
    public static void setModified(Command command, Long modified) {
        command.setModified(modified);
    }
}
