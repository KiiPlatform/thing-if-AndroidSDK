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

    /**
     * Getter of schema name.
     *
     * @return schema name
     */
    @NonNull
    public String getSchemaName() {
        // TODO: implement me.
        return null;
    }

    /**
     *  Getter of schema version.
     *
     * @return schema version
     */
    @NonNull
    public int getSchemaVersion() {
        // TODO: implement me.
        return 0;
    }

    /**
     *  Getter of target thing ID.
     *
     * @return target thing ID
     */
    @NonNull
    public TypedID getTargetID() {
        // TODO: implement me.
        return null;
    }

    /**
     * Getter of actions.
     *
     * @return actions
     */
    @NonNull
    public List<Action> getActions() {
        // TODO: implement me.
        return null;
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
     * Setter of title
     *
     * @param title Length of title must be equal or less than 50 characters.
     * @return this instance
     * @throws IllegalArgumentException if title is invalid.
     */
    @NonNull
    public TriggeredCommandForm setTitle(
            @Nullable String title)
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
     * Setter of description
     *
     * @param description Length of description must be equal or less than
     * 200 characters.
     * @return this instance.
     * @throws IllegalArgumentException if description is invalid.
     */
    @NonNull
    public TriggeredCommandForm setDescription(
            @Nullable String description)
        throws IllegalArgumentException
    {
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
     * Setter of meta data.
     *
     * @param metadata meta data of this command.
     * @return this instance.
     */
    @NonNull
    public TriggeredCommandForm setMetadata(@Nullable JSONObject metadata) {
        // TODO: implement me.
        return this;
    }

    private TriggeredCommandForm(Parcel in) {
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
