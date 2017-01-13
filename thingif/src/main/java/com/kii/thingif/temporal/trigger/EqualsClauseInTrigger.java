package com.kii.thingif.temporal.trigger;

import android.os.Parcel;

import com.kii.thingif.temporal.base.BaseEquals;

import org.json.JSONObject;

public class EqualsClauseInTrigger extends BaseEquals implements TriggerClause{
    private String alias;
    public EqualsClauseInTrigger(String alias, String fieldName, Object value) {
        super(fieldName, value);
        this.alias = alias;
    }
    @Override
    public String getField() {
        return null;
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public JSONObject toJSONObject() {
        return null;
    }


    // Implementation of Parcelable
    private EqualsClauseInTrigger(Parcel in) {
        super(in);
    }

    public static final Creator<EqualsClauseInTrigger> CREATOR = new Creator<EqualsClauseInTrigger>() {
        @Override
        public EqualsClauseInTrigger createFromParcel(Parcel in) {
            return new EqualsClauseInTrigger(in);
        }

        @Override
        public EqualsClauseInTrigger[] newArray(int size) {
            return new EqualsClauseInTrigger[size];
        }
    };

}
