package com.kii.iotcloud.trigger;

import com.kii.iotcloud.trigger.statement.Statement;

public class Condition {
    private Statement statement;
    public Condition(Statement statement) {
        this.statement = statement;
    }
}
