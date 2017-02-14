package com.kii.thingif.query;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.kii.thingif.TargetState;

import java.util.Date;

public class HistoryState<T extends TargetState> implements Parcelable {
    private @NonNull T state;
    private @NonNull Date createdAt;

    private volatile int hashCode; // cached hashcode for performance

    public HistoryState(
            @NonNull T state,
            @NonNull Date createdAt) {
        this.state = state;
        this.createdAt = createdAt;
    }

    @NonNull
    public T getState() {
        return state;
    }

    @NonNull
    public Date getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof HistoryState)) {
            return false;
        }
        HistoryState other = (HistoryState) o;
        if (!other.state.getClass().equals(this.state.getClass())) {
            return false;
        }
        return this.state.equals(other.state) && this.createdAt.equals(other.createdAt);
    }

    @Override
    public int hashCode() {
        int result = this.hashCode;
        if (result == 0) {
            result = 17;
            result = 31 * result + this.state.hashCode();
            result = 31 * result + this.createdAt.hashCode();
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
        dest.writeSerializable(this.state);
        dest.writeLong(this.createdAt.getTime());
    }

    public HistoryState(Parcel in) {
        this.state = (T)in.readSerializable();
        this.createdAt = new Date(in.readLong());
    }

    public static final Creator<HistoryState> CREATOR = new Creator<HistoryState>() {
        @Override
        public HistoryState createFromParcel(Parcel source) {
            return new HistoryState(source);
        }

        @Override
        public HistoryState[] newArray(int size) {
            return new HistoryState[size];
        }
    };
}
