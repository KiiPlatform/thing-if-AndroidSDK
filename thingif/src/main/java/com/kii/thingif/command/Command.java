package com.kii.thingif.command;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;

import com.google.gson.annotations.SerializedName;
import com.kii.thingif.Alias;
import com.kii.thingif.TypedID;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a command that is executed by the thing
 */
public class Command implements Parcelable {

    private String commandID;
    @SerializedName("schema")
    private final String schemaName;
    private final int schemaVersion;
    @SerializedName("target")
    private final TypedID targetID;
    @SerializedName("issuer")
    private final TypedID issuerID;
    private final List<Pair<Alias, List<Action>>> actions;
    private final List<Pair<Alias,List<ActionResult>>> actionResults;
    @SerializedName("commandState")
    private final CommandState commandState;
    private final String firedByTriggerID;
    @SerializedName("createdAt")
    private final Long created;
    @SerializedName("modifiedAt")
    private final Long modified;
    private final String title;
    private final String description;
    private final JSONObject metadata;

    public Command(@NonNull String schemaName,
                   int schemaVersion,
                   @Nullable TypedID targetID,
                   @NonNull TypedID issuerID,
                   @NonNull List<Pair<Alias, List<Action>>> actions,
                   @Nullable List<Pair<Alias,List<ActionResult>>> actonResults,
                   @Nullable CommandState commandState,
                   @Nullable String firedByTriggerID,
                   @Nullable Long created,
                   @Nullable Long modified,
                   @Nullable String title,
                   @Nullable String description,
                   @Nullable JSONObject metadata
                   ) {
        if (TextUtils.isEmpty(schemaName)) {
            throw new IllegalArgumentException("schemaName is null or empty");
        }
        if (targetID == null) {
            throw new IllegalArgumentException("targetID is null");
        }
        if (issuerID == null) {
            throw new IllegalArgumentException("issuerID is null");
        }
        if (actions == null || actions.size() == 0) {
            throw new IllegalArgumentException("actions is null or empty");
        }
        this.schemaName = schemaName;
        this.schemaVersion = schemaVersion;
        this.targetID = targetID;
        this.issuerID = issuerID;
        this.actions = actions;
        this.actionResults = actonResults;
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
    public String getCommandID() {
        return this.commandID;
    }

    /** Get name of the schema in which command is defined.
     * @return name of the schema.
     */
    public String getSchemaName() {
        return this.schemaName;
    }

    /** Get version of the schema in which command is defined.
     * @return version of the schema.
     */
    public int getSchemaVersion() {
        return this.schemaVersion;
    }

    /**
     * Get ID of the target thing.
     * @return target thing ID which is issued this command.
     */
    public TypedID getTargetID() {
        return this.targetID;
    }

    /**
     * Get ID of the issuer user.
     * @return issuer ID by which this command is issued.
     */
    public TypedID getIssuerID() {
        return this.issuerID;
    }

    /**
     * Get list of actions
     * @return action of this command.
     */
    public List<Pair<Alias, List<Action>>> getActions() {
        return this.actions;
    }

    /**
     * Get list of action result
     * @return action results of this command.
     */
    public List<Pair<Alias,List<ActionResult>>> getActionResults() {
        return this.actionResults;
    }

    /**
     * Get a action result associated with specified action
     *
     * @param alias alias to find action.
     * @param action action to specify action result.
     * @return action reuslt specified with parameter's action.
     */
    public List<ActionResult> getActionResult(
            @NonNull Alias alias,
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
    public CommandState getCommandState() {
        return this.commandState;
    }

    /**
     * Get ID of trigger which fired this command
     * @return trigger ID which fired this command.
     */
    public String getFiredByTriggerID() {
        return this.firedByTriggerID;
    }

    /**
     * Get creation time
     * @return creation time of this command.
     */
    public Long getCreated() {
        return this.created;
    }
    /**
     * Get modification time
     * @return modification time of this command.
     */
    public Long getModified() {
        return this.modified;
    }
    /**
     * Get title.
     * @return title of this command.
     */
    public String getTitle() {
        return this.title;
    }
    /**
     * Get description.
     * @return description of this command.
     */
    public String getDescription() {
        return this.description;
    }
    /**
     * Get meta data
     * @return meta data of this command.
     */
    public JSONObject getMetadata() {
        return this.metadata;
    }

    // Implementation of Parcelable
    protected Command(Parcel in) throws Exception{
        this.commandID = in.readString();
        this.schemaName = in.readString();
        this.schemaVersion = in.readInt();
        this.targetID = in.readParcelable(TypedID.class.getClassLoader());
        this.issuerID = in.readParcelable(TypedID.class.getClassLoader());
        this.actions = new ArrayList<Pair<Alias, List<Action>>>();
        in.readList(this.actions, Command.class.getClassLoader());
        this.actionResults = new ArrayList<Pair<Alias, List<ActionResult>>>();
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
        dest.writeString(this.schemaName);
        dest.writeInt(this.schemaVersion);
        dest.writeParcelable(this.targetID, flags);
        dest.writeParcelable(this.issuerID, flags);
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
