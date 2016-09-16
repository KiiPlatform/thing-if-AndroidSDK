package com.kii.thingif.trigger;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONObject;

/**
 * Options of trigger.
 */
public final class TriggerOptions implements Parcelable {

    /**
     * TriggerOptions builder.
     */
    public static final class Builder {

        /**
         * Constructs a {@link TriggerOptions.Builder} instance.
         *
         * @return builder instance.
         */
        @NonNull
        public static Builder builder() {
            // TODO: implement me.
            return null;
        }

        /**
         * Constructs a {@link TriggerOptions.Builder} instance.
         * 
         * <p>
         * This constructor copies followings {@link Trigger} fields:
         * </p>
         *
         * <ul>
         * <li>{@link Trigger#getTitle()}</li>
         * <li>{@link Trigger#getDescription()}</li>
         * <li>{@link Trigger#getMetadata()}</li>
         * </ul>
         *
         * @param trigger source of this {@link TriggerOptions.Builder}
         * instance.
         * @return builder instance
         * @throws IllegalArgumentException if trigger is null.
         */
        @NonNull
        public static Builder builder(
                @NonNull Trigger trigger)
            throws IllegalArgumentException
        {
            // TODO: implement me.
            return null;
        }

        /**
         * Setter of title
         *
         * @param title Length of title must be equal or less than 50
         * characters.
         * @return this instance for method chaining.
         * @throws IllegalArgumentException if title is invalid.
         */
        @NonNull
        public Builder setTitle(
                @Nullable String title)
            throws IllegalArgumentException
        {
            // TODO: implement me.
            return this;
        }

        /**
         * Getter of titile.
         *
         * @return title
         */
        @Nullable
        public String getTitle() {
            // TODO: implement me.
            return null;
        }

        /**
         * Setter of description
         *
         * @param description Length of description must be equal or less
         * than 200 characters.
         * @return this instance for method chaining.
         * @throws IllegalArgumentException if description is invalid.
         */
        @NonNull
        public Builder setDescription(
                @Nullable String description)
            throws IllegalArgumentException
        {
            // TODO: implement me.
            return this;
        }

        /**
         * Getter of description.
         *
         * @return description
         */
        @Nullable
        public String getDescription() {
            // TODO: implement me.
            return null;
        }

        /**
         * Setter of meta data.
         *
         * @param metadata meta data of this command.
         * @return this instance for method chaining.
         */
        @NonNull
        public Builder setMetadata(@Nullable JSONObject metadata) {
            // TODO: implement me.
            return this;
        }

        /**
         * Getter of meta data.
         *
         * @return meta data
         */
        @Nullable
        public JSONObject getMetadata() {
            // TODO: implement me.
            return null;
        }

        /**
         * Build {@link TriggerOptions} instance.
         *
         * @return {@link TriggerOptions} instance.
         */
        @NonNull
        public TriggerOptions build() {
            // TODO: implement me.
            return null;
        }
    }

    /**
     * Getter of titile.
     *
     * @return title
     */
    @Nullable
    public String getTitle() {
        // TODO: implement me.
        return null;
    }

    /**
     * Getter of description.
     *
     * @return description
     */
    @Nullable
    public String getDescription() {
        // TODO: implement me.
        return null;
    }

    /**
     * Getter of meta data.
     *
     * @return meta data
     */
    @Nullable
    public JSONObject getMetadata() {
        // TODO: implement me.
        return null;
    }

    protected TriggerOptions(Parcel in) {
        // TODO: implements me.
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO: implements me.
    }

    @Override
    public int describeContents() {
        // TODO: implements me.
        return 0;
    }

    public static final Creator<TriggerOptions> CREATOR =
            new Creator<TriggerOptions>() {
        @Override
        public TriggerOptions createFromParcel(Parcel in) {
            // TODO: implements me.
            return new TriggerOptions(in);
        }

        @Override
        public TriggerOptions[] newArray(int size) {
            // TODO: implements me.
            return new TriggerOptions[size];
        }
    };
}
