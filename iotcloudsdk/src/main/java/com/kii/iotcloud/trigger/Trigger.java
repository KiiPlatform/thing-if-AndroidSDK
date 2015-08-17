package com.kii.iotcloud.trigger;

import android.os.Parcel;
import android.os.Parcelable;

import com.kii.iotcloud.TypedID;
import com.kii.iotcloud.command.Command;

public class Trigger implements Parcelable {

    private String triggerID;
    private Predicate predicate;
    private Command command;
    private boolean enabled;


    public String getTriggerID() {
        return this.triggerID;
    }
    public TypedID getTargetID() {
        if (this.command == null) {
            return null;
        }
        return this.command.getTargetID();
    }

    public boolean enabled() {
        return this.enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Predicate getPredicate() {
        return this.predicate;
    }

    public Command getCommand() {
        return this.command;
    }



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
