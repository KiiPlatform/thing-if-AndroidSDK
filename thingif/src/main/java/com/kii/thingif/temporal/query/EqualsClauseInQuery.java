package com.kii.thingif.temporal.query;

import android.os.Parcel;

import com.kii.thingif.temporal.base.BaseEquals;

import org.json.JSONObject;

public class EqualsClauseInQuery extends BaseEquals implements QueryClause {
    public EqualsClauseInQuery(String fieldName, Object value) {
        super(fieldName, value);
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
    private EqualsClauseInQuery(Parcel in) {
        super(in);
    }

    public static final Creator<EqualsClauseInQuery> CREATOR = new Creator<EqualsClauseInQuery>() {
        @Override
        public EqualsClauseInQuery createFromParcel(Parcel in) {
            return new EqualsClauseInQuery(in);
        }

        @Override
        public EqualsClauseInQuery[] newArray(int size) {
            return new EqualsClauseInQuery[size];
        }
    };
}
