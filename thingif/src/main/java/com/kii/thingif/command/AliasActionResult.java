package com.kii.thingif.command;

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

    private volatile int hashCode; // cached hashcode for performance

    /**
     * Initialize AliasActionResult instance.
     * @param alias name of alias
     * @param results list of {@link ActionResult} instance
     * @throws IllegalArgumentException thrown when alias is null/empty or results is null/empty
     */
    public AliasActionResult(
            @NonNull String alias,
            @NonNull List<ActionResult> results) {
        if (TextUtils.isEmpty(alias)) {
            throw new IllegalArgumentException("alias is null or empty");
        }

        if (results == null || results.size() == 0) {
            throw new IllegalArgumentException("result is null or empty");
        }
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

    public AliasActionResult(Parcel in) {
        this.alias = in.readString();
        this.results = new ArrayList<>();
        in.readList(this.results, ActionResult.class.getClassLoader());
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

