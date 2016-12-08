package com.kii.thingif.command;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Form of a trait formatted command.
 *
 * This class contains data in order to create {@link TraitCommand} with {@link
 * com.kii.thingif.TraitThingIFAPI#postNewCommand(TraitCommandForm)}.
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
public final class TraitCommandForm implements Parcelable {

    private final @NonNull
    Map<String, List<Action>> actions;

    private @Nullable String title;
    private @Nullable String description;
    private @Nullable JSONObject metadata;

    /**
     * Constructs a CommandForm instance.
     *
     * @param actions List of actions. Must not be null or empty.
     * @throws IllegalArgumentException when schemaName is null or empty
     * string and/or actions is null or empty.
     */
    public TraitCommandForm(
            @NonNull Map<String, List<Action>> actions)
            throws IllegalArgumentException
    {
        if (actions == null || actions.size() == 0) {
            throw new IllegalArgumentException("actions is null or empty.");
        }
        this.actions = actions;
    }

    /**
     * Setter of title
     *
     * @param title Length of title must be equal or less than 50 characters.
     * @return this instance
     * @throws IllegalArgumentException if title is invalid.
     */
    public TraitCommandForm setTitle(
            @Nullable String title)
            throws IllegalArgumentException
    {
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
     * @return this instance.
     * @throws IllegalArgumentException if description is invalid.
     */
    public TraitCommandForm setDescription(@Nullable String description) {
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
     * @return this instance.
     */
    public TraitCommandForm setMetadata(@Nullable JSONObject metadata) {
        this.metadata = metadata;
        return this;
    }


    /**
     * Getter of actions.
     *
     * @return actions
     */
    @NonNull
    public Map<String, List<Action>> getActions() {
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
        dest.writeInt(this.actions.size());
        for(Map.Entry<String, List<Action>> e : this.actions.entrySet()) {
            dest.writeString(e.getKey());
            dest.writeList(e.getValue());
        }
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.metadata == null ? null : this.metadata.toString());
    }

    public static final Parcelable.Creator<TraitCommandForm> CREATOR
            = new Parcelable.Creator<TraitCommandForm>() {
        public TraitCommandForm createFromParcel(Parcel in) {
            return new TraitCommandForm(in);
        }

        public TraitCommandForm[] newArray(int size) {
            return new TraitCommandForm[size];
        }
    };

    private TraitCommandForm(Parcel in) {
        int size = in.readInt();
        this.actions = new HashMap<>(size);
        for(int i=0; i < size; i++) {
            String alias = in.readString();
            List<Action> actions = new ArrayList<>();
            in.readList(actions, null);
        }
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
