package com.kii.thingif.trigger;

import org.mockito.internal.util.reflection.Whitebox;

public class TriggerUtils {
    public static void setTriggerID(Trigger trigger, String triggerID) {
        Whitebox.setInternalState(trigger, "triggerID", triggerID);
    }
    public static void setDisabledReason(Trigger trigger, String disabledReason) {
        Whitebox.setInternalState(trigger, "disabledReason", disabledReason);
    }
}
