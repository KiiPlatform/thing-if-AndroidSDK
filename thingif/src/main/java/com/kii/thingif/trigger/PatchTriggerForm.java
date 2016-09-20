package com.kii.thingif.trigger;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONObject;

/**
 */
public final class PatchTriggerForm implements Parcelable {

    public static final class Builder {

        @NonNull
        public static Builder builder(
                @NonNull String triggerID,
                @NonNull TriggeredCommandForm.Builder form,
                @NonNull Predicate predicate)
            throws IllegalArgumentException
        {
            // TODO: implement me.
            return null;
        }

        @NonNull
        public static Builder build(
                @NonNull Trigger trigger)
            throws IllegalArgumentException
        {
            // TODO: implement me.
            return null;
        }

        @NonNull
        public Builder setTriggerID(
            @NonNull String triggerID) {
            // TODO: implement me.
            return this;
        }

        @NonNull
        public String getTriggerID() {
            return null;
        }

        @NonNull
        public Builder setForm(
                @Nullable TriggeredCommandForm.Builder form)
            throws IllegalArgumentException
        {
            return this;
        }

        @Nullable
        public TriggeredCommandForm.Builder getForm() {
            // TODO: implement me.
            return null;
        }

        @NonNull
        public Builder setPredicate(
                @Nullable Predicate predicate)
            throws IllegalArgumentException
        {
            return this;
        }

        @Nullable
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
        public PatchTriggerForm build() {
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

    protected PatchTriggerForm(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PatchTriggerForm> CREATOR =
            new Creator<PatchTriggerForm>() {
        @Override
        public PatchTriggerForm createFromParcel(Parcel in) {
            return new PatchTriggerForm(in);
        }

        @Override
        public PatchTriggerForm[] newArray(int size) {
            return new PatchTriggerForm[size];
        }
    };
}