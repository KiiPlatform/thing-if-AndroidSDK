package com.kii.thingif.trigger;

import com.kii.thingif.ServerError;

public class TriggeredServerCodeResultFactory {
    public static TriggeredServerCodeResult create(
            boolean succeeded,
            Object returnedValue,
            long executedAt,
            String endpoint,
            ServerError error) {
        return new TriggeredServerCodeResult(
                succeeded,
                returnedValue,
                executedAt,
                endpoint,
                error);
    }
}
