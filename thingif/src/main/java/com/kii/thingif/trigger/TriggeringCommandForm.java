package com.kii.thingif.trigger;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kii.thingif.TypedID;
import com.kii.thingif.command.Action;

import org.json.JSONObject;

import java.util.List;

public final class TriggeringCommandForm implements Parcelable {

    public TriggeringCommandForm(
            @NonNull String schemaName,
            int schemaVersion,
            @NonNull TypedID targetID,
            @NonNull List<Action> actions)
        throws IllegalArgumentException
    {
        // TODO: implement me.
    }

    public String getSchemaName() {
        // TODO: implement me.
        return null;
    }

    public int getSchemaVersion() {
        // TODO: implement me.
        return 0;
    }

    public TypedID getTargetID() {
        // TODO: implement me.
        return null;
    }

    public List<Action> getActions() {
        // TODO: implement me.
        return null;
    }

    @Nullable
    public String getTitle() {
        // TODO: implement me.
        return null;
    }

    public void setTitle(@Nullable String title) {
        // TODO: implement me.
    }

    @Nullable
    public String getDescription() {
        // TODO: implement me.
        return null;
    }

    public void setDescription(@Nullable String description) {
        // TODO: implement me.
    }

    @Nullable
    public JSONObject getMetadata() {
        // TODO: implement me.
        return null;
    }

    public void setMetadata(@Nullable JSONObject metadata) {
        // TODO: implement me.
    }

    protected TriggeringCommandForm(Parcel in) {
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

    public static final Creator<TriggeringCommandForm> CREATOR =
            new Creator<TriggeringCommandForm>() {
        @Override
        public TriggeringCommandForm createFromParcel(Parcel in) {
            // TODO: implement me.
            return new TriggeringCommandForm(in);
        }

        @Override
        public TriggeringCommandForm[] newArray(int size) {
            // TODO: implement me.
            return new TriggeringCommandForm[size];
        }
    };
}
