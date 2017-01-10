package com.kii.thingif.command;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;

import com.google.gson.annotations.SerializedName;
import com.kii.thingif.TypedID;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a command that is executed by the thing
 */
public class Command implements Parcelable {

    private final @Nullable String commandID;
    @SerializedName("target")
    private final @Nullable TypedID targetID;
    @SerializedName("issuer")
    private final @NonNull TypedID issuerID;
    private final @NonNull List<Pair<String, Object>> actions;
    private final @Nullable List<Pair<String,List<ActionResult>>> actionResults;
    @SerializedName("commandState")
    private final @Nullable CommandState commandState;
    private final @Nullable String firedByTriggerID;
    @SerializedName("createdAt")
    private final @Nullable Long created;
    @SerializedName("modifiedAt")
    private final @Nullable Long modified;
    private final @Nullable String title;
    private final @Nullable String description;
    private final @Nullable JSONObject metadata;

    public Command(@NonNull TypedID targetID,
                   @NonNull TypedID issuerID,
                   @NonNull List<Pair<String, Object>> actions,
                   @Nullable List<Pair<String, List<ActionResult>>> actonResults,
                   @Nullable String commandID,
                   @Nullable CommandState commandState,
                   @Nullable String firedByTriggerID,
                   @Nullable Long created,
                   @Nullable Long modified,
                   @Nullable String title,
                   @Nullable String description,
                   @Nullable JSONObject metadata
                   ) {
        if (targetID == null) {
            throw new IllegalArgumentException("targetID is null");
        }
        if (issuerID == null) {
            throw new IllegalArgumentException("issuerID is null");
        }
        if (actions == null || actions.size() == 0) {
            throw new IllegalArgumentException("actions is null or empty");
        }
        this.targetID = targetID;
        this.issuerID = issuerID;
        this.actions = actions;
        this.actionResults = actonResults;
        this.commandID = commandID;
        this.commandState = commandState;
        this.firedByTriggerID = firedByTriggerID;
        this.created = created;
        this.modified = modified;
        this.title = title;
        this.metadata = metadata;
        this.description = description;
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
     * Get list of actions
     * @return action of this command.
     */
    @NonNull
    public List<Pair<String, Object>> getActions() {
        return this.actions;
    }

    /**
     * Get list of action result
     * @return action results of this command.
     */
    @Nullable
    public List<Pair<String, List<ActionResult>>> getActionResults() {
        return this.actionResults;
    }

    /**
     * Get a action result associated with specified action
     *
     * @param alias alias to find action.
     * @param action action to specify action result.
     * @return action reuslt specified with parameter's action.
     */
    @Nullable
    public List<ActionResult> getActionResult(
            @NonNull String alias,
            @NonNull Action action) {
        if (action == null) {
            throw new IllegalArgumentException("action is null");
        }
        //TODO: // FIXME: 12/14/16
//        if (this.getActionResults() != null) {
//            for (ActionResult result : this.getActionResults()) {
//                if (TextUtils.equals(action.getActionName(), result.getActionName())) {
//                    return result;
//                }
//            }
//        }
        return null;
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
    protected Command(Parcel in) throws Exception{
        this.commandID = in.readString();
        this.targetID = in.readParcelable(TypedID.class.getClassLoader());
        this.issuerID = in.readParcelable(TypedID.class.getClassLoader());
        //TODO: // FIXME: 12/16/16 fix to adapt to alias
        this.actions = new ArrayList<>();
        in.readList(this.actions, Command.class.getClassLoader());
        this.actionResults = new ArrayList<>();
        in.readList(this.actionResults, Command.class.getClassLoader());
        this.commandState = (CommandState)in.readSerializable();
        this.firedByTriggerID = in.readString();
        this.created = (Long)in.readValue(Command.class.getClassLoader());
        this.modified = (Long)in.readValue(Command.class.getClassLoader());
        this.title = in.readString();
        this.description = in.readString();
        String metadata = in.readString();
        if (!TextUtils.isEmpty(metadata)) {
            this.metadata = new JSONObject(metadata);
        }else{
            this.metadata = null;
        }
    }
    public static final Creator<Command> CREATOR = new Creator<Command>() {
        @Override
        public Command createFromParcel(Parcel in) {
            try {
                return new Command(in);
            }catch (Exception ex){
                return null;
            }
        }

        @Override
        public Command[] newArray(int size) {
            return new Command[size];
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
        //TODO // FIXME: 12/16/16 fix to adapt alias
        dest.writeList(this.actions);
        dest.writeList(this.actionResults);
        dest.writeSerializable(this.commandState);
        dest.writeString(this.firedByTriggerID);
        dest.writeValue(this.created);
        dest.writeValue(this.modified);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.metadata == null ? null : this.metadata.toString());
    }
}
