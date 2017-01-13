package com.kii.thingif.temporal.base;

import android.os.Parcel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BaseAnd<T extends BaseClause> implements BaseClause{
    protected  List<T> clauses;

    public List<T> getClauses(){
        return this.clauses;
    }
    public void addClause(T clause){
        this.clauses.add(clause);
    }

    public BaseAnd(T... clauses) {
        this.clauses = new ArrayList<>();
        for(T clause: clauses) {
            this.clauses.add(clause);
        }
    }
    @Override
    public JSONObject toJSONObject() {
        JSONObject ret = new JSONObject();
        JSONArray clauses = new JSONArray();
        try {
            ret.put("type", "and");
            for (BaseClause clause : this.clauses) {
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
        BaseAnd and = (BaseAnd) o;
        return Arrays.equals(this.getClauses().toArray(), and.getClauses().toArray());

    }
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.getClauses().toArray());
    }

    // Implementation of Parcelable
    protected BaseAnd(Parcel in) {
        super();
        int length = in.readInt();
        for (int i = 0; i < length; i++) {
            this.clauses.add((T) in.readParcelable(getClass().getClassLoader()));
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.clauses.size());
        for (BaseClause clause : this.clauses) {
            dest.writeParcelable(clause, flags);
        }
    }


//    public static final Parcelable.Creator<BaseAnd> CREATOR = new Parcelable.Creator<BaseAnd>() {
//        @Override
//        public BaseAnd createFromParcel(Parcel in) {
//            return new BaseAnd(in);
//        }
//
//        @Override
//        public BaseAnd[] newArray(int size) {
//            return new BaseAnd[size];
//        }
//    };
}