package com.kii.iotcloud.trigger;

public class StatePredicate extends Predicate {

    private Condition condition;
    private TriggersWhen triggersWhen;

    public StatePredicate(Condition condition, TriggersWhen triggersWhen) {
        this.condition = condition;
        this.triggersWhen = triggersWhen;
    }
}
