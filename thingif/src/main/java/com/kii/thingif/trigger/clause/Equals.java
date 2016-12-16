package com.kii.thingif.trigger.clause;

import android.os.Parcel;
import android.provider.AlarmClock;
import android.support.annotation.NonNull;

import com.kii.thingif.Alias;

import org.json.JSONException;
import org.json.JSONObject;

public class Equals<T extends Alias> extends Clause<T> {

    private final String field;
    private final Object value;
    private final T alias;
    public Equals(@NonNull String field, String value, T alias) {
        this.field = field;
        this.value = value;
        this.alias = alias;
    }

    public Equals(String field, long value, T alias) {
        this.field = field;
        this.value = value;
        this.alias = alias;
    }

    public Equals(String field, boolean value, T alias) {
        this.field = field;
        this.value = value;
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
        //TODO: // FIXME: 12/15/16 should adapt to alias
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
        //TODO: // FIXME: 12/15/16 should adapt to alias
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
        //TODO: // FIXME: 12/15/16 should adapt to subclass of Alias, refer http://stackoverflow.com/a/31979348
        this.alias = in.readParcelable(Alias.class.getClassLoader());
        this.field = in.readString();
        Class<?> clazz = (Class<?>)in.readSerializable();
        if (clazz == String.class) {
            this.value = in.readString();
        } else if (clazz == Long.class) {
            this.value = in.readLong();
        } else if (clazz == Boolean.class) {
            this.value = (in.readByte() != 0);
        } else {
            // Won't happens.
            throw new AssertionError("Detected unexpected value.");
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
        //TODO: // FIXME: 12/15/16 shoudl adapt to subclass of Alias
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
