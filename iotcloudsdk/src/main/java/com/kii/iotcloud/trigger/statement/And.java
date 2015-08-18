package com.kii.iotcloud.trigger.statement;

import android.os.Parcel;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class And extends Statement {
    private final Statement[] statements;
    public And(@NonNull Statement... statements) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        And and = (And) o;
        return Arrays.equals(statements, and.statements);
    }
    @Override
    public int hashCode() {
        return Arrays.hashCode(statements);
    }

    // Implementation of Parcelable
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
