package com.kii.thing_if.trigger;

import android.os.Parcel;
import android.support.annotation.NonNull;

public class StatePredicate extends Predicate {

    private Condition condition;
    private TriggersWhen triggersWhen;

    public StatePredicate(@NonNull Condition condition, @NonNull TriggersWhen triggersWhen) {
        if (condition == null) {
            throw new IllegalArgumentException("condition is null");
        }
        if (triggersWhen == null) {
            throw new IllegalArgumentException("triggersWhen is null");
        }
        this.condition = condition;
        this.triggersWhen = triggersWhen;
    }
    public EventSource getEventSource() {
        return EventSource.STATES;
    }
    public Condition getCondition() {
        return this.condition;
    }
    public TriggersWhen getTriggersWhen() {
        return this.triggersWhen;
    }

    // Implementation of Parcelable
    protected StatePredicate(Parcel in) {
        this.condition = in.readParcelable(Condition.class.getClassLoader());
        this.triggersWhen = (TriggersWhen)in.readSerializable();
    }
    public static final Creator<StatePredicate> CREATOR = new Creator<StatePredicate>() {
        @Override
        public StatePredicate createFromParcel(Parcel in) {
            return new StatePredicate(in);
        }

        @Override
        public StatePredicate[] newArray(int size) {
            return new StatePredicate[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.condition, flags);
        dest.writeSerializable(this.triggersWhen);
    }
}
