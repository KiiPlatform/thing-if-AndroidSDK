package com.kii.thingiftrait.trigger;

import com.kii.thingiftrait.ServerError;

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
