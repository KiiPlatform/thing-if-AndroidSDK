package com.kii.thingif.query;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kii.thingif.TargetState;

import java.util.Arrays;
import java.util.List;

public class AggregatedResult<T extends Number, S extends TargetState> implements Parcelable {
    private @NonNull TimeRange timeRange;
    private @NonNull T value;
    private @Nullable List<HistoryState<S>> aggregatedObjects;

    private volatile int hashCode; // cached hashcode for performance

    public AggregatedResult(
        @NonNull TimeRange timeRange,
        @NonNull T value,
        @Nullable List<HistoryState<S>> aggregatedObjects) {
        this.timeRange = timeRange;
        this.value = value;
        this.aggregatedObjects = aggregatedObjects;
    }

    @NonNull
    public TimeRange getTimeRange() {
        return this.timeRange;
    }

    @NonNull
    public T getValue() {
        return this.value;
    }

    @Nullable
    public List<HistoryState<S>> getAggregatedObjects() {
        return this.aggregatedObjects;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof AggregatedResult)) {
            return false;
        }
        AggregatedResult other = (AggregatedResult) o;
        if (!other.value.getClass().equals(this.value.getClass())) {
            return false;
        }
        if (this.aggregatedObjects != null) {
            if (other.aggregatedObjects == null || !Arrays.equals(
                    this.aggregatedObjects.toArray(), other.aggregatedObjects.toArray())) {
                return false;
            }
        } else {
            if (other.aggregatedObjects != null) {
                return false;
            }
        }
        return this.timeRange.equals(other.timeRange) && this.value.equals(other.value);
    }

    @Override
    public int hashCode() {
        int result = this.hashCode;
        if (result == 0) {
            result = 17;
            result = 31 * result + this.timeRange.hashCode();
            result = 31 * result + this.value.hashCode();
            result = 31 * result + ((this.aggregatedObjects == null)?
                    0: this.aggregatedObjects.hashCode());
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
        dest.writeSerializable(this.value);
        dest.writeList(this.aggregatedObjects);
    }

    public AggregatedResult(Parcel in) {
        this.timeRange = in.readParcelable(TimeRange.class.getClassLoader());
        this.value = (T)in.readSerializable();
        this.aggregatedObjects = in.readArrayList(HistoryState.class.getClassLoader());
    }

    public static final Creator<AggregatedResult> CREATOR = new Creator<AggregatedResult>() {
        @Override
        public AggregatedResult createFromParcel(Parcel source) {
            return new AggregatedResult(source);
        }

        @Override
        public AggregatedResult[] newArray(int size) {
            return new AggregatedResult[size];
        }
    };
}
