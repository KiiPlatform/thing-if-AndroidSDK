package com.kii.thingif.command;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public abstract class CommandActions implements Parcelable {
    protected List actions;
    public  CommandActions(List actions){
        this.actions = actions;
    }

    public List getActions() {
        return actions;
    }

    protected CommandActions(Parcel in) {
        throw new IllegalStateException("should implement this method");
    }
}
