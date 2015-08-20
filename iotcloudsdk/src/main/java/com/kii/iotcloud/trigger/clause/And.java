package com.kii.iotcloud.trigger.clause;

import android.os.Parcel;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class And extends Clause {
    private final Clause[] clauses;
    public And(@NonNull Clause... clauses) {
        this.clauses = clauses;
    }
    public Clause[] getClauses() {
        return this.clauses;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject ret = new JSONObject();
        JSONArray clauses = new JSONArray();
        try {
            ret.put("type", "and");
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        And and = (And) o;
        return Arrays.equals(clauses, and.clauses);
    }
    @Override
    public int hashCode() {
        return Arrays.hashCode(clauses);
    }

    // Implementation of Parcelable
    protected And(Parcel in) {
        int length = in.readInt();
        this.clauses = new Clause[length];
        for (int i = 0; i < length; i++) {
            this.clauses[i] = in.readParcelable(getClass().getClassLoader());
        }
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
        dest.writeInt(this.clauses.length);
        for (Clause clause : this.clauses) {
            dest.writeParcelable(clause, flags);
        }
    }
}
