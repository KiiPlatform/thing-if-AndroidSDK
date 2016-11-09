package com.kii.thingif.command;

import android.os.Parcel;

import java.util.List;

public class NonTraitCommandActions extends CommandActions {

    public NonTraitCommandActions(List<Action> actions) {
        super(actions);
    }

    public void addAction(Action action) {
        this.actions.add(action);
    }

    // Implementation of Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    protected NonTraitCommandActions(Parcel in) {
        super(in);
        this.actions = in.readArrayList(Action.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeArray(this.actions.toArray());
    }

    public static final Creator<NonTraitCommandActions> CREATOR = new Creator<NonTraitCommandActions>(){
        @Override
        public NonTraitCommandActions[] newArray(int size) {
            return new NonTraitCommandActions[size];
        }

        @Override
        public NonTraitCommandActions createFromParcel(Parcel source) {
            return new NonTraitCommandActions(source);
        }
    };
}
