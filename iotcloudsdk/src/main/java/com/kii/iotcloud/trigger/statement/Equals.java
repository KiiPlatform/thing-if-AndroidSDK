package com.kii.iotcloud.trigger.statement;

import android.os.Parcel;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class Equals extends Statement {

    private String field;
    private Object value;
    public Equals(@NonNull String field, String value) {
        this.field = field;
        this.value = value;
    }

    public Equals(String field, long value) {
        this.field = field;
        this.value = value;
    }

    public Equals(String field, boolean value) {
        this.field = field;
        this.value = value;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject ret = new JSONObject();
        try {
            ret.put("type", "eq");
            ret.put("field", this.field);
            ret.put("value", this.value);
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
        Equals equals = (Equals) o;
        if (!field.equals(equals.field)) return false;
        return value.equals(equals.value);
    }
    @Override
    public int hashCode() {
        int result = field.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    // Implementation of Parcelable
    protected Equals(Parcel in) {
        this.field = in.readString();
        Class<?> clazz = (Class<?>)in.readSerializable();
        if (clazz == String.class) {
            this.value = in.readString();
        } else if (clazz == Long.class) {
            this.value = in.readLong();
        } else if (clazz == Boolean.class) {
            this.value = (in.readByte() != 0);
        }
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
        dest.writeString(this.field);
        dest.writeSerializable(this.value.getClass());
        if (this.value instanceof String) {
            dest.writeString((String)this.value);
        } else if (Long.class.isInstance(this.value)) {
            dest.writeLong((Long)this.value);
        } else if (Boolean.class.isInstance(this.value)) {
            dest.writeByte((byte)((Boolean)this.value ? 1 : 0));
        }
    }
}
