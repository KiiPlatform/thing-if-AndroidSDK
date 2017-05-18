package com.kii.thing_if.query;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Represent aggregation
 */
public class Aggregation {
    @SerializedName("type")
    private @NonNull final FunctionType function;
    private @NonNull final String field;
    private @NonNull final FieldType fieldType;

    private transient volatile int hashCode; // cached hashcode for performance

    public static enum FunctionType{
        MAX,
        MIN,
        MEAN,
        SUM,
        COUNT
    }

    public static enum FieldType{
        INTEGER,
        DECIMAL,
        BOOLEAN,
        OBJECT,
        ARRAY
        //TODO: need to check other field types
    }

    /**
     * Initialize Aggregation
     * @param function Aggregation function.
     * @param field
     * @param fieldType
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

    public static Aggregation newAggregation(
            @NonNull FunctionType function,
            @NonNull String field,
            @NonNull FieldType fieldType) throws IllegalArgumentException {
        switch (function) {
            case COUNT:
                return Aggregation.newCountAggregation(field, fieldType);
            case MAX:
                return Aggregation.newMaxAggregation(field, fieldType);
            case MIN:
                return Aggregation.newMinAggregation(field, fieldType);
            case SUM:
                return Aggregation.newSumAggregation(field, fieldType);
            case MEAN:
                return Aggregation.newMeanAggregation(field, fieldType);
            default:
                throw new IllegalArgumentException("Unsupported function type");
        }
    }

    /**
     * Create count aggregation.
     * @param field name of filed.
     * @param fieldType type of field.
     * @return {@link Aggregation} instance.
     */
    public static Aggregation newCountAggregation(
            @NonNull String field,
            @NonNull FieldType fieldType) {
        return new Aggregation(FunctionType.COUNT, field, fieldType);
    }

    /**
     * Create mean aggregation.
     * @param field name of field.
     * @param fieldType type of field. Only integer and decimal allowed.
     * @return {@link Aggregation} instance.
     * @throws IllegalArgumentException Thrown when fieldType is neither integer or decimal.
     */
    public static Aggregation newMeanAggregation(
            @NonNull String field,
            @NonNull FieldType fieldType) throws IllegalArgumentException{
        if(fieldType != FieldType.INTEGER && fieldType != FieldType.DECIMAL) {
            throw new IllegalArgumentException("Unsupported field type for mean aggregation");
        }
        return new Aggregation(FunctionType.MEAN, field, fieldType);
    }

    /**
     * Create max aggregation.
     * @param field name of field.
     * @param fieldType type of field. Only integer and decimal allowed.
     * @return {@link Aggregation} instance.
     * @throws IllegalArgumentException Thrown when fieldType is neither integer or decimal.
     */
    public static Aggregation newMaxAggregation(
            @NonNull String field,
            @NonNull FieldType fieldType) throws IllegalArgumentException{
        if(fieldType != FieldType.INTEGER && fieldType != FieldType.DECIMAL) {
            throw new IllegalArgumentException("Unsupported field type for max aggregation");
        }
        return new Aggregation(FunctionType.MAX, field, fieldType);
    }

    /**
     * Create min aggregation.
     * @param field name of field.
     * @param fieldType type of field. Only integer and decimal allowed.
     * @return {@link Aggregation} instance.
     * @throws IllegalArgumentException Thrown when fieldType is neither integer or decimal.
     */
    public static Aggregation newMinAggregation(
            @NonNull String field,
            @NonNull FieldType fieldType) throws IllegalArgumentException{
        if(fieldType != FieldType.INTEGER && fieldType != FieldType.DECIMAL) {
            throw new IllegalArgumentException("Unsupported field type for min aggregation");
        }
        return new Aggregation(FunctionType.MIN, field, fieldType);
    }

    /**
     * Create sum aggregation.
     * @param field name of field.
     * @param fieldType type of field. Only integer and decimal allowed.
     * @return {@link Aggregation} instance.
     * @throws IllegalArgumentException Thrown when fieldType is neither integer or decimal.
     */
    public static Aggregation newSumAggregation(
            @NonNull String field,
            @NonNull FieldType fieldType) throws IllegalArgumentException{
        if(fieldType != FieldType.INTEGER && fieldType != FieldType.DECIMAL) {
            throw new IllegalArgumentException("Unsupported field type for sum aggregation");
        }
        return new Aggregation(FunctionType.SUM, field, fieldType);
    }

    public FunctionType getFunction() {
        return function;
    }

    public String getField() {
        return field;
    }

    public FieldType getFieldType() { return  fieldType; }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Aggregation)) {
            return false;
        }
        Aggregation other = (Aggregation) o;
        return this.function.equals(other.function) &&
                this.field.equals(other.field) &&
                this.fieldType.equals(other.fieldType);
    }

    @Override
    public int hashCode() {
        int result = this.hashCode;
        if (result == 0) {
            result = 17;
            result = 31 * result + this.function.hashCode();
            result = 31 * result + this.field.hashCode();
            result = 31 * result + this.fieldType.hashCode();
            this.hashCode = result;
        }
        return result;
    }

    protected Aggregation(Parcel in) {
        //TODO: // FIXME: 12/22/16 check where read serializable for enum
        this.function = (FunctionType) in.readSerializable();
        this.field = in.readString();
        //TODO: // FIXME: 12/21/16 read fieldType
        this.fieldType = (FieldType) in.readSerializable();
    }
}