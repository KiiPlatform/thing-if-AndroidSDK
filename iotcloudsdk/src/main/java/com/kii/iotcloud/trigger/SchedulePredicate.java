package com.kii.iotcloud.trigger;

import android.os.Parcel;

public class SchedulePredicate extends Predicate {
    private Schedule schedule;

    public SchedulePredicate(@NonNull Schedule schedule) {
        this.schedule = schedule;
        throw new UnsupportedOperationException("SchedulePredicate is not supported");
    }
    public Schedule getSchedule() {
        return this.schedule;
    }
    public EventSource getEventSource() {
        return EventSource.SCHEDULE;
    }


    // Implementation of Parcelable
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
