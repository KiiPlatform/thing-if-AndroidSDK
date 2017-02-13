package com.kii.thingif.query;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Date;

public class TimeRange implements Parcelable{

    private @NonNull Date from;
    private @NonNull Date to;

    private volatile int hashCode; // cached hashcode for performance

    /**
     * Initialize time range.
     * @param from date when the time range starts. It is inclusive.
     * @param to date when the time range ends. It is inclusive.
     */
    public TimeRange(
            @NonNull Date from,
            @NonNull Date to) {
        this.from = from;
        this.to = to;
    }

    @NonNull
    public Date getFrom() { return this.from; }

    @NonNull
    public Date getTo() { return this.to; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.from.getTime());
        dest.writeLong(this.to.getTime());
    }

    public TimeRange(Parcel in) {
        this.from = new Date(in.readLong());
        this.to = new Date(in.readLong());
    }

    public static final Creator<TimeRange> CREATOR = new Creator<TimeRange>() {
        @Override
        public TimeRange createFromParcel(Parcel source) {
            return new TimeRange(source);
        }

        @Override
        public TimeRange[] newArray(int size) {
            return new TimeRange[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof TimeRange)) {
            return false;
        }
        TimeRange other = (TimeRange)o;
        return this.from.equals(other.from) && this.to.equals(other.to);
    }

    @Override
    public int hashCode() {
        int result = this.hashCode;
        if (result == 0) {
            result = 17;
            result = 31 * result + this.from.hashCode();
            result = 31 * result + this.to.hashCode();
            this.hashCode = result;
        }
        return result;
    }
}
