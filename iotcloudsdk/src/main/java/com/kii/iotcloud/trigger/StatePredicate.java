package com.kii.iotcloud.trigger;

import android.os.Parcel;

public class StatePredicate implements Predicate {

    private final Condition condition;
    private final TriggersWhen triggersWhen;

    public StatePredicate(Condition condition, TriggersWhen triggersWhen) {
        this.condition = condition;
        this.triggersWhen = triggersWhen;
    }


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
