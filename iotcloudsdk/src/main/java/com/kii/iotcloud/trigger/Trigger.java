package com.kii.iotcloud.trigger;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.kii.iotcloud.TypedID;
import com.kii.iotcloud.command.Command;

public class Trigger implements Parcelable {

    private String triggerID;
    private Predicate predicate;
    private Command command;
    private boolean disabled;

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


    // Implementation of Parcelable
    protected Trigger(Parcel in) {
        this.triggerID = in.readString();
        this.predicate = in.readParcelable(Predicate.class.getClassLoader());
        this.command = in.readParcelable(Command.class.getClassLoader());
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
    }
}
