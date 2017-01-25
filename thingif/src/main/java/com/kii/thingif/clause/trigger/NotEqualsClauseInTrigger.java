package com.kii.thingif.clause.trigger;

import android.os.Parcel;

import com.kii.thingif.clause.base.BaseNotEquals;

import org.json.JSONObject;

public class NotEqualsClauseInTrigger implements BaseNotEquals, TriggerClause {
    private EqualsClauseInTrigger equals;

    public NotEqualsClauseInTrigger(EqualsClauseInTrigger equals) {
        this.equals = equals;
    }

    @Override
    public EqualsClauseInTrigger getEquals() {
        return this.equals;
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
        dest.writeParcelable(this.equals, flags);
    }

    private NotEqualsClauseInTrigger(Parcel in) {
        this.equals = in.readParcelable(EqualsClauseInTrigger.class.getClassLoader());
    }

    public static final Creator<NotEqualsClauseInTrigger> CREATOR = new Creator<NotEqualsClauseInTrigger>() {
        @Override
        public NotEqualsClauseInTrigger createFromParcel(Parcel source) {
            return new NotEqualsClauseInTrigger(source);
        }

        @Override
        public NotEqualsClauseInTrigger[] newArray(int size) {
            return new NotEqualsClauseInTrigger[size];
        }
    };
}
