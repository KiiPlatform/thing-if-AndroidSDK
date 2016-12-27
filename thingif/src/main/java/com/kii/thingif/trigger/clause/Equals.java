package com.kii.thingif.trigger.clause;

import android.os.Parcel;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class Equals extends com.kii.thingif.internal.clause.Equals implements Clause {
    private final String alias;
    public Equals(@NonNull String field, String value, String alias) {
        super(field, value);
        this.alias = alias;
    }

    public Equals(String field, long value, String alias) {
        super(field, value);
        this.alias = alias;
    }

    public Equals(String field, boolean value, String alias) {
        super(field, value);
        this.alias = alias;
    }
    public String getField() {
        return this.field;
    }
    public Object getValue() {
        return this.value;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject ret = super.toJSONObject();
        try {
            ret.put("alias", this.alias);
        }catch (JSONException e) {
            // Won't happens.
            throw new RuntimeException(e);
        }
        return  ret;
    }

    @Override
    public boolean equals(Object o) {
        if(!super.equals(o)){
            return false;
        }
        Equals equals = (Equals) o;
        return alias.equals(equals.alias);
    }
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + alias.hashCode();
        return result;
    }

    // Implementation of Parcelable
    protected Equals(Parcel in) {
        super(in);
        this.alias = in.readString();
    }
    public static final Creator<Equals> CREATOR = new Creator<Equals>() {
        @Override
        public Equals createFromParcel(Parcel in) {
            return new Equals(in);
        }
        @Override
        public Equals[] newArray(int size) {
            return new Equals[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.alias);
    }
}
