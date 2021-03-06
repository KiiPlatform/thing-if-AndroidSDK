package com.kii.thing_if.trigger;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.kii.thing_if.clause.trigger.TriggerClause;

public class Condition implements Parcelable {
    private TriggerClause clause;
    public Condition(@NonNull TriggerClause clause) {
        this.clause = clause;
    }
    public TriggerClause getClause() {
        return this.clause;
    }


    // Implementation of Parcelable
    protected Condition(Parcel in) {
        this.clause = in.readParcelable(TriggerClause.class.getClassLoader());
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
