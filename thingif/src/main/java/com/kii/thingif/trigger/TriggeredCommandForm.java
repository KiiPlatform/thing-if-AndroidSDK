package com.kii.thingif.trigger;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kii.thingif.TypedID;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.Command;

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
 * <li>List of actions</li>
 * </ul>
 * Optional data are followings:
 * <ul>
 * <li>Target thing ID</li>
 * <li>Title of a schema</li>
 * <li>Description of a schema</li>
 * <li>meta data of a schema</li>
 * </ul>
 */
public final class TriggeredCommandForm implements Parcelable {

    public static class Builder {

        /**
         * Constructs a {@link TriggeredCommandForm.Builder} instance.
         *
         * @param schemaName name of schema. Must not be null or empty string.
         * @param schemaVersion version of schema.
         * @param actions List of actions. Must not be null or empty.
         * @throws IllegalArgumentException This exception is thrown if one or
         * more following conditions are met.
         * <ul>
         *   <li>schemaName is null or empty string.</li>
         *   <li>actions is null or empty</li>
         * </ul>
         */
        @NonNull
        public static Builder builder(
                @NonNull String schemaName,
                int schemaVersion,
                @NonNull List<Action> actions)
            throws IllegalArgumentException
        {
            // TODO: implement me.
            return null;
        }

        /**
         * Constructs a {@link TriggeredCommandForm.Builder} instance.
         *
         * <p>
         * This constructor copies following {@link Command} fields:
         * </p>
         *
         * <ul>
         *   <li>{@link Command#getSchemaName()}</li>
         *   <li>{@link Command#getSchemaVersion()}</li>
         *   <li>{@link Command#getActions()}</li>
         *   <li>{@link Command#getTargetID()}</li>
         *   <li>{@link Command#getTitle()}</li>
         *   <li>{@link Command#getDescription()}</li>
         *   <li>{@link Command#getMetadata()}</li>
         * </ul>
         *
         * @param command Souce of this {@link TriggeredCommandForm.Builder}
         * instance.
         * @throws IllegalArgumentException if command is null.
         */
        @NonNull
        public static Builder builder(
                @NonNull Command command)
            throws IllegalArgumentException
        {
            // TODO: implement me.
            return null;
        }

        /**
         * Setter of schema name.
         *
         * <p>
         * Schema name is requried field of command, so null and empty string
         * is not acceptable.
         * </p>
         *
         * @param schemaName schema name.
         * @return this instance for method chaining.
         * @throws IllegalArgumentException
         */
        @NonNull
        public Builder setSchemaName(
                @NonNull String schemaName)
            throws IllegalArgumentException
        {
            // TODO: implement me.
            return this;
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
         * Setter of schema version.
         *
         * @param schemaVersion schema version.
         * @return this instance for method chaining.
         */
        @NonNull
        public Builder setSchemaVersion(int schemaVersion) {
            // TODO: implement me.
            return this;
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
         * Setter of actions.
         *
         * <p>
         * List of action is required field of command, so null and empty
         * list is not acceptable.
         * </p>
         *
         * @param actions List of action.
         * @return this instance for method chaining.
         * @throws IllegalArgumentException actions is null or empty list.
         */
        @NonNull
        public Builder setActions(
                @NonNull List<Action> actions)
            throws IllegalArgumentException
        {
            // TODO: implement me.
            return this;
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
         * Setter of target thing ID.
         *
         * <p>
         * {@link
         * com.kii.thingif.ThingIFAPI#postNewTrigger(TriggeredCommandForm,
         * Predicate)} and {@link
         * com.kii.thingif.ThingIFAPI#patchTrigger(String,
         * TriggeredCommandForm, Predicate)} use {@link
         * TriggeredCommandForm#getTargetID()} to specify target of command
         * in trigger. If you do not set target thing ID with this method,
         * Default target is used. The default target is {@link
         * com.kii.thingif.ThingIFAPI#getTarget()}.
         * </p>
         *
         * <p>
         * If you create trigger which target of command is not default
         * target, and update trigger with {@link
         * TriggeredCommandForm#getTargetID()} as null, then, command target
         * of updated trigger is changed to default target.
         * </p>
         *
         * @param targetID target thing ID.
         * @return this instance for method chaining.
         * @throws IllegalArgumentException type of targetID is not {@link
         * TypedID.Types#THING}.
         */
        @NonNull
        public Builder setTargetID(
                @Nullable TypedID targetID)
            throws IllegalArgumentException
        {
            // TODO: implement me.
            return this;
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

        @NonNull
        public TriggeredCommandForm build() {
            // TODO: implement me.
            return null;
        }

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
