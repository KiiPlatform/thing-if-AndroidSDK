package com.kii.iotcloud.trigger;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.kii.iotcloud.trigger.statement.Statement;

public class Condition implements Parcelable {
    private Statement statement;
    public Condition(@NonNull Statement statement) {
        this.statement = statement;
    }
    public Statement getStatement() {
        return this.statement;
    }


    // Implementation of Parcelable
    protected Condition(Parcel in) {
        this.statement = in.readParcelable(Statement.class.getClassLoader());
    }
    public static final Creator<Condition> CREATOR = new Creator<Condition>() {
        @Override
        public Condition createFromParcel(Parcel in) {
            return new Condition(in);
        }

        @Override
        public Condition[] newArray(int size) {
            return new Condition[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.statement, flags);
    }
}
