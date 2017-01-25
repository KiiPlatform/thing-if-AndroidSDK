package com.kii.thingif.clause.trigger;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.kii.thingif.clause.base.BaseEquals;

import org.json.JSONObject;

public class EqualsClauseInTrigger implements BaseEquals, TriggerClause{
    private @NonNull String field;
    private @NonNull Object value;
    private @NonNull String alias;

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

    @Override
    public JSONObject toJSONObject() {
        //TODO: implement me
        return null;
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
        this.value = in.readValue(null);
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
}
