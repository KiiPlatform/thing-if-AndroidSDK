package com.kii.iotcloud.command;

import android.os.Parcel;

import java.io.Serializable;

public abstract class Action implements Serializable {
    private final String name;
    public Action(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }

    protected Action(Parcel in) {
        this.name = in.readString();
    }
}
