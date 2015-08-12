package com.kii.iotcloud.trigger;

import android.os.Parcel;

public class StatePredicate implements Predicate {

    private Condition condition;
    private TriggersWhen triggersWhen;

    public StatePredicate(Condition condition, TriggersWhen triggersWhen) {
        this.condition = condition;
        this.triggersWhen = triggersWhen;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
