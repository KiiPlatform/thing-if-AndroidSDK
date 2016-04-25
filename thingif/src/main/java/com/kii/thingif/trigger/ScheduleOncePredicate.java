package com.kii.thingif.trigger;

import android.os.Parcel;
import android.support.annotation.NonNull;

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
        //TODO: implementations
    }
    public long getScheduleAt() {
        return this.scheduleAt;
    }
    public EventSource getEventSource() {
        return EventSource.SCHEDULE_ONCE;
    }
    
    // Implementation of Parcelable
    protected ScheduleOncePredicate(Parcel in) {
        this.scheduleAt = in.readParcelable(Schedule.class.getClassLoader());
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
        //TODO: implementations
    }
}
