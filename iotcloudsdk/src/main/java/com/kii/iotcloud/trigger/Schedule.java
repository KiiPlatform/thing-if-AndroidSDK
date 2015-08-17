package com.kii.iotcloud.trigger;

import android.os.Parcel;
import android.os.Parcelable;

public class Schedule implements Parcelable {

    private String cronExpression;

    public Schedule(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    protected Schedule(Parcel in) {
        this.cronExpression = in.readString();
    }
    public static final Creator<Schedule> CREATOR = new Creator<Schedule>() {
        @Override
        public Schedule createFromParcel(Parcel in) {
            return new Schedule(in);
        }

        @Override
        public Schedule[] newArray(int size) {
            return new Schedule[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cronExpression);
    }
}
