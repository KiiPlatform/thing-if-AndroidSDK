package com.kii.thingiftrait.trigger;

import android.os.Parcel;

/** Class represents ScheduleOncePredicate
 *
 */
public class ScheduleOncePredicate extends Predicate {
    private long scheduleAt;

    /** Initialize ScheduleOncePredicate with scheduled date.
     * @param scheduleAt Represent the one time schedule execution date. Must be future date.
     * @throws IllegalArgumentException when scheduleAt is not in the future.
     */
    public ScheduleOncePredicate(long scheduleAt) {
        this.scheduleAt = scheduleAt;
    }
    public long getScheduleAt() {
        return this.scheduleAt;
    }
    public EventSource getEventSource() {
        return EventSource.SCHEDULE_ONCE;
    }
    
    // Implementation of Parcelable
    protected ScheduleOncePredicate(Parcel in) {
        this.scheduleAt = in.readLong();
    }
    public static final Creator<ScheduleOncePredicate> CREATOR = new Creator<ScheduleOncePredicate>() {
        @Override
        public ScheduleOncePredicate createFromParcel(Parcel in) {
            return new ScheduleOncePredicate(in);
        }

        @Override
        public ScheduleOncePredicate[] newArray(int size) {
            return new ScheduleOncePredicate[size];
        }
    };
    public int describeContents() {
        return 0;
    }
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.scheduleAt);
    }
}
