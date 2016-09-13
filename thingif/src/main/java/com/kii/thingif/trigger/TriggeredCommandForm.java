package com.kii.thingif.trigger;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kii.thingif.TypedID;
import com.kii.thingif.command.Action;

import org.json.JSONObject;

import java.util.List;

/**
 * Form of a command in trigger request.
 *
 * This clas contains data in order to create {@link
 * com.kii.thingif.command.Command} instance got from {@link
 * Trigger#getCommand()}.
 * <br><br>
 * Mandatory data are followings:
 * <ul>
 * <li>Name of a schema</li>
 * <li>Version of a schema</li>
 * <li>Target thing ID</li>
 * <li>List of actions</li>
 * </ul>
 * Optional data are followings:
 * <ul>
 * <li>Title of a schema</li>
 * <li>Description of a schema</li>
 * <li>meta data of a schema</li>
 * </ul>
 */
public final class TriggeredCommandForm implements Parcelable {

    /**
     * Constructs a {@link TriggeredCommandForm} instance.
     *
     * @param schemaName name of schema. Must not be null or empty string.
     * @param schemaVersion version of schema.
     * @param targetID thing ID issued this command. Must not be
     * null. Returning value of {@link TypedID#getType()} must be {@link
     * com.kii.thingif.TypedID.Types#THING}.
     * @param actions List of actions. Must not be null or empty.
     * @throws IllegalArgumentException This exception is thrown if one or
     * more following condition are met.
     * <ul>
     *   <li>schemaName is null or empty string.</li>
     *   <li>targetID is null or type is not {@link
     *   com.kii.thingif.TypedID.Types#THING}.</li>
     *   <li>actions is null or empty</li>
     * </ul>
     */
    public TriggeredCommandForm(
            @NonNull String schemaName,
            int schemaVersion,
            @NonNull TypedID targetID,
            @NonNull List<Action> actions)
        throws IllegalArgumentException
    {
        // TODO: implement me.
    }

    @NonNull
    public String getSchemaName() {
        // TODO: implement me.
        return null;
    }

    @NonNull
    public int getSchemaVersion() {
        // TODO: implement me.
        return 0;
    }

    @NonNull
    public TypedID getTargetID() {
        // TODO: implement me.
        return null;
    }

    @NonNull
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

    protected TriggeredCommandForm(Parcel in) {
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

    public static final Creator<TriggeredCommandForm> CREATOR =
            new Creator<TriggeredCommandForm>() {
        @Override
        public TriggeredCommandForm createFromParcel(Parcel in) {
            // TODO: implement me.
            return new TriggeredCommandForm(in);
        }

        @Override
        public TriggeredCommandForm[] newArray(int size) {
            // TODO: implement me.
            return new TriggeredCommandForm[size];
        }
    };
}
