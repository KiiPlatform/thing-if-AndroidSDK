package com.kii.thingif.query;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kii.thingif.TargetState;

import java.util.Arrays;
import java.util.List;

public class GroupedHistoryStates<S extends TargetState> implements Parcelable {
    private @NonNull TimeRange timeRange;
    private @NonNull List<HistoryState<S>> objects;

    private volatile int hashCode; // cached hashcode for performance

    public GroupedHistoryStates(
            @NonNull TimeRange timeRange,
            @NonNull List<HistoryState<S>> objects) {
        this.timeRange = timeRange;
        this.objects = objects;
    }

    @NonNull
    public TimeRange getTimeRange() {
        return this.timeRange;
    }

    @NonNull
    public List<HistoryState<S>> getObjects() {
        return this.objects;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof GroupedHistoryStates)) {
            return false;
        }
        GroupedHistoryStates other = (GroupedHistoryStates) o;
        return this.timeRange.equals(other.timeRange) &&
                Arrays.equals(this.objects.toArray(), other.objects.toArray());
    }

    @Override
    public int hashCode() {
        int result = this.hashCode;
        if (result == 0) {
            result = 17;
            result = 31 * result + this.timeRange.hashCode();
            result = 31 * result + this.objects.hashCode();
            this.hashCode = result;
        }
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.timeRange, flags);
        dest.writeList(this.objects);
    }

    public GroupedHistoryStates(Parcel in) {
        this.timeRange = in.readParcelable(TimeRange.class.getClassLoader());
        this.objects = in.readArrayList(HistoryState.class.getClassLoader());
    }

    public static final Creator<GroupedHistoryStates> CREATOR = new Creator<GroupedHistoryStates>() {
        @Override
        public GroupedHistoryStates createFromParcel(Parcel source) {
            return new GroupedHistoryStates(source);
        }

        @Override
        public GroupedHistoryStates[] newArray(int size) {
            return new GroupedHistoryStates[size];
        }
    };
}
