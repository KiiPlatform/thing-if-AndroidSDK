package com.kii.thing_if.command;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.kii.thing_if.TypedID;

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
    @SerializedName("actions")
    private final @NonNull List<AliasAction> aliasActions;
    @SerializedName("actionResults")
    private final @Nullable List<AliasActionResult> aliasActionResults;
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

    Command(
            @NonNull TypedID issuerID,
            @NonNull List<AliasAction> aliasActions,
            @Nullable String commandID,
            @Nullable TypedID targetID,
            @Nullable List<AliasActionResult> aliasActionResults,
            @Nullable CommandState commandState,
            @Nullable String firedByTriggerID,
            @Nullable Long created,
            @Nullable Long modified,
            @Nullable String title,
            @Nullable String description,
            @Nullable JSONObject metadata) {
        this.issuerID = issuerID;
        this.aliasActions = aliasActions;
        this.commandID = commandID;
        this.targetID = targetID;
        this.aliasActionResults = aliasActionResults;
        this.commandState = commandState;
        this.firedByTriggerID = firedByTriggerID;
        this.created = created;
        this.modified = modified;
        this.title = title;
        this.description = description;
        this.metadata = metadata;
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
    public List<AliasAction> getAliasActions() {
        return this.aliasActions;
    }

    /**
     * Retrieve specified AliasAction
     * @param alias alias to retrieve
     * @return list of AliasAction with the specified type.
     */
    @NonNull
    public List<AliasAction> getAliasActions(
            @NonNull String alias) {
        List<AliasAction> foundActions = new ArrayList<>();
        for (AliasAction aliasAction: this.aliasActions) {
            if (aliasAction.getAlias().equals(alias)){
                foundActions.add( aliasAction);
            }
        }
        return foundActions;
    }

    /**
     * Get list of action result
     * @return action results of this command.
     */
    @Nullable
    public List<AliasActionResult> getAliasActionResults() {
        return this.aliasActionResults;
    }

    /**
     * Get a action result associated with specified alias and action name
     *
     * @param alias alias to find action.
     * @param actionName name of action to specify action result.
     * @return list of {@link ActionResult}.
     */
    @NonNull
    public List<ActionResult> getActionResult(
            @NonNull String alias,
            @NonNull String actionName) {
        List<ActionResult> foundResults = new ArrayList<>();
        if (this.aliasActionResults != null) {
            for (AliasActionResult aliasResult : this.aliasActionResults) {
                if (aliasResult.getAlias().equals(alias)) {
                    for (ActionResult result : aliasResult.getResults()) {
                        if (result.getActionName().equals(actionName)) {
                            foundResults.add(result);
                        }
                    }
                }
            }
        }
        return foundResults;
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
    public Command(Parcel in) throws Exception{
        this.commandID = in.readString();
        this.targetID = in.readParcelable(TypedID.class.getClassLoader());
        this.issuerID = in.readParcelable(TypedID.class.getClassLoader());
        this.aliasActions = new ArrayList<>();
        in.readList(this.aliasActions, AliasAction.class.getClassLoader());
        List<AliasActionResult> tempResults = new ArrayList<>();
        in.readList(tempResults, null);
        if (tempResults.size() == 0) {
            this.aliasActionResults = null;
        }else{
            this.aliasActionResults = tempResults;
        }

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

        if (this.targetID == null) {
            throw new IllegalArgumentException("targetID is null");
        }
        if (this.issuerID == null) {
            throw new IllegalArgumentException("issuerID is null");
        }
        if (this.aliasActions == null || this.aliasActions.size() == 0) {
            throw new IllegalArgumentException("actions is null or empty");
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
        dest.writeList(this.aliasActions);
        dest.writeList(this.aliasActionResults);
        dest.writeSerializable(this.commandState);
        dest.writeString(this.firedByTriggerID);
        dest.writeValue(this.created);
        dest.writeValue(this.modified);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.metadata == null ? null : this.metadata.toString());
    }
}
