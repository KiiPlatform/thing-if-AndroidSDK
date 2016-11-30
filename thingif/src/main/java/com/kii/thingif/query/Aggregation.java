package com.kii.thingif.query;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.SoundEffectConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represent aggregation
 */
public class Aggregation implements Parcelable{
    private AggregationType type;
    private String responseField;
    private String field;
    private String fieldType;

    /**
     * Initialize Aggregation
     * @param type Type of aggregation
     * @param responseField
     * @param field
     * @param fieldType
     */
    public Aggregation(
            AggregationType type,
            String responseField,
            String field,
            String fieldType
    ) {
        this.type = type;
        this.responseField = responseField;
        this.field = field;
        this.fieldType = fieldType;
    }

    public JSONObject toJSONObject() {
        JSONObject ret = new JSONObject();
        try {
            ret.put("type", this.type);
            ret.put("responseField", this.responseField);
            ret.put("field", this.field);
            ret.put("fieldType", this.fieldType);
            return ret;
        } catch (JSONException e) {
            // Won't happens.
            throw new RuntimeException(e);
        }
    }

    protected Aggregation(Parcel in) {
        this.type = (AggregationType)in.readSerializable();
        this.responseField = in.readString();
        this.field = in.readString();
        this.fieldType = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.type);
        dest.writeString(this.responseField);
        dest.writeString(this.field);
        dest.writeString(this.fieldType);
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
