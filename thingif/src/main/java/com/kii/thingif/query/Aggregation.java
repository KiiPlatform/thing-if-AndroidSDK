package com.kii.thingif.query;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represent aggregation
 */
public class Aggregation<T> implements Parcelable{
    private @NonNull final AggregationType type;
    private @NonNull final String field;
    private @NonNull final Class<T> fieldType;

    /**
     * Initialize Aggregation
     * @param type Type of aggregation
     * @param field
     */
    public Aggregation(
            @NonNull AggregationType type,
            @NonNull String field,
            @NonNull Class<T> fieldType
    ) {
        this.type = type;
        this.field = field;
        this.fieldType = fieldType;
    }

    public JSONObject toJSONObject() {
        JSONObject ret = new JSONObject();
        try {
            ret.put("type", this.type);
            ret.put("responseField", type.toString().toLowerCase());
            ret.put("field", this.field);
            //TODO: // FIXME: 12/21/16 get string name of field type
            ret.put("fieldType", this.fieldType);
            return ret;
        } catch (JSONException e) {
            // Won't happens.
            throw new RuntimeException(e);
        }
    }

    public AggregationType getType() {
        return type;
    }

    public String getField() {
        return field;
    }

    protected Aggregation(Parcel in) {
        this.type = (AggregationType)in.readSerializable();
        this.field = in.readString();
        //TODO: // FIXME: 12/21/16 read fieldType
        this.fieldType = null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.type);
        dest.writeString(this.field);
        dest.writeSerializable(this.fieldType);
    }

    public static final Creator<Aggregation> CREATOR = new Creator<Aggregation>() {
        @Override
        public Aggregation createFromParcel(Parcel source) {
            return new Aggregation(source);
        }

        @Override
        public Aggregation[] newArray(int size) {
            return new Aggregation[size];
        }
    };
}