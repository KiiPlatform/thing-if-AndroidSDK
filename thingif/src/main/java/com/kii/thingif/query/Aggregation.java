package com.kii.thingif.query;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represent aggregation
 */
public class Aggregation implements Parcelable{
    private @NonNull final FunctionType function;
    private @NonNull final String field;
    private @NonNull final FieldType fieldType;

    public static enum FunctionType{
        MAX,
        MIN,
        MEAN,
        SUM
    }

    public static enum FieldType{
        INTEGER,
        DECIMAL
    }

    /**
     * Initialize Aggregation
     * @param function Agrregation function.
     * @param field
     */
    private Aggregation(
            @NonNull FunctionType function,
            @NonNull String field,
            @NonNull FieldType fieldType
    ) {
        this.function = function;
        this.field = field;
        this.fieldType = fieldType;
    }


    public  static Aggregation newMaxAggregation(
            @NonNull String field,
            @NonNull FieldType fieldType) throws IllegalArgumentException{
        return new Aggregation(FunctionType.MAX, field, fieldType);
    }

    public  static Aggregation newMinAggregation(
            @NonNull String field,
            @NonNull FieldType fieldType) throws IllegalArgumentException{
        return new Aggregation(FunctionType.MIN, field, fieldType);
    }

    public  static Aggregation newMeanAggregation(
            @NonNull String field,
            @NonNull FieldType fieldType) throws IllegalArgumentException{
        return new Aggregation(FunctionType.MEAN, field, fieldType);
    }

    public  static Aggregation newSumAggregation(
            @NonNull String field,
            @NonNull FieldType fieldType) throws IllegalArgumentException{
        if(fieldType != FieldType.DECIMAL && fieldType != FieldType.INTEGER){
            throw new IllegalArgumentException("fieldType of SUM aggregation should only be INTEGER or DECIMAL");
        }
        return new Aggregation(FunctionType.SUM, field, fieldType);
    }


    public JSONObject toJSONObject() {
        JSONObject ret = new JSONObject();
        try {
            ret.put("type", this.function);
            ret.put("responseField", function.toString().toLowerCase());
            ret.put("field", this.field);
            //TODO: // FIXME: 12/21/16 get string name of field type
            ret.put("fieldType", this.fieldType);
            return ret;
        } catch (JSONException e) {
            // Won't happens.
            throw new RuntimeException(e);
        }
    }

    public FunctionType getFunction() {
        return function;
    }

    public String getField() {
        return field;
    }

    protected Aggregation(Parcel in) {
        //TODO: // FIXME: 12/22/16 check where read serializable for enum
        this.function = (FunctionType) in.readSerializable();
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
        dest.writeSerializable(this.function);
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