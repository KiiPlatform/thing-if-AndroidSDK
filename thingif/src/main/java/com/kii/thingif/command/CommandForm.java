package com.kii.thingif.command;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONObject;

import java.util.List;

/**
 * Form of a command.
 *
 * This class contains data in order to create {@link Command} with {@link
 * com.kii.thingif.ThingIFAPI#postNewCommand(CommandForm)}.
 * <br><br>
 * Mandatory data are followings:
 * <ul>
 * <li>Name of a schema</li>
 * <li>Version of a schema</li>
 * <li>List of actions</li>
 * </ul>
 * Optional data are followings:
 * <ul>
 * <li>Title of a schema</li>
 * <li>Description of a schema</li>
 * <li>meta data of a schema</li>
 * </ul>
 */
public final class CommandForm implements Parcelable {

    private final @NonNull String schemaName;
    private final int schemaVersion;
    private final @NonNull List<Action> actions;

    private @Nullable String firedByTriggerID;
    private @Nullable String title;
    private @Nullable String description;
    private @Nullable JSONObject metadata;

    /**
     * Constructs a CommandForm instance.
     *
     * @param schemaName name of schema. Must not be null or empty string.
     * @param schemaVersion version of schema.
     * @param actions List of actions. Must not be null or empty.
     * @throws IllegalArgumentException when schemaName is null or empty
     * string and/or actions is null or empty.
     */
    public CommandForm(
            @NonNull String schemaName,
            int schemaVersion,
            @NonNull List<Action> actions)
        throws IllegalArgumentException
    {
        if (schemaName.length() == 0) {
            throw new IllegalArgumentException("schemaName must not be empty.");
        }
        if (actions.size() == 0) {
            throw new IllegalArgumentException("actions must contain at least one Action.");
        }
        this.schemaName = schemaName;
        this.schemaVersion = schemaVersion;
        this.actions = actions;
    }

    /**
     * Setter of firedByTriggerID
     *
     * @param id ID of the trigger if command invoked by trigger.
     * @return this instance
     */
    public CommandForm setFiredByTriggerID(
            @Nullable String id)
    {
        this.firedByTriggerID = id;
        return this;
    }

    /**
     * Setter of title
     *
     * @param title Length of title must be equal or less than 50 characters.
     * @return this instance
     * @throws IllegalArgumentException if title is invalid.
     */
    public CommandForm setTitle(
            @Nullable String title)
        throws IllegalArgumentException
    {
        if (title != null && title.length() > 50) {
            throw new IllegalArgumentException("title length must be max 50.");
        }
        this.title = title;
        return this;
    }

    /**
     * Setter of description
     *
     * @param description Length of description must be equal or less than
     * 200 characters.
     * @return this instance.
     * @throws IllegalArgumentException if description is invalid.
     */
    public CommandForm setDescription(@Nullable String description) {
        if (description != null && description.length() > 200) {
            throw new IllegalArgumentException("description length must be max 200.");
        }
        this.description = description;
        return this;
    }

    /**
     * Setter of meta data.
     *
     * @param metadata
     * @return this instance.
     */
    public CommandForm setMetadata(@Nullable JSONObject metadata) {
        this.metadata = metadata;
        return this;
    }

    /**
     * Getter of schema name.
     *
     * @return schema name
     */
    @NonNull
    public String getSchemaName() {
        return this.schemaName;
    }

    /**
     *  Getter of schema version.
     *
     * @return schema version
     */
    public int getSchemaVersion() {
        return this.schemaVersion;
    }

    /**
     * Getter of actions.
     *
     * @return actions
     */
    @NonNull
    public List<Action> getActions() {
        return this.actions;
    }

    /**
     * Getter of firedByTriggerID.
     *
     * @return firedByTriggerID
     */
    @Nullable
    public String getFiredByTriggerID() {
        return this.firedByTriggerID;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO: implement me.
    }
}
