package com.kii.iotcloud.trigger.statement;

import android.os.Parcel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class Or extends Statement {
    private final Statement[] statements;
    public Or(Statement... statements) {
        this.statements = statements;
    }
    @Override
    public JSONObject toJSONObject() {
        JSONObject ret = new JSONObject();
        JSONArray clauses = new JSONArray();
        try {
            ret.put("type", "or");
            for (Statement statement : this.statements) {
                clauses.put(statement.toJSONObject());
            }
            ret.put("clauses", clauses);
            return ret;
        } catch (JSONException e) {
            // Won't happens.
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Or or = (Or) o;
        return Arrays.equals(statements, or.statements);

    }
    @Override
    public int hashCode() {
        return Arrays.hashCode(statements);
    }

    // Implementation of Parcelable
    protected Or(Parcel in) {
        this.statements = in.createTypedArray(Or.CREATOR);
    }
    public static final Creator<Or> CREATOR = new Creator<Or>() {
        @Override
        public Or createFromParcel(Parcel in) {
            return new Or(in);
        }

        @Override
        public Or[] newArray(int size) {
            return new Or[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(this.statements, flags);
    }
}
