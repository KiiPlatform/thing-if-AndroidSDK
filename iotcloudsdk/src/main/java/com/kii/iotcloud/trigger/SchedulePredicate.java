package com.kii.iotcloud.trigger;

import android.os.Parcel;

public class SchedulePredicate implements Predicate {
    private Schedule schedule;

    public SchedulePredicate(Schedule schedule) {
        this.schedule = schedule;
    }

    public Schedule getSchedule() {
        return this.schedule;
    }






    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
