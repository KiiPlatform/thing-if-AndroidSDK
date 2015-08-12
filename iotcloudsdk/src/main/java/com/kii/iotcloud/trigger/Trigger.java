package com.kii.iotcloud.trigger;

import android.os.Parcel;
import android.os.Parcelable;

import com.kii.iotcloud.TypedID;
import com.kii.iotcloud.command.Command;

public class Trigger implements Parcelable {

    private String triggerID;
    private Predicate predicate;
    private Command command;


    public String getTriggerID() {
        return this.triggerID;
    }
    public TypedID getTargetID() {
        // TODO: implement
        return null;
    }

    public boolean enabled() {
        // TODO: implement
        return false;
    }

    public Predicate getPredicate() {
        return this.predicate;
    }

    public Command getCommand() {
        return this.command;
    }











    protected Trigger(Parcel in) {
        triggerID = in.readString();
        predicate = in.readParcelable(Predicate.class.getClassLoader());
        command = in.readParcelable(Command.class.getClassLoader());
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
        dest.writeString(triggerID);
        dest.writeParcelable(predicate, flags);
        dest.writeParcelable(command, flags);
    }
}
