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
    @SerializedName("target")
    private final TypedID targetID;
    @SerializedName("issuer")
    private final TypedID issuerID;
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
    private final List actions;
    private List actionResults;

    /**
     * Initialize Command
     * @param targetID TypedID of target thing.
     * @param issuerID TypedID of issuer.
     * @param actions List of actions. Class of action item only accept {@link Action}
     *                or {@link TraitActions}
     * @throws IllegalArgumentException Thrown when targetID, issuerID is null.
     *   Or actions is null or empty. Or if actions is not empty, but the action element
     *   is not Action or TraitActions.
     */
    public Command(@NonNull TypedID targetID,
                   @NonNull TypedID issuerID,
                   @NonNull List<Action> actions) {
        if (targetID == null) {
            throw new IllegalArgumentException("targetID is null");
        }
        if (issuerID == null) {
            throw new IllegalArgumentException("issuerID is null");
        }
        if (actions == null || actions.size() == 0) {
            throw new IllegalArgumentException("actions is null or empty");
        }
        //TODO: check class of element of action
        this.targetID = targetID;
        this.issuerID = issuerID;
        this.actions = actions;
    }

    /**
     * Initialize Command
     * @param issuerID TypedID of issuer.
     * @param actions List of actions. Class of action item only accept {@link Action}
     *                or {@link TraitActions}
     * @throws IllegalArgumentException Thrown when targetID, issuerID is null.
     *   Or actions is null or empty. Or if actions is not empty, but the action element
     *   is not Action or TraitActions.
     */
    public Command(@NonNull TypedID issuerID,
                   @NonNull List actions) {
        if (issuerID == null) {
            throw new IllegalArgumentException("issuerID is null");
        }
        if (actions == null || actions.size() == 0) {
            throw new IllegalArgumentException("actions is null or empty");
        }
        this.targetID = null;
        this.issuerID = issuerID;
        this.actions = actions;
    }

    public void addActionResult(@NonNull String alias, @NonNull ActionResult ar) {
        // TODO: implement me
    }

    public void addActionResults(@NonNull String alias, @NonNull List<ActionResult> ars) {
        // TODO: implement me
    }

    public void addActionResult(@NonNull ActionResult ar) throws Exception{
        if (ar == null) {
            throw new IllegalArgumentException("ActionResult is null");
        }
        boolean hasAction = false;
        //TODO: FIXME
        // first check whether actions is trait format, if yes throw exception
        // else check whether action name of action result in actions array
//        for (Action action : this.actions) {
//            if (TextUtils.equals(ar.getActionName(), action.getActionName())) {
//                hasAction = true;
//            }
//        }
//        if (!hasAction) {
//            throw new IllegalArgumentException(ar.getActionName() + " is not contained in this Command");
//        }
//        if (this.actionResults == null) {
//            this.actionResults = new ArrayList<ActionResult>();
//        }
//        this.actionResults.add(ar);
    }

    /** Get ID of the command.
     * @return ID of the command.
     */
    public String getCommandID() {
        return this.commandID;
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
     * Get list of Action instances
     * @return action of this command.
     */
    public List<Action> getActions() {
        List<Action> allActions = new ArrayList<>();
        for(int i=0; i<this.actions.size(); i++) {
            if(this.actions.get(i) instanceof Action){
                Action action = (Action)this.actions.get(i);
                allActions.add(action);
            }else if(this.actions.get(i) instanceof TraitActions){
                throw new IllegalStateException("Actions of this command support trait.");
            }
        }
        return allActions;
    }

    /**
     * Get list of Action instances
     * @return action of this command.
     */
    public List<Action> getAllActions() {
        List<Action> allActions = new ArrayList<>();
        for(int i=0; i<this.actions.size(); i++) {
            if(this.actions.get(i) instanceof TraitActions){
                TraitActions traitActions = (TraitActions) this.actions.get(i);
                allActions.addAll(traitActions.getActions());
            }else if(this.actions.get(i) instanceof Action){
                Action action = (Action)this.actions.get(i);
                allActions.add(action);
            }
        }
        return allActions;
    }

    public List<String> getAlias() {
        List<String> allAlias = new ArrayList<>();
        for(int i=0; i<this.actions.size(); i++) {
            if(this.actions.get(i) instanceof TraitActions){
                TraitActions traitActions = (TraitActions) this.actions.get(i);
                allAlias.add(traitActions.getAlias());
            }
        }
        return allAlias;
    }

    /**
     * Get actions with specified trait alias name.
     * @param alias Name of trait alias.
     * @return List of Action instance of the specified trait alias.
     */
    public List<Action> getActions(String alias) {
        //TODO: implement me
        return new ArrayList<>();
    }

    /**
     * Get list of action result
     * @return action results of this command.
     */
    public List<ActionResult> getActionResults() {
        List<ActionResult> allResults = new ArrayList<>();
        for(int i= 0; i<this.actionResults.size(); i++) {
            if(this.actionResults.get(i) instanceof TraitActionResults){
                TraitActionResults results = (TraitActionResults)this.actionResults.get(i);
                allResults.addAll(results.getActionResults());
            }else if(this.actionResults.get(i) instanceof ActionResult) {
                allResults.add((ActionResult)this.actionResults.get(i));
            }
        }
        return allResults;
    }

    public List<ActionResult> getActionResults(@NonNull String alias) {
        //TODO: implement me
        return new ArrayList<>();
    }

    public ActionResult getActionResult(@NonNull String alias, @NonNull Action action) {
        //TODO: implement me
        return null;
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
        //TODO: fix me
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
    protected Command(Parcel in) {
        this.commandID = in.readString();
        this.targetID = in.readParcelable(TypedID.class.getClassLoader());
        this.issuerID = in.readParcelable(TypedID.class.getClassLoader());
        this.actions = new ArrayList();
        in.readList(this.actions, null);
        this.actionResults = new ArrayList<>();
        in.readList(this.actionResults, null);
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
