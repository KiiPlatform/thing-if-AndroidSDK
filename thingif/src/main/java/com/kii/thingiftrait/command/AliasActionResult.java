package com.kii.thingiftrait.command;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents action results of specified alias.
 */
public class AliasActionResult implements Parcelable{
    @NonNull private String alias;
    @NonNull private List<ActionResult> results;

    private transient volatile int hashCode; // cached hashcode for performance

    AliasActionResult(
            @NonNull String alias,
            @NonNull List<ActionResult> results) {
        this.alias = alias;
        this.results = results;
    }
    @NonNull
    public List<ActionResult> getResults() {
        return results;
    }

    @NonNull
    public String getAlias() {
        return alias;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.alias);
        dest.writeList(this.results);
    }

    /**
     * Initialize AliasActionResult with Parcel instance.
     * @param in Parcel instance.
     * @throws IllegalArgumentException thrown when alias is null/empty or results is null/empty
     */
    public AliasActionResult(Parcel in) {
        this.alias = in.readString();
        if (TextUtils.isEmpty(alias)) {
            throw new IllegalArgumentException("alias is null or empty");
        }
        this.results = new ArrayList<>();
        in.readList(this.results, ActionResult.class.getClassLoader());
        if (results == null || results.size() == 0) {
            throw new IllegalArgumentException("result is null or empty");
        }
    }

    public static final Creator<AliasActionResult> CREATOR = new Creator<AliasActionResult>() {
        @Override
        public AliasActionResult createFromParcel(Parcel source) {
            return new AliasActionResult(source);
        }

        @Override
        public AliasActionResult[] newArray(int size) {
            return new AliasActionResult[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof AliasActionResult)) return false;
        AliasActionResult result = (AliasActionResult)o;
        return this.alias.equals(result.getAlias()) &&
                Arrays.equals(this.results.toArray(), result.getResults().toArray());
    }

    @Override
    public int hashCode() {
        int result = this.hashCode;
        if (result == 0) {
            result = 17;
            result = 31 * result + this.alias.hashCode();
            result = 31 * result + this.results.hashCode();
            this.hashCode = result;
        }
        return result;
    }
}

