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
    private TypedID targetID;
    @SerializedName("issuer")
    private TypedID issuerID;
    private List<Action> actions;
    private List<ActionResult> actionResults;
    @SerializedName("state")
    private CommandState commandState;
    private String firedByTriggerID;
    private Long created;
    private Long modified;

    public Command(String schemaName, int schemaVersion, TypedID targetID, TypedID issuerID) {
        if (TextUtils.isEmpty(schemaName)) {
            throw new IllegalArgumentException("schemaName is null or empty");
        }
        this.schemaName = schemaName;
        this.schemaVersion = schemaVersion;
        this.targetID = targetID;
        this.issuerID = issuerID;




        this.actions = new ArrayList<Action>();
        this.actionResults = new ArrayList<ActionResult>();
    }
    public void addAction(Action a) {
        this.actions.add(a);
    }
    public void addActionResult(ActionResult ar) {
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
        return targetID;
    }

    /**
     * Get ID of the issuer user.
     * @return
     */
    public TypedID getIssuerID() {
        return issuerID;
    }

    /**
     * Get list of actions
     * @return
     */
    public List<Action> getActions() {
        return actions;
    }

    /**
     * Get list of action result
     * @return
     */
    public List<ActionResult> getActionResults() {
        return actionResults;
    }

    /**
     * Get status of command
     * @return
     */
    public CommandState getCommandState() {
        return commandState;
    }

    /**
     * Get ID of trigger which fired this command
     * @return
     */
    public String getFiredByTriggerID() {
        return firedByTriggerID;
    }

    /**
     * Get creation time
     * @return
     */
    public long getCreated() {
        return created;
    }

    /**
     * Get modification time
     * @return
     */
    public long getModified() {
        return modified;
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
