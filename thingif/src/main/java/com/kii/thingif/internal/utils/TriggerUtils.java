package com.kii.thingif.internal.utils;

import com.kii.thingif.TypedID;
import com.kii.thingif.trigger.Trigger;

import java.lang.reflect.Field;

public class TriggerUtils {
    public static void setTargetID(Trigger instance, TypedID targetID) {
        try {
            Field field = Trigger.class.getDeclaredField("targetID");
            field.setAccessible(true);
            field.set(instance, targetID);
        }catch (Exception ex) {
                throw new RuntimeException(ex);
        }
    }
}
