package com.kii.iotcloud.command;

import android.os.Parcel;

import java.io.Serializable;

/**
 * Represents base cass of Action.
 * Subclass must have the default constructor.
 */
public abstract class Action implements Serializable {
    public Action() {
    }
    public abstract String getActionName();
}
