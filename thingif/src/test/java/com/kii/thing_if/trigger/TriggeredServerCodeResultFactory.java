package com.kii.thing_if.trigger;

import com.kii.thing_if.ServerError;

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
