package com.kii.thingif.trigger;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.kii.thingif.TypedID;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.Command;

import org.json.JSONObject;

import java.util.List;

/**
 * Form of a command in trigger request.
 *
 * This class contains data in order to create {@link
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

    /**
     * TriggeredCommandForm builder.
     */
    public static class Builder {

        @NonNull private String schemaName;
        @NonNull private int schemaVersion;
        @NonNull private List<Action> actions;
        @Nullable private TypedID targetID;
        @Nullable private String title;
        @Nullable private String description;
        @Nullable private JSONObject metadata;

        Builder(
                @NonNull String schemaName,
                int schemaVersion,
                @NonNull List<Action> actions)
        {
            if (TextUtils.isEmpty(schemaName)) {
                throw new IllegalArgumentException(
                    "schemaName is null or empty.");
            }
            if (CollectionUtils.isEmpty(actions)) {
                throw new IllegalArgumentException("actions is null or empty.");
            }
            this.schemaName = schemaName;
            this.schemaVersion = schemaVersion;
            this.actions = actions;
        }

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
         * @return builder instance.
         */
        @NonNull
        public static Builder builder(
                @NonNull String schemaName,
                int schemaVersion,
                @NonNull List<Action> actions)
            throws IllegalArgumentException
        {
            return new Builder(schemaName, schemaVersion, actions);
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
         * @return builder instance.
         * @throws IllegalArgumentException if command is null.
         */
        @NonNull
        public static Builder builder(
                @NonNull Command command)
            throws IllegalArgumentException
        {
            return (new Builder(
                        command.getSchemaName(),
                        command.getSchemaVersion(),
                        command.getActions())).
                    setTargetID(command.getTargetID()).
                    setTitle(command.getTitle()).
                    setDescription(command.getDescription()).
                    setMetadata(command.getMetadata());
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
         * @throws IllegalArgumentException if schemaName is invalid.
         */
        @NonNull
        public Builder setSchemaName(
                @NonNull String schemaName)
            throws IllegalArgumentException
        {
            if (TextUtils.isEmpty(schemaName)) {
                throw new IllegalArgumentException(
                    "schemaName is null or empty.");
            }
            this.schemaName = schemaName;
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
         * Setter of schema version.
         *
         * @param schemaVersion schema version.
         * @return this instance for method chaining.
         */
        @NonNull
        public Builder setSchemaVersion(int schemaVersion) {
            this.schemaVersion = schemaVersion;
            return this;
        }

        /**
         *  Getter of schema version.
         *
         * @return schema version
         */
        @NonNull
        public int getSchemaVersion() {
            return this.schemaVersion;
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
            if (CollectionUtils.isEmpty(actions)) {
                throw new IllegalArgumentException("actions is null or empty.");
            }
            this.actions = actions;
            return this;
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
         * Setter of target thing ID.
         *
         * <p>
         * {@link
         * com.kii.thingif.ThingIFAPI#postNewTrigger(TriggeredCommandForm,
         * Predicate, TriggerOptions)} and {@link
         * com.kii.thingif.ThingIFAPI#patchTrigger(String,
         * TriggeredCommandForm, Predicate, TriggerOptions)} use {@link
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
            if (targetID != null && targetID.type == Types.THING) {
                throw new IllegalArgumentException(
                    "targetID type must be Types.THING");
            }
            this.targetID = targetID;
            return this;
        }

        /**
         *  Getter of target thing ID.
         *
         * @return target thing ID
         */
        @Nullable
        public TypedID getTargetID() {
            return this.targetID;
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
            return this.meta;
        }

        /**
         * Build {@link TriggeredCommandForm} instance.
         *
         * @return {@link TriggeredCommandForm} instance.
         */
        @NonNull
        public TriggeredCommandForm build() {
            TriggeredCommandForm retval =
                    new TriggeredCommandForm(
                        this.schemaName, this.schemaVersion, this.actions);
            retval.targetID = this.targetID;
            retval.title = this.title;
            retval.description = this.description;
            retval.metadata = this.metadata;
            return retval;
        }

    }

    @NonNull private final String schemaName;
    @NonNull private final int schemaVersion;
    @NonNull private final List<Action> actions;
    @Nullable private TypedID targetID;
    @Nullable private String title;
    @Nullable private String description;
    @Nullable private JSONObject metadata;

    private TriggeredCommandForm(
            @NonNull String schemaName,
            int schemaVersion,
            @NonNull actions)
    {
        this.schemaName = schemaName;
        this.schemaVersion = schemaVersion;
        this.actions = actions;
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
     *  Getter of target thing ID.
     *
     * @return target thing ID
     */
    @Nullable
    public TypedID getTargetID() {
        return this.targetID;
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
