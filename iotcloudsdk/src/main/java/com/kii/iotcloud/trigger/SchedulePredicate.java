package com.kii.iotcloud.trigger;

import android.os.Parcel;

public class SchedulePredicate implements Predicate {
    private final Schedule schedule;

    public SchedulePredicate(Schedule schedule) {
        this.schedule = schedule;
    }
    public Schedule getSchedule() {
        return this.schedule;
    }

    protected SchedulePredicate(Parcel in) {
        this.schedule = in.readParcelable(Schedule.class.getClassLoader());
    }
    public static final Creator<SchedulePredicate> CREATOR = new Creator<SchedulePredicate>() {
        @Override
        public SchedulePredicate createFromParcel(Parcel in) {
            return new SchedulePredicate(in);
        }

        @Override
        public SchedulePredicate[] newArray(int size) {
            return new SchedulePredicate[size];
        }
    };
    public int describeContents() {
        return 0;
    }
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.schedule, flags);
    }
}
