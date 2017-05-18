package com.kii.thing_if.trigger;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Options of trigger.
 */
public class TriggerOptions {

    /**
     * TriggerOptions builder.
     */
    public static class Builder {

        @Nullable private String title;
        @Nullable private String description;
        @Nullable private JSONObject metadata;

        private Builder() {
        }

        /**
         * Constructs a {@link TriggerOptions.Builder} instance.
         *
         * @return builder instance.
         */
        @NonNull
        public static Builder newBuilder() {
            return new Builder();
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
            if (title != null && title.length() > 50) {
                throw new IllegalArgumentException(
                    "title is more than 50 charactors.");
            }
            this.title = title;
            return this;
        }

        /**
         * Getter of titile.
         *
         * @return title
         */
        @Nullable
        public String getTitle() {
            return this.title;
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
            if (description != null && description.length() > 200) {
                throw new IllegalArgumentException(
                    "description is more than 200 charactors.");
            }
            this.description = description;
            return this;
        }

        /**
         * Getter of description.
         *
         * @return description
         */
        @Nullable
        public String getDescription() {
            return this.description;
        }

        /**
         * Setter of meta data.
         *
         * @param metadata meta data of this command.
         * @return this instance for method chaining.
         */
        @NonNull
        public Builder setMetadata(@Nullable JSONObject metadata) {
            this.metadata = metadata;
            return this;
        }

        /**
         * Getter of meta data.
         *
         * @return meta data
         */
        @Nullable
        public JSONObject getMetadata() {
            return this.metadata;
        }

        /**
         * Build {@link TriggerOptions} instance.
         *
         * @return {@link TriggerOptions} instance.
         */
        @NonNull
        public TriggerOptions build() {
            return new TriggerOptions(this.title, this.description,
                    this.metadata);
        }
    }

    @Nullable private String title;
    @Nullable private String description;
    @Nullable private JSONObject metadata;

    private TriggerOptions(
            @Nullable String title,
            @Nullable String description,
            @Nullable JSONObject metadata)
    {
        this.title = title;
        this.description = description;
        if (metadata != null) {
            try {
                this.metadata = new JSONObject(metadata.toString());
            } catch (JSONException e) {
                // Nerver happen.
            }
        }
    }

    /**
     * Getter of titile.
     *
     * @return title
     */
    @Nullable
    public String getTitle() {
        return this.title;
    }

    /**
     * Getter of description.
     *
     * @return description
     */
    @Nullable
    public String getDescription() {
        return this.description;
    }

    /**
     * Getter of meta data.
     *
     * @return meta data
     */
    @Nullable
    public JSONObject getMetadata() {
        return this.metadata;
    }
}
