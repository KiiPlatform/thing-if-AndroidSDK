package com.kii.thingiftrait.clause.query;

import android.os.Parcel;

public class AllClause implements QueryClause {

    public AllClause() { }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) { }

    private AllClause(Parcel in) { }

    public static final Creator<AllClause> CREATOR = new Creator<AllClause>() {
        @Override
        public AllClause createFromParcel(Parcel source) {
            return new AllClause(source);
        }

        @Override
        public AllClause[] newArray(int size) {
            return new AllClause[size];
        }
    };
}
