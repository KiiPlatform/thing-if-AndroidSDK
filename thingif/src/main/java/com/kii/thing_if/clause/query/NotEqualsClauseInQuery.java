package com.kii.thing_if.clause.query;

import android.os.Parcel;

import com.kii.thing_if.clause.base.BaseNotEquals;

public class NotEqualsClauseInQuery implements BaseNotEquals, QueryClause {

    private EqualsClauseInQuery equals;

    private transient volatile int hashCode; // cached hashcode for performance

    public NotEqualsClauseInQuery(EqualsClauseInQuery equals) {
        this.equals = equals;
    }

    @Override
    public EqualsClauseInQuery getEquals() {
        return this.equals;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.equals, flags);
    }

    private NotEqualsClauseInQuery(Parcel in) {
        this.equals = in.readParcelable(EqualsClauseInQuery.class.getClassLoader());
    }

    public static final Creator<NotEqualsClauseInQuery> CREATOR = new Creator<NotEqualsClauseInQuery>() {
        @Override
        public NotEqualsClauseInQuery createFromParcel(Parcel source) {
            return new NotEqualsClauseInQuery(source);
        }

        @Override
        public NotEqualsClauseInQuery[] newArray(int size) {
            return new NotEqualsClauseInQuery[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof NotEqualsClauseInQuery)) return false;
        NotEqualsClauseInQuery notEquals = (NotEqualsClauseInQuery) o;
        return this.equals.equals(notEquals.equals);
    }

    @Override
    public int hashCode() {
        int result = this.hashCode;
        if(result == 0) {
            result = 17;
            result = 31 * result + this.equals.hashCode();
            this.hashCode = result;
        }
        return result;
    }
}
