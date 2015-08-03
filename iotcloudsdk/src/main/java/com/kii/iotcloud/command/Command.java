package com.kii.iotcloud.command;

import android.os.Parcel;
import android.os.Parcelable;

import com.kii.iotcloud.TypedID;

import java.util.ArrayList;


public class Command implements Parcelable {

    private TypedID targetID;
    private TypedID issuerID;
    private ArrayList<Action> actions;
    private ArrayList<ActionResult> actionResults;
    private CommandState commandState;

    /** Get ID of the command.
     * @return ID of the command.
     */
    public String getCommandID() {
        // TODO: implement it.
        return null;
    }

    /** Get name of the schema in which command is defined.
     * @return name of the schema.
     */
    public String getSchemaName() {
        // TODO: implement it.
        return null;
    }

    /** Get version of the schema in which command is defined.
     * @return version of the schema.
     */
    public int getSchemaVersion() {
        // TODO: implement it.
        return 0;
    }

    @Override
    public int describeContents() {
        // TODO: implement it.
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        // TODO: implement it.
    }

}
