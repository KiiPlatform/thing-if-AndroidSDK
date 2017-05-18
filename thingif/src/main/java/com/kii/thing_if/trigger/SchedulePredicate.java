package com.kii.thing_if.trigger;

import android.os.Parcel;
import android.support.annotation.NonNull;

public class SchedulePredicate extends Predicate {
    private String schedule;

    public SchedulePredicate(@NonNull String schedule) {
        this.schedule = schedule;
    }
    public String getSchedule() {
        return this.schedule;
    }
    public EventSource getEventSource() {
        return EventSource.SCHEDULE;
    }


    // Implementation of Parcelable
    protected SchedulePredicate(Parcel in) {
        this.schedule = in.readString();
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
        dest.writeString(this.schedule);
    }
}
