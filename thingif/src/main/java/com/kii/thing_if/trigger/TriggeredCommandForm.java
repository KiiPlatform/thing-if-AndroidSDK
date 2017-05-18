package com.kii.thing_if.trigger;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.kii.thing_if.TypedID;
import com.kii.thing_if.command.AliasAction;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Form of a command in trigger request.
 *
 * This class contains data in order to create {@link
 * com.kii.thing_if.command.Command} instance got from {@link
 * Trigger#getCommand()}.
 * <br><br>
 * Mandatory data are followings:
 * <ul>
 * <li>List of alias actions</li>
 * </ul>
 * Optional data are followings:
 * <ul>
 * <li>Target thing ID</li>
 * <li>Title of a command</li>
 * <li>Description of a command</li>
 * <li>meta data of a command</li>
 * </ul>
 */
public class TriggeredCommandForm {

    /**
     * TriggeredCommandForm builder.
     */
    public static class Builder {

        @NonNull private List<AliasAction> aliasActions;
        @Nullable private TypedID targetID;
        @Nullable private String title;
        @Nullable private String description;
        @Nullable private JSONObject metadata;

        private Builder(
                @NonNull List<AliasAction> aliasActions)
        {
            this.aliasActions = aliasActions;
        }

        /**
         * Constructs a {@link TriggeredCommandForm.Builder} instance.
         *
         * @param aliasActions List of alias actions. Must not be null or empty.
         * @throws IllegalArgumentException This exception is thrown if one or
         * more following conditions are met.
         * <ul>
         *   <li>actions is null or empty</li>
         * </ul>
         * @return builder instance.
         */
        @NonNull
        public static Builder newBuilder(
                @NonNull List<AliasAction> aliasActions)
        {
            return new Builder(aliasActions);
        }

        /**
         * Constructs a {@link TriggeredCommandForm.Builder} instance with empty actions Array
         * @return builder instance.
         */
        public static Builder newBuilder() {
            return new Builder(new ArrayList<AliasAction>());
        }

//        /**
//         * Constructs a {@link TriggeredCommandForm.Builder} instance.
//         *
//         * <p>
//         * This constructor copies following {@link Command} fields:
//         * </p>
//         *
//         * <ul>
//         *   <li>{@link Command#getActions()}</li>
//         *   <li>{@link Command#getTargetID()}</li>
//         *   <li>{@link Command#getTitle()}</li>
//         *   <li>{@link Command#getDescription()}</li>
//         *   <li>{@link Command#getMetadata()}</li>
//         * </ul>
//         *
//         * @param command Souce of this {@link TriggeredCommandForm.Builder}
//         * instance.
//         * @return builder instance.
//         * @throws IllegalArgumentException if command is null.
//         */
//        @NonNull
//        public static <T3 extends Alias> Builder<T3> newBuilderFromCommand(
//                @NonNull Command<T3> command)
//            throws IllegalArgumentException
//        {
//            return (new Builder<>(command.getActions())).
//                    setTargetID(command.getTargetID()).
//                    setTitle(command.getTitle()).
//                    setDescription(command.getDescription()).
//                    setMetadata(command.getMetadata());
//        }

        /**
         * Add alias action to aliasActions array.
         * @param aliasAction AliasAction instance.
         * @return this instance for method chaining.
         */
        @NonNull
        public Builder addAliasAction(
                @NonNull AliasAction aliasAction)
            throws IllegalArgumentException
        {
            this.aliasActions.add(aliasAction);
            return this;
        }


        /**
         * Getter of aliasActions.
         *
         * @return aliasActions
         */
        @NonNull
        public List<AliasAction> getAliasActions() {
            return this.aliasActions;
        }

        /**
         * Setter of target thing ID.
         *
         * <p>
         * {@link
         * com.kii.thing_if.ThingIFAPI#postNewTrigger(TriggeredCommandForm,
         * Predicate, TriggerOptions)},  {@link
         * com.kii.thing_if.ThingIFAPI#patchCommandTrigger(String, TriggeredCommandForm, Predicate, TriggerOptions)}
         * and {@link com.kii.thing_if.ThingIFAPI#patchServerCodeTrigger(String, ServerCode, Predicate)} use {@link
         * TriggeredCommandForm#getTargetID()} to specify target of command
         * in trigger. If you do not set target thing ID with this method,
         * Default target is used. The default target is {@link
         * com.kii.thing_if.ThingIFAPI#getTarget()}.
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
         * Build {@link TriggeredCommandForm} instance.
         *
         * @return {@link TriggeredCommandForm} instance.
         */
        @NonNull
        public TriggeredCommandForm build() {
            if (isEmpty(this.aliasActions)) {
                throw new IllegalArgumentException("aliasActions is empty.");
            }

            TriggeredCommandForm retval =
                    new TriggeredCommandForm(this.aliasActions);
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

    @SerializedName("actions")
    @NonNull private final List<AliasAction> aliasActions;
    @SerializedName("target")
    @Nullable private TypedID targetID;
    @Nullable private String title;
    @Nullable private String description;
    @Nullable private JSONObject metadata;

    private TriggeredCommandForm(
            @NonNull List<AliasAction> aliasActions)
    {
        this.aliasActions = aliasActions;
    }

    /**
     * Getter of aliasActions.
     *
     * @return aliasActions
     */
    @NonNull
    public List<AliasAction> getAliasActions() {
        return this.aliasActions;
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
}
