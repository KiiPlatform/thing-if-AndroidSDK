package com.kii.thingif.trigger.clause;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class And extends ContainerClause {
    public And(Clause... clauses) {
        super(clauses);
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
        return Arrays.equals(this.getClauses(), and.getClauses());

    }
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.getClauses());
    }


    // Implementation of Parcelable
    protected And(Parcel in) {
        super();
        int length = in.readInt();
        for (int i = 0; i < length; i++) {
            this.clauses.add((Clause)in.readParcelable(getClass().getClassLoader()));
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
        dest.writeInt(this.clauses.size());
        for (Clause clause : this.clauses) {
            dest.writeParcelable(clause, flags);
        }
    }
}
