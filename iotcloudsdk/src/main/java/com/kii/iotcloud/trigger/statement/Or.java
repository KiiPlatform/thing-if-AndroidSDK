package com.kii.iotcloud.trigger.statement;

import android.os.Parcel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Or implements Statement {
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
