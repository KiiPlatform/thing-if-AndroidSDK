package com.kii.thingif.trigger;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.kii.thingif.trigger.clause.AliasClause;

public class Condition implements Parcelable {
    private AliasClause aliasClause;
    public Condition(@NonNull AliasClause aliasClause) {
        this.aliasClause = aliasClause;
    }
    public AliasClause getAliasClause() {
        return this.aliasClause;
    }


    // Implementation of Parcelable
    protected Condition(Parcel in) {
        this.aliasClause = in.readParcelable(AliasClause.class.getClassLoader());
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
        dest.writeParcelable(this.aliasClause, flags);
    }
}
