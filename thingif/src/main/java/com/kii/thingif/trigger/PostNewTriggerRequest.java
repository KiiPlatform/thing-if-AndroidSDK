package com.kii.thingif.trigger;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONObject;

/**
 */
public final class PostNewTriggerRequest implements Parcelable {

    public static final class Builder {

        @NonNull
        public static Builder builder(
                @NonNull TriggeredCommandForm form,
                @NonNull Predicate predicate)
            throws IllegalArgumentException
        {
            // TODO: implement me.
            return null;
        }

        @NonNull
        public Builder setForm(
                @NonNull TriggeredCommandForm form)
            throws IllegalArgumentException
        {
            return this;
        }

        @NonNull
        public TriggeredCommandForm getForm() {
            // TODO: implement me.
            return null;
        }

        @NonNull
        public Builder setPredicate(
                @NonNull Predicate predicate)
            throws IllegalArgumentException
        {
            return this;
        }

        @NonNull
        public Predicate getPredicate() {
            // TODO: implement me.
            return null;
        }

        @NonNull
        public Builder setTitle(
                @Nullable String title)
            throws IllegalArgumentException
        {
            return this;
        }

        @Nullable
        public String getTitle() {
            // TODO: implement me.
            return null;
        }

        @NonNull
        public Builder setDescription(
                @Nullable String description)
            throws IllegalArgumentException
        {
            return this;
        }

        @Nullable
        public String getDescription() {
            // TODO: implement me.
            return null;
        }

        @NonNull
        public Builder setMetadata(@Nullable JSONObject metadata) {
            return this;
        }

        @Nullable
        public JSONObject getMetadata() {
            // TODO: implement me.
            return null;
        }

        @NonNull
        public PostNewTriggerRequest build() {
            // TODO: implement me.
            return null;
        }

    }

    @NonNull
    public TriggeredCommandForm getForm() {
        // TODO: implement me.
        return null;
    }

    @NonNull
    public Predicate getPredicate() {
        // TODO: implement me.
        return null;
    }

    @Nullable
    public String getTitle() {
        // TODO: implement me.
        return null;
    }

    @Nullable
    public String getDescription() {
        // TODO: implement me.
        return null;
    }

    @Nullable
    public JSONObject getMetadata() {
        // TODO: implement me.
        return null;
    }


    protected PostNewTriggerRequest(Parcel in) {
        // TODO: implement me.
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO: implement me.
    }

    @Override
    public int describeContents() {
        // TODO: implement me.
        return 0;
    }

    public static final Creator<PostNewTriggerRequest> CREATOR =
            new Creator<PostNewTriggerRequest>() {
        @Override
        public PostNewTriggerRequest createFromParcel(Parcel in) {
            // TODO: implement me.
            return new PostNewTriggerRequest(in);
        }

        @Override
        public PostNewTriggerRequest[] newArray(int size) {
            // TODO: implement me.
            return new PostNewTriggerRequest[size];
        }
    };
}
