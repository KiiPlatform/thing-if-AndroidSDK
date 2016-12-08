package com.kii.thingif.command;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kii.thingif.TypedID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a trait formatted command that is executed by the thing
 */
public class TraitCommand extends AbstractCommand implements Parcelable {

    private @NonNull final Map<String, List<Action>> actions;
    private @NonNull Map<String, List<ActionResult>> actionResults;

    public TraitCommand(
                   @NonNull TypedID targetID,
                   @NonNull TypedID issuerID,
                   @NonNull Map<String, List<Action>> actions) {
        super(targetID, issuerID);
        if (actions == null || actions.size() == 0) {
            throw new IllegalArgumentException("actions is null or empty");
        }
        this.actions = actions;
        this.actionResults = new HashMap<>();
    }
    public TraitCommand(
                   @NonNull TypedID issuerID,
                   @NonNull Map<String, List<Action>> actions) {
        super(issuerID);
        if (actions == null || actions.size() == 0) {
            throw new IllegalArgumentException("actions is null or empty");
        }
        this.actions = actions;
        this.actionResults = new HashMap<>();
    }

    /**
     * Add list of ActionResult instance by alias
     * @param alias Alias name.
     * @param results List of {@link ActionResult} instances.
     */
    public void addActionResults(
            @NonNull String alias,
            @NonNull List<ActionResult> results) {
        this.actionResults.put(alias, results);
    }

    /**
     * Get list of actions
     * @return action of this command.
     */
    @NonNull
    public Map<String, List<Action>> getActions() {
        return this.actions;
    }

    /**
     * Get list of action result
     * @return action results of this command.
     */
    @NonNull
    public Map<String, List<ActionResult>> getActionResults() {
        return this.actionResults;
    }

    /**
     * Get a action result associated with specified action
     *
     * @param alias name of alias to retrieve action result.
     * @param action action to specify action result.
     * @return action reuslt specified with parameter's action.
     */
    @Nullable
    public ActionResult getActionResult(
            @NonNull String alias,
            @NonNull Action action) {
        //TODO: implement me
        return null;
    }


    // Implementation of Parcelable
    private TraitCommand(Parcel in) {
        super(in);
        int size = in.readInt();
        this.actions = new HashMap<>(size);
        for(int i= 0; i < size; i++) {
            String alias = in.readString();
            List<Action> actions = new ArrayList<>();
            in.readList(actions, null);
            this.actions.put(alias, actions);
        }
        int size1 = in.readInt();
        this.actionResults = new HashMap<>(size1);
        for(int j=0; j < size1; j++) {
            String alias = in.readString();
            List<ActionResult> actionResults = new ArrayList<>();
            in.readList(actionResults, null);
            this.actionResults.put(alias, actionResults);
        }
    }
    public static final Creator<TraitCommand> CREATOR = new Creator<TraitCommand>() {
        @Override
        public TraitCommand createFromParcel(Parcel in) {
            return new TraitCommand(in);
        }

        @Override
        public TraitCommand[] newArray(int size) {
            return new TraitCommand[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.actions.size());
        for(Map.Entry<String, List<Action>> e : this.actions.entrySet()){
            dest.writeString(e.getKey());
            dest.writeList(e.getValue());
        }
        dest.writeInt(this.actionResults.size());
        for(Map.Entry<String, List<ActionResult>> e : this.actionResults.entrySet()) {
            dest.writeString(e.getKey());
            dest.writeList(e.getValue());
        }
    }
}
