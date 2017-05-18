package com.kii.thing_if.trigger;

import android.os.Parcel;
import android.os.Parcelable;

import com.kii.thing_if.ServerError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TriggeredServerCodeResult implements Parcelable {

    private static final byte TYPE_NULL = 0;
    private static final byte TYPE_JSON_OBJECT = 1;
    private static final byte TYPE_JSON_ARRAY = 2;
    private static final byte TYPE_STRING = 3;
    private static final byte TYPE_INTEGER = 4;
    private static final byte TYPE_LONG = 5;
    private static final byte TYPE_DOUBLE = 6;
    private static final byte TYPE_BOOLEAN = 7;


    private final boolean succeeded;
    /**
     * returnedValue is JsonObject, JsonArray, String, Number, Boolean or null
     */
    private Object returnedValue;
    private final long executedAt;
    private String endpoint;
    private ServerError error;

    TriggeredServerCodeResult(boolean succeeded, Object returnedValue, long executedAt, String endpoint, ServerError error) {
        this.succeeded = succeeded;
        this.returnedValue = returnedValue;
        this.executedAt = executedAt;
        this.endpoint = endpoint;
        this.error = error;
    }
    public boolean isSucceeded() {
        return this.succeeded;
    }
    public boolean hasReturnedValue() {
        return this.returnedValue != null;
    }
    public Object getReturnedValue() {
        return this.returnedValue;
    }
    public JSONObject getReturnedValueAsJsonObject() {
        if (this.returnedValue == null) {
            return null;
        }
        try {
            return (JSONObject)this.returnedValue;
        } catch (Exception e) {
            throw new ClassCastException(this.returnedValue + " cannot cast to org.json.JSONObject");
        }
    }
    public JSONArray getReturnedValueAsJsonArray() {
        if (this.returnedValue == null) {
            return null;
        }
        try {
            return (JSONArray)this.returnedValue;
        } catch (Exception e) {
            throw new ClassCastException(this.returnedValue + " cannot cast to org.json.JSONArray");
        }
    }
    public String getReturnedValueAsString() {
        if (this.returnedValue == null) {
            return null;
        }
        try {
            if (this.returnedValue instanceof String) {
                return (String)this.returnedValue;
            }
            return this.returnedValue.toString();
        } catch (Exception e) {
            throw new ClassCastException(this.returnedValue + " cannot cast to Integer");
        }
    }
    public Integer getReturnedValueAsInteger() {
        if (this.returnedValue == null) {
            return null;
        }
        try {
            return ((Number)this.returnedValue).intValue();
        } catch (Exception e) {
            throw new ClassCastException(this.returnedValue + " cannot cast to Integer");
        }
    }
    public Long getReturnedValueAsLong() {
        if (this.returnedValue == null) {
            return null;
        }
        try {
            return ((Number)this.returnedValue).longValue();
        } catch (Exception e) {
            throw new ClassCastException(this.returnedValue + " cannot cast to Long");
        }
    }
    public Number getReturnedValueAsNumber() {
        if (this.returnedValue == null) {
            return null;
        }
        try {
            return (Number)this.returnedValue;
        } catch (Exception e) {
            throw new ClassCastException(this.returnedValue + " cannot cast to Number");
        }
    }
    public Boolean getReturnedValueAsBoolean() {
        if (this.returnedValue == null) {
            return null;
        }
        try {
            return (Boolean)this.returnedValue;
        } catch (Exception e) {
            throw new ClassCastException(this.returnedValue + " cannot cast to Boolean");
        }
    }
    public Double getReturnedValueAsDouble() {
        if (this.returnedValue == null) {
            return null;
        }
        try {
            return ((Number)this.returnedValue).doubleValue();
        } catch (Exception e) {
            throw new ClassCastException(this.returnedValue + " cannot cast to Double");
        }
    }
    public long getExecutedAt() {
        return this.executedAt;
    }
    public String getEndpoint() {
        return this.endpoint;
    }
    public ServerError getError() {
        return this.error;
    }

    // Implementation of Parcelable
    protected TriggeredServerCodeResult(Parcel in) {
        this.succeeded = (in.readByte() != 0);
        byte returnedValueType = in.readByte();
        switch (returnedValueType) {
            case TYPE_NULL:
                this.returnedValue = null;
                break;
            case TYPE_JSON_OBJECT:
                try {
                    this.returnedValue = new JSONObject(in.readString());
                } catch (JSONException ignore) {
                }
                break;
            case TYPE_JSON_ARRAY:
                try {
                    this.returnedValue = new JSONArray(in.readString());
                } catch (JSONException ignore) {
                }
                break;
            case TYPE_STRING:
                this.returnedValue = in.readString();
                break;
            case TYPE_INTEGER:
                this.returnedValue = in.readInt();
                break;
            case TYPE_LONG:
                this.returnedValue = in.readLong();
                break;
            case TYPE_DOUBLE:
                this.returnedValue = in.readDouble();
                break;
            case TYPE_BOOLEAN:
                this.returnedValue = (in.readByte() != 0);
                break;
        }
        this.executedAt = in.readLong();
        this.endpoint = in.readString();
        this.error = in.readParcelable(ServerError.class.getClassLoader());
    }
    public static final Creator<TriggeredServerCodeResult> CREATOR = new Creator<TriggeredServerCodeResult>() {
        @Override
        public TriggeredServerCodeResult createFromParcel(Parcel in) {
            return new TriggeredServerCodeResult(in);
        }

        @Override
        public TriggeredServerCodeResult[] newArray(int size) {
            return new TriggeredServerCodeResult[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (this.succeeded ? 1 : 0));
        if (this.returnedValue == null) {
            dest.writeByte(TYPE_NULL);
        } else if (this.returnedValue instanceof JSONObject) {
            dest.writeByte(TYPE_JSON_OBJECT);
            dest.writeString(((JSONObject)this.returnedValue).toString());
        } else if (this.returnedValue instanceof JSONArray) {
            dest.writeByte(TYPE_JSON_ARRAY);
            dest.writeString(((JSONArray)this.returnedValue).toString());
        } else if (this.returnedValue instanceof String) {
            dest.writeByte(TYPE_STRING);
            dest.writeString((String)this.returnedValue);
        } else if (this.returnedValue instanceof Integer) {
            dest.writeByte(TYPE_INTEGER);
            dest.writeInt((Integer)this.returnedValue);
        } else if (this.returnedValue instanceof Long) {
            dest.writeByte(TYPE_LONG);
            dest.writeLong((Long) this.returnedValue);
        } else if (this.returnedValue instanceof Double) {
            dest.writeByte(TYPE_DOUBLE);
            dest.writeDouble((Double) this.returnedValue);
        } else if (this.returnedValue instanceof Boolean) {
            dest.writeByte(TYPE_BOOLEAN);
            dest.writeByte((byte) ((Boolean)this.returnedValue ? 1 : 0));
        }
        dest.writeLong(this.executedAt);
        dest.writeString(this.endpoint);
        if (this.error != null) {
            dest.writeParcelable(this.error, flags);
        } else {
            dest.writeParcelable(null, flags);
        }
    }
}
