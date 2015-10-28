package com.kii.thingif.trigger;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.kii.thingif.TypedID;
import com.kii.thingif.command.Command;

/**
 * Represents a trigger that is fired when status of thing changed or it became at the designated time.
 */
public class Trigger implements Parcelable {

    private String triggerID;
    private Predicate predicate;
    private Command command;
    private boolean disabled;
    private String disabledReason;

    public Trigger(@NonNull Predicate predicate, @NonNull Command command) {
        if (predicate == null) {
            throw new IllegalArgumentException("predicate is null");
        }
        if (command == null) {
            throw new IllegalArgumentException("command is null");
        }
        this.predicate = predicate;
        this.command = command;
    }

    public String getTriggerID() {
        return this.triggerID;
    }
    void setTriggerID(String triggerID) {
        this.triggerID = triggerID;
    }
    public TypedID getTargetID() {
        if (this.command == null) {
            return null;
        }
        return this.command.getTargetID();
    }

    public boolean disabled() {
        return this.disabled;
    }
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public Predicate getPredicate() {
        return this.predicate;
    }

    public Command getCommand() {
        return this.command;
    }
    public String getDisabledReason() {
        return this.disabledReason;
    }
    void setDisabledReason(String disabledReason) {
        this.disabledReason = disabledReason;
    }

    // Implementation of Parcelable
    protected Trigger(Parcel in) {
        this.triggerID = in.readString();
        this.predicate = in.readParcelable(Predicate.class.getClassLoader());
        this.command = in.readParcelable(Command.class.getClassLoader());
        this.disabled = (in.readByte() != 0);
        this.disabledReason = in.readString();
    }

    public static final Creator<Trigger> CREATOR = new Creator<Trigger>() {
        @Override
        public Trigger createFromParcel(Parcel in) {
            return new Trigger(in);
        }

        @Override
        public Trigger[] newArray(int size) {
            return new Trigger[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.triggerID);
        dest.writeParcelable(this.predicate, flags);
        dest.writeParcelable(this.command, flags);
        dest.writeByte((byte) (this.disabled ? 1 : 0));
        dest.writeString(this.disabledReason);
    }
}