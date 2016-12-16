package com.kii.thingif.trigger;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;

import com.google.gson.annotations.SerializedName;
import com.kii.thingif.Alias;
import com.kii.thingif.NonTraitAlias;
import com.kii.thingif.TraitAlias;
import com.kii.thingif.TypedID;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.Command;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
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
 * <li>List of actions</li>
 * </ul>
 * Optional data are followings:
 * <ul>
 * <li>Target thing ID</li>
 * <li>Title of a command</li>
 * <li>Description of a command</li>
 * <li>meta data of a command</li>
 * </ul>
 */
public class TriggeredCommandForm<T extends Alias> implements Parcelable {

    /**
     * TriggeredCommandForm builder.
     */
    public static class Builder<T1 extends Alias> {

        @NonNull private List<Pair<T1, List<Action>>> actions;
        @Nullable private TypedID targetID;
        @Nullable private String title;
        @Nullable private String description;
        @Nullable private JSONObject metadata;

        private Builder(
                @NonNull List<Pair<T1, List<Action>>> actions)
        {
            if (isEmpty(actions)) {
                throw new IllegalArgumentException("actions is null or empty.");
            }
            this.actions = actions;
        }

        /**
         * Constructs a {@link TriggeredCommandForm.Builder} instance.
         *
         * @param actions List of actions. Must not be null or empty.
         * @throws IllegalArgumentException This exception is thrown if one or
         * more following conditions are met.
         * <ul>
         *   <li>actions is null or empty</li>
         * </ul>
         * @return builder instance.
         */
        @NonNull
        public static <T2 extends Alias>Builder<T2> newBuilder(
                @NonNull List<Pair<T2, List<Action>>> actions)
        {
            return new Builder<>(actions);
        }

        /**
         * Constructs a {@link TriggeredCommandForm.Builder} instance.
         *
         * <p>
         * This constructor copies following {@link Command} fields:
         * </p>
         *
         * <ul>
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
        public static <T3 extends Alias> Builder<T3> newBuilderFromCommand(
                @NonNull Command<T3> command)
            throws IllegalArgumentException
        {
            return (new Builder<>(command.getActions())).
                    setTargetID(command.getTargetID()).
                    setTitle(command.getTitle()).
                    setDescription(command.getDescription()).
                    setMetadata(command.getMetadata());
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
        public Builder<T1> setActions(
                @NonNull List<Pair<T1, List<Action>>> actions)
            throws IllegalArgumentException
        {
            if (isEmpty(actions)) {
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
        public List<Pair<T1, List<Action>>> getActions() {
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
        public Builder<T1> setTargetID(
                @Nullable TypedID targetID)
            throws IllegalArgumentException
        {
            if (targetID != null && targetID.getType() != TypedID.Types.THING) {
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
        public Builder<T1> setTitle(
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
        public Builder<T1> setDescription(
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
        public Builder<T1> setMetadata(@Nullable JSONObject metadata) {
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
         * Build {@link TriggeredCommandForm} instance.
         *
         * @return {@link TriggeredCommandForm} instance.
         */
        @NonNull
        public TriggeredCommandForm<T1> build() {

            TriggeredCommandForm<T1> retval =
                    new TriggeredCommandForm<T1>(this.actions);
            retval.targetID = this.targetID;
            retval.title = this.title;
            retval.description = this.description;
            if (this.metadata != null) {
                try {
                    retval.metadata = new JSONObject(this.metadata.toString());
                } catch (JSONException e) {
                    // Never happen.
                }
            }
            return retval;
        }

        private static boolean isEmpty(Collection<?> collection) {
            return collection == null || collection.isEmpty();
        }

    }

    @NonNull private final List<Pair<T, List<Action>>> actions;
    @SerializedName("target")
    @Nullable private TypedID targetID;
    @Nullable private String title;
    @Nullable private String description;
    @Nullable private JSONObject metadata;

    private TriggeredCommandForm(
            @NonNull List<Pair<T, List<Action>>> actions)
    {
        this.actions = actions;
    }

    /**
     * Getter of actions.
     *
     * @return actions
     */
    @NonNull
    public List<Pair<T, List<Action>>> getActions() {
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
        //TODO: // FIXME: 12/16/16 should adapt to alias subclasses
        this.actions = new ArrayList<>();
        in.readList(this.actions, TriggeredCommandForm.class.getClassLoader());
        this.targetID = in.readParcelable(TypedID.class.getClassLoader());
        this.title = in.readString();
        this.description = in.readString();
        String metadata = in. readString();
        if (metadata != null) {
            try {
                this.metadata = new JSONObject(metadata);
            } catch (JSONException e) {
                // Never happen.
            }
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //TODO: // FIXME: 12/16/16 should adapt to alias subclass
        dest.writeList(this.actions);
        dest.writeParcelable(this.getTargetID(), flags);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.metadata != null ?
                this.metadata.toString() : null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TriggeredCommandForm> CREATOR =
            new Creator<TriggeredCommandForm>() {
        @Override
        public TriggeredCommandForm createFromParcel(Parcel in) {
            return new TriggeredCommandForm(in);
        }

        @Override
        public TriggeredCommandForm[] newArray(int size) {
            return new TriggeredCommandForm[size];
        }
    };
}
