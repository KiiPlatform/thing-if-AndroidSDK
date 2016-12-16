package com.kii.thingif.trigger.clause;

import android.os.Parcel;

import com.kii.thingif.Alias;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class Or<T extends Alias> extends ContainerClause<T> {
    public Or(Clause<T>... clauses) {
        super(clauses);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Or or = (Or) o;
        return Arrays.equals(this.getClauses(), or.getClauses());

    }
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.getClauses());
    }

    // Implementation of Parcelable
    protected Or(Parcel in) {
        int length = in.readInt();
        for (int i = 0; i < length; i++) {
            this.clauses.add((Clause)in.readParcelable(getClass().getClassLoader()));
        }
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
        dest.writeInt(this.clauses.size());
        for (Clause clause : this.clauses) {
            dest.writeParcelable(clause, flags);
        }
    }
}
