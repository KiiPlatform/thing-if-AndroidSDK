package com.kii.thing_if.clause.trigger;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.kii.thing_if.clause.base.BaseEquals;

public class EqualsClauseInTrigger implements BaseEquals, TriggerClause{
    private @NonNull String field;
    private @NonNull Object value;
    private @NonNull String alias;

    private transient volatile int hashCode; // cached hashcode for performance

    public EqualsClauseInTrigger(
            @NonNull String alias,
            @NonNull String field,
            @NonNull String value) {
        this.alias = alias;
        this.field = field;
        this.value = value;
    }

    public EqualsClauseInTrigger(
            @NonNull String alias,
            @NonNull String field,
            long value) {
        this.alias = alias;
        this.field = field;
        this.value = value;
    }

    public EqualsClauseInTrigger(
            @NonNull String alias,
            @NonNull String field,
            boolean value) {
        this.alias = alias;
        this.field = field;
        this.value = value;
    }

    @NonNull
    public String getField() {
        return this.field;
    }

    @NonNull
    public Object getValue() {
        return this.value;
    }

    @NonNull
    public String getAlias() {
        return alias;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.alias);
        dest.writeString(this.field);
        dest.writeValue(this.value);
    }

    private EqualsClauseInTrigger(Parcel in) {
        this.alias = in.readString();
        this.field = in.readString();
        this.value = in.readValue(Object.class.getClassLoader());
    }

    public static final Creator<EqualsClauseInTrigger> CREATOR = new Creator<EqualsClauseInTrigger>() {
        @Override
        public EqualsClauseInTrigger createFromParcel(Parcel source) {
            return new EqualsClauseInTrigger(source);
        }

        @Override
        public EqualsClauseInTrigger[] newArray(int size) {
            return new EqualsClauseInTrigger[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof EqualsClauseInTrigger)) return false;
        EqualsClauseInTrigger equals = (EqualsClauseInTrigger) o;
        return alias.equals(equals.alias) &&
                field.equals(equals.field) &&
                value.equals(equals.value);
    }

    @Override
    public int hashCode() {
        int result = this.hashCode;
        if (result == 0) {
            result = 17;
            result = 31 * result + this.alias.hashCode();
            result = 31 * result + this.field.hashCode();
            result = 31 * result + this.value.hashCode();
            this.hashCode = result;
        }
        return result;
    }
}
