package com.kii.thingif.command;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
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
    private final List<Action> actions;
    private List<ActionResult> actionResults;
    @SerializedName("commandState")
    private CommandState commandState;
    private String firedByTriggerID;
    @SerializedName("createdAt")
    private Long created;
    @SerializedName("modifiedAt")
    private Long modified;
    private String title;
    private String description;
    private JSONObject metadata;

    public Command(@NonNull String schemaName,
                   int schemaVersion,
                   @NonNull TypedID targetID,
                   @NonNull TypedID issuerID,
                   @NonNull List<Action> actions) {
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
    }
    public Command(@NonNull String schemaName,
                   int schemaVersion,
                   @NonNull TypedID issuerID,
                   @NonNull List<Action> actions) {
        if (TextUtils.isEmpty(schemaName)) {
            throw new IllegalArgumentException("schemaName is null or empty");
        }
        if (issuerID == null) {
            throw new IllegalArgumentException("issuerID is null");
        }
        if (actions == null || actions.size() == 0) {
            throw new IllegalArgumentException("actions is null or empty");
        }
        this.schemaName = schemaName;
        this.schemaVersion = schemaVersion;
        this.targetID = null;
        this.issuerID = issuerID;
        this.actions = actions;
    }
    public void addActionResult(@NonNull ActionResult ar) {
        if (ar == null) {
            throw new IllegalArgumentException("ActionResult is null");
        }
        boolean hasAction = false;
        for (Action action : this.actions) {
            if (TextUtils.equals(ar.getActionName(), action.getActionName())) {
                hasAction = true;
            }
        }
        if (!hasAction) {
            throw new IllegalArgumentException(ar.getActionName() + " is not contained in this Command");
        }
        if (this.actionResults == null) {
            this.actionResults = new ArrayList<ActionResult>();
        }
        this.actionResults.add(ar);
    }

    /** Get ID of the command.
     * @return ID of the command.
     */
    public String getCommandID() {
        return this.commandID;
    }
    void setCommandID(String commandID) {
        this.commandID = commandID;
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
    public List<Action> getActions() {
        return this.actions;
    }

    /**
     * Get list of action result
     * @return action results of this command.
     */
    public List<ActionResult> getActionResults() {
        return this.actionResults;
    }

    /**
     * Get a action result associated with specified action
     *
     * @param action action to specify action result.
     * @return action reuslt specified with parameter's action.
     */
    public ActionResult getActionResult(@NonNull Action action) {
        if (action == null) {
            throw new IllegalArgumentException("action is null");
        }
        if (this.getActionResults() != null) {
            for (ActionResult result : this.getActionResults()) {
                if (TextUtils.equals(action.getActionName(), result.getActionName())) {
                    return result;
                }
            }
        }
        return null;
    }

    /**
     * Get status of command
     * @return status of this command.
     */
    public CommandState getCommandState() {
        return this.commandState;
    }
    void setCommandState(CommandState commandState) {
        this.commandState = commandState;
    }

    /**
     * Get ID of trigger which fired this command
     * @return trigger ID which fired this command.
     */
    public String getFiredByTriggerID() {
        return this.firedByTriggerID;
    }
    void setFiredByTriggerID(String firedByTriggerID) {
        this.firedByTriggerID = firedByTriggerID;
    }

    /**
     * Get creation time
     * @return creation time of this command.
     */
    public Long getCreated() {
        return this.created;
    }
    void setCreated(Long created) {
        this.created = created;
    }
    /**
     * Get modification time
     * @return modification time of this command.
     */
    public Long getModified() {
        return this.modified;
    }
    void setModified(Long modified) {
        this.modified = modified;
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
    protected Command(Parcel in) {
        this.commandID = in.readString();
        this.schemaName = in.readString();
        this.schemaVersion = in.readInt();
        this.targetID = in.readParcelable(TypedID.class.getClassLoader());
        this.issuerID = in.readParcelable(TypedID.class.getClassLoader());
        this.actions = new ArrayList<Action>();
        in.readList(this.actions, Command.class.getClassLoader());
        this.actionResults = new ArrayList<ActionResult>();
        in.readList(this.actionResults, Command.class.getClassLoader());
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
    public static final Creator<Command> CREATOR = new Creator<Command>() {
        @Override
        public Command createFromParcel(Parcel in) {
            return new Command(in);
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
