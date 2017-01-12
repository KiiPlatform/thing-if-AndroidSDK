package com.kii.thingif.command;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Form of a command.
 *
 * This class contains data in order to create {@link Command} with {@link
 * com.kii.thingif.ThingIFAPI#postNewCommand(CommandForm)}.
 * <br><br>
 * Mandatory data are followings:
 * <ul>
 * <li>List of actions</li>
 * </ul>
 * Optional data are followings:
 * <ul>
 * <li>Title of a command</li>
 * <li>Description of a command</li>
 * <li>meta data of a command</li>
 * </ul>
 */
public final class CommandForm implements Parcelable {

    private final @NonNull List<AliasAction<? extends Action>> actions;

    private @Nullable String title;
    private @Nullable String description;
    private @Nullable JSONObject metadata;


    public static class Builder{
        private @NonNull List<AliasAction<? extends Action>> actions;
        private @Nullable String title;
        private @Nullable String description;
        private @Nullable JSONObject metadata;

        private Builder(
                @NonNull List<AliasAction<? extends Action>> actions) {
            this.actions = actions;
        }

        /**
         * Create {@link Builder} without Actions
         * @return {@link Builder} instance
         */
        @NonNull
        public static Builder newBuilder() {
            List<AliasAction<? extends Action>> actions = new ArrayList<>();
            return new Builder(actions);
        }

        /**
         * Create {@link Builder} with Actions
         * @param actions list of {@link AliasAction} instances.
         * @return {@link Builder} instance.
         */
        @NonNull
        public static Builder newBuilder(
                @NonNull List<AliasAction<? extends Action>> actions) {
            return new Builder(actions);
        }

        /**
         * Add instance of {@link AliasAction} to action list.
         * @param action Intance of AliasAction
         * @return {@link Builder} instance
         */
        @NonNull
        public Builder addAction(
                @NonNull AliasAction<? extends Action> action) {
            this.actions.add(action);
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
                throw new IllegalArgumentException("title is more than 50 charactors.");
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
                throw new IllegalArgumentException("description is more than 200 charactors.");
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
            if(this.actions.size() == 0) {
                throw new IllegalStateException("Actions is empty");
            }
            return new CommandForm(
                    this.actions,
                    this.title,
                    this.description,
                    this.metadata);
        }
    }

    private CommandForm(
            @NonNull List<AliasAction<? extends Action>> actions,
            @Nullable String title,
            @Nullable String description,
            @Nullable Object metaData)
            throws IllegalArgumentException
    {
        this.actions = actions;
    }

    /**
     * Getter of actions.
     *
     * @return actions
     */
    @NonNull
    public List<AliasAction<? extends Action>> getActions() {
        return this.actions;
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
        dest.writeList(this.actions);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.metadata == null ? null : this.metadata.toString());
    }

    public static final Parcelable.Creator<CommandForm> CREATOR
            = new Parcelable.Creator<CommandForm>() {
        public CommandForm createFromParcel(Parcel in) {
            return new CommandForm(in);
        }

        public CommandForm[] newArray(int size) {
            return new CommandForm[size];
        }
    };

    private CommandForm(Parcel in) {
        this.actions = new ArrayList<>();
        in.readList(this.actions, null);
        this.title = in.readString();
        this.description = in.readString();
        String metadata = in.readString();
        if (!TextUtils.isEmpty(metadata)) {
            try {
                this.metadata = new JSONObject(metadata);
            } catch (JSONException ignore) {
                // Won’t happen
            }
        }
    }
}