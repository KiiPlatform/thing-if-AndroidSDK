package com.kii.thingif.thingifapi.utils;

import com.kii.thingif.Target;
import com.kii.thingif.ThingIFAPI;

import org.mockito.internal.util.reflection.Whitebox;

import java.lang.reflect.Method;

public class ThingIFAPIUtils {
    public static void setTarget(ThingIFAPI api, Target target) {
        Whitebox.setInternalState(api, "target", target);
        try {
            Method method = ThingIFAPI.class.getDeclaredMethod("saveInstance", ThingIFAPI.class);
            method.setAccessible(true);
            method.invoke(method, api);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
}
