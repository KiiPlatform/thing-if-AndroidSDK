package com.kii.iotcloud.trigger.statement;

import android.os.Parcel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class And implements Statement {
    private final Statement[] statements;
    public And(Statement... statements) {
        this.statements = statements;
    }
    @Override
    public JSONObject toJSONObject() {
        JSONObject ret = new JSONObject();
        JSONArray clauses = new JSONArray();
        try {
            ret.put("type", "and");
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

    protected And(Parcel in) {
        this.statements = in.createTypedArray(And.CREATOR);
    }
    public static final Creator<And> CREATOR = new Creator<And>() {
        @Override
        public And createFromParcel(Parcel in) {
            return new And(in);
        }

        @Override
        public And[] newArray(int size) {
            return new And[size];
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
