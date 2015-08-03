package com.kii.iotcloud.trigger;

public class SchedulePredicate extends Predicate {
    private Schedule schedule;

    public SchedulePredicate(Schedule schedule) {
        this.schedule = schedule;
    }

    public Schedule getSchedule() {
        return this.schedule;
    }
}
