package com.kii.thingif.trigger.clause;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.kii.thingif.query.clause.Equals;

import org.json.JSONException;
import org.json.JSONObject;

public class AliasEquals extends Equals implements AliasClause {
    private final String alias;
    public AliasEquals(@NonNull String field, String value, String alias) {
        super(field, value);
        this.alias = alias;
    }

    public AliasEquals(String field, long value, String alias) {
        super(field, value);
        this.alias = alias;
    }

    public AliasEquals(String field, boolean value, String alias) {
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
        AliasEquals equals = (AliasEquals) o;
        return alias.equals(equals.alias);
    }
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + alias.hashCode();
        return result;
    }

    // Implementation of Parcelable
    protected AliasEquals(Parcel in) {
        super(in);
        this.alias = in.readString();
    }
    public static final Creator<AliasEquals> CREATOR = new Creator<AliasEquals>() {
        @Override
        public AliasEquals createFromParcel(Parcel in) {
            return new AliasEquals(in);
        }
        @Override
        public AliasEquals[] newArray(int size) {
            return new AliasEquals[size];
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
