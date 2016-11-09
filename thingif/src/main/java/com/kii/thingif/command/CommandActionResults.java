package com.kii.thingif.command;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public abstract class CommandActionResults implements Parcelable {
    protected List actionResults;

    protected CommandActionResults(List actionResults) {
        this.actionResults = actionResults;
    }

    public List getActionResults() {
        return actionResults;
    }

    protected CommandActionResults(Parcel in) {
    }
}
