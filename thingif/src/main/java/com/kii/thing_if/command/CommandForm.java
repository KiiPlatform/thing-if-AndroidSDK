package com.kii.thing_if.command;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Form of a command.
 *
 * This class contains data in order to create {@link Command} with {@link
 * com.kii.thing_if.ThingIFAPI#postNewCommand(CommandForm)}.
 * <br><br>
 * Mandatory data are followings:
 * <ul>
 * <li>List of alias actions</li>
 * </ul>
 * Optional data are followings:
 * <ul>
 * <li>Title of a command</li>
 * <li>Description of a command</li>
 * <li>meta data of a command</li>
 * </ul>
 */
public final class CommandForm {

    @SerializedName("actions")
    private final @NonNull List<AliasAction> aliasActions;

    private @Nullable String title;
    private @Nullable String description;
    private @Nullable JSONObject metadata;


    public static class Builder{
        private @NonNull List<AliasAction> aliasActions;
        private @Nullable String title;
        private @Nullable String description;
        private @Nullable JSONObject metadata;

        private Builder(
                @NonNull List<AliasAction> aliasActions) {
            this.aliasActions = aliasActions;
        }

        /**
         * Create {@link Builder} without Actions
         * @return {@link Builder} instance
         */
        @NonNull
        public static Builder newBuilder() {
            List<AliasAction> actions = new ArrayList<>();
            return new Builder(actions);
        }

        /**
         * Create {@link Builder} with Actions
         * @param aliasActions list of {@link AliasAction} instances.
         * @return {@link Builder} instance.
         * @throws IllegalArgumentException Thrown when aliasActions is null or empty.
         */
        @NonNull
        public static Builder newBuilder(
                @NonNull List<AliasAction> aliasActions) {
            if (aliasActions == null || aliasActions.size() == 0) {
                throw new IllegalArgumentException("aliasActions is null or empty");
            }
            return new Builder(aliasActions);
        }

        /**
         * Add instance of {@link AliasAction} to action list.
         * @param aliasAction Instance of AliasAction
         * @return {@link Builder} instance
         * @throws IllegalArgumentException Thrown when aliasAction is null.
         */
        @NonNull
        public Builder addAliasAction(
                @NonNull AliasAction aliasAction) {
            if (aliasAction == null) {
                throw new IllegalArgumentException("aliasAction is null");
            }
            this.aliasActions.add(aliasAction);
            return this;
        }

        /**
         * Setter of title
         *
         * @param title Length of title must be equal or less than 50 characters.
         * @return {@link Builder} instance
         * @throws IllegalArgumentException if title is invalid.
         */
        @NonNull
        public Builder setTitle(@Nullable String title)
                throws IllegalArgumentException {
            if (title != null && title.length() > 50) {
                throw new IllegalArgumentException("title is more than 50 characters.");
            }
            this.title = title;
            return this;
        }

        /**
         * Setter of description
         *
         * @param description Length of description must be equal or less than
         * 200 characters.
         * @return {@link Builder} instance.
         * @throws IllegalArgumentException if description is invalid.
         */
        @NonNull
        public Builder setDescription(@Nullable String description)
                throws IllegalArgumentException{
            if (description != null && description.length() > 200) {
                throw new IllegalArgumentException("description is more than 200 characters.");
            }
            this.description = description;
            return this;
        }

        /**
         * Setter of meta data.
         *
         * @param metadata meta data of this command.
         * @return {@link Builder} instance.
         */
        @NonNull
        public Builder setMetadata(@Nullable JSONObject metadata) {
            this.metadata = metadata;
            return this;
        }

        /**
         * Build {@link CommandForm} instance
         * @return {@link CommandForm} instance
         * @throws IllegalStateException If actions is empty.
         */
        @NonNull
        public CommandForm build()
                throws IllegalStateException{
            if(this.aliasActions.size() == 0) {
                throw new IllegalStateException("aliasActions is empty");
            }
            return new CommandForm(
                    this.aliasActions,
                    this.title,
                    this.description,
                    this.metadata);
        }
    }

    private CommandForm(
            @NonNull List<AliasAction> aliasActions,
            @Nullable String title,
            @Nullable String description,
            @Nullable JSONObject metaData)
            throws IllegalArgumentException
    {
        this.aliasActions = aliasActions;
        this.title = title;
        this.description = description;
        this.metadata = metaData;
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