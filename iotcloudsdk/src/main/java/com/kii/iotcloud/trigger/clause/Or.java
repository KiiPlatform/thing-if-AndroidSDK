package com.kii.iotcloud.trigger.clause;

import android.os.Parcel;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class Or extends Clause {
    private final Clause[] clauses;
    public Or(@NonNull Clause... clauses) {
        this.clauses = clauses;
    }
    @Override
    public JSONObject toJSONObject() {
        JSONObject ret = new JSONObject();
        JSONArray clauses = new JSONArray();
        try {
            ret.put("type", "or");
            for (Clause clause : this.clauses) {
                clauses.put(clause.toJSONObject());
            }
            ret.put("clauses", clauses);
            return ret;
        } catch (JSONException e) {
            // Won't happens.
            throw new RuntimeException(e);
        }
    }
    public Clause[] getClauses() {
        return this.clauses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Or or = (Or) o;
        return Arrays.equals(clauses, or.clauses);

    }
    @Override
    public int hashCode() {
        return Arrays.hashCode(clauses);
    }

    // Implementation of Parcelable
    protected Or(Parcel in) {
        this.clauses = in.createTypedArray(Or.CREATOR);
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
        dest.writeTypedArray(this.clauses, flags);
    }
}
