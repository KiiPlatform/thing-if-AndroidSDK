package com.kii.thingif.trigger;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.kii.thingif.Alias;
import com.kii.thingif.trigger.clause.Clause;

public class Condition<T extends Alias> implements Parcelable {
    private Clause<T> clause;
    public Condition(@NonNull Clause<T> clause) {
        this.clause = clause;
    }
    public Clause<T> getClause() {
        return this.clause;
    }


    // Implementation of Parcelable
    protected Condition(Parcel in) {
        this.clause = in.readParcelable(Clause.class.getClassLoader());
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
        dest.writeParcelable(this.clause, flags);
    }
}
