package com.kii.iotcloud.command;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.kii.iotcloud.TypedID;
import com.kii.iotcloud.utils.GsonRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Command implements Parcelable {

    @SerializedName("id")
    private String commandID;
    @SerializedName("schema")
    private final String schemaName;
    private final int schemaVersion;
    @SerializedName("target")
    private final TypedID targetID;
    @SerializedName("issuer")
    private final TypedID issuerID;
    private final List<Action> actions;
    private final List<ActionResult> actionResults;
    @SerializedName("state")
    private CommandState commandState;
    private String firedByTriggerID;
    private Long created;
    private Long modified;

    public Command(@NonNull String schemaName,
                   int schemaVersion,
                   @NonNull TypedID targetID,
                   @NonNull TypedID issuerID,
                   @NonNull List<Action> actions) {
        if (TextUtils.isEmpty(schemaName)) {
            throw new IllegalArgumentException("schemaName is null or empty");
        }
        this.schemaName = schemaName;
        this.schemaVersion = schemaVersion;
        this.targetID = targetID;
        this.issuerID = issuerID;
        this.actions = actions;
        this.actionResults = new ArrayList<ActionResult>();
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
        this.actionResults.add(ar);
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
     * @return
     */
    public TypedID getTargetID() {
        return this.targetID;
    }

    /**
     * Get ID of the issuer user.
     * @return
     */
    public TypedID getIssuerID() {
        return this.issuerID;
    }

    /**
     * Get list of actions
     * @return
     */
    public List<Action> getActions() {
        return this.actions;
    }

    /**
     * Get list of action result
     * @return
     */
    public List<ActionResult> getActionResults() {
        return this.actionResults;
    }

    /**
     * Get a action result associated with specified action
     *
     * @param action
     * @return
     */
    public ActionResult getActionResult(@NonNull Action action) {
        if (action == null) {
            throw new IllegalArgumentException("action is null");
        }
        for (ActionResult result : this.actionResults) {
            if (TextUtils.equals(action.getActionName(), result.getActionName())) {
                return result;
            }
        }
        return null;
    }

    /**
     * Get status of command
     * @return
     */
    public CommandState getCommandState() {
        return this.commandState;
    }

    /**
     * Get ID of trigger which fired this command
     * @return
     */
    public String getFiredByTriggerID() {
        return this.firedByTriggerID;
    }

    /**
     * Get creation time
     * @return
     */
    public long getCreated() {
        return this.created;
    }

    /**
     * Get modification time
     * @return
     */
    public long getModified() {
        return this.modified;
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
        this.created = in.readLong();
        this.modified = in.readLong();
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
        dest.writeLong(this.created);
        dest.writeLong(this.modified);
    }
}
