package com.kii.thingif.command;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.kii.thingif.TypedID;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a command that is executed by the thing
 */
public class Command extends AbstractCommand implements Parcelable {

    private final List<Action> actions;
    private List<ActionResult> actionResults;

    public Command(@NonNull TypedID targetID,
                   @NonNull TypedID issuerID,
                   @NonNull List<Action> actions) {
        super(targetID, issuerID);
        if (actions == null || actions.size() == 0) {
            throw new IllegalArgumentException("actions is null or empty");
        }
        this.actions = actions;
    }
    public Command(
                   @NonNull TypedID issuerID,
                   @NonNull List<Action> actions) {
        super(issuerID);
        if (actions == null || actions.size() == 0) {
            throw new IllegalArgumentException("actions is null or empty");
        }
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


    // Implementation of Parcelable
    protected Command(Parcel in) {
        super(in);
        this.actions = new ArrayList<Action>();
        in.readList(this.actions, Command.class.getClassLoader());
        this.actionResults = new ArrayList<ActionResult>();
        in.readList(this.actionResults, Command.class.getClassLoader());
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
        super.writeToParcel(dest, flags);
        dest.writeList(this.actions);
        dest.writeList(this.actionResults);
    }
}
