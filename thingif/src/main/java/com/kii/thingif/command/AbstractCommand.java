package com.kii.thingif.command;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.kii.thingif.TypedID;

import org.json.JSONException;
import org.json.JSONObject;

class AbstractCommand implements Parcelable{
    private @Nullable String commandID;
    @SerializedName("target")
    private @Nullable final TypedID targetID;
    @SerializedName("issuer")
    private @NonNull final TypedID issuerID;
    @SerializedName("commandState")
    private @Nullable CommandState commandState;
    private @Nullable String firedByTriggerID;
    @SerializedName("createdAt")
    private @Nullable Long created;
    @SerializedName("modifiedAt")
    private @Nullable Long modified;
    private @Nullable String title;
    private @Nullable String description;
    private @Nullable JSONObject metadata;

    AbstractCommand(
                   @NonNull TypedID targetID,
                   @NonNull TypedID issuerID) {
        if (targetID == null) {
            throw new IllegalArgumentException("targetID is null");
        }
        if (issuerID == null) {
            throw new IllegalArgumentException("issuerID is null");
        }
        this.targetID = targetID;
        this.issuerID = issuerID;
    }
    AbstractCommand(
                   @NonNull TypedID issuerID) {
        if (issuerID == null) {
            throw new IllegalArgumentException("issuerID is null");
        }
        this.targetID = null;
        this.issuerID = issuerID;
    }

    /** Get ID of the command.
     * @return ID of the command.
     */
    @Nullable
    public String getCommandID() {
        return this.commandID;
    }

    /**
     * Get ID of the target thing.
     * @return target thing ID which is issued this command.
     */
    @Nullable
    public TypedID getTargetID() {
        return this.targetID;
    }

    /**
     * Get ID of the issuer user.
     * @return issuer ID by which this command is issued.
     */
    @NonNull
    public TypedID getIssuerID() {
        return this.issuerID;
    }


    /**
     * Get status of command
     * @return status of this command.
     */
    @Nullable
    public CommandState getCommandState() {
        return this.commandState;
    }

    /**
     * Get ID of trigger which fired this command
     * @return trigger ID which fired this command.
     */
    @Nullable
    public String getFiredByTriggerID() {
        return this.firedByTriggerID;
    }

    /**
     * Get creation time
     * @return creation time of this command.
     */
    @Nullable
    public Long getCreated() {
        return this.created;
    }
    /**
     * Get modification time
     * @return modification time of this command.
     */
    @Nullable
    public Long getModified() {
        return this.modified;
    }
    /**
     * Get title.
     * @return title of this command.
     */
    @Nullable
    public String getTitle() {
        return this.title;
    }
    /**
     * Get description.
     * @return description of this command.
     */
    @Nullable
    public String getDescription() {
        return this.description;
    }
    /**
     * Get meta data
     * @return meta data of this command.
     */
    @Nullable
    public JSONObject getMetadata() {
        return this.metadata;
    }

    // Implementation of Parcelable
    AbstractCommand(Parcel in) {
        this.commandID = in.readString();
        this.targetID = in.readParcelable(TypedID.class.getClassLoader());
        this.issuerID = in.readParcelable(TypedID.class.getClassLoader());
        this.commandState = (CommandState)in.readSerializable();
        this.firedByTriggerID = in.readString();
        this.created = (Long)in.readValue(Command.class.getClassLoader());
        this.modified = (Long)in.readValue(Command.class.getClassLoader());
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
    public static final Parcelable.Creator<AbstractCommand> CREATOR = new Parcelable.Creator<AbstractCommand>() {
        @Override
        public AbstractCommand createFromParcel(Parcel in) {
            return new AbstractCommand(in);
        }

        @Override
        public AbstractCommand[] newArray(int size) {
            return new AbstractCommand[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.commandID);
        dest.writeParcelable(this.targetID, flags);
        dest.writeParcelable(this.issuerID, flags);
        dest.writeSerializable(this.commandState);
        dest.writeString(this.firedByTriggerID);
        dest.writeValue(this.created);
        dest.writeValue(this.modified);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.metadata == null ? null : this.metadata.toString());
    }
}
