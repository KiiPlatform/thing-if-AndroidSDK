package com.kii.thingif.command;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class TraitEnabledCommandActions implements Parcelable{

    private List<TraitActions> actions;

    public TraitEnabledCommandActions(){
        this.actions = new ArrayList<>();
    }

    public TraitEnabledCommandActions(List<TraitActions> aliasActions){
        this.actions = new ArrayList<>();
        this.actions.addAll(aliasActions);
    }

    public void addActions(TraitActions actions) {
        this.actions.add(actions);
    }

    public void addActions(String alias, List<Action> actions) {
        this.actions.add(new TraitActions(alias, actions));
    }

    public List toArray(){
        return this.actions;
    }

    // Implementation of Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    protected TraitEnabledCommandActions(Parcel in) {
        this.actions = in.readArrayList(TraitActions.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeArray(this.actions.toArray());
    }

    public static final Creator<TraitEnabledCommandActions> CREATOR = new Creator<TraitEnabledCommandActions>(){
        @Override
        public TraitEnabledCommandActions[] newArray(int size) {
            return new TraitEnabledCommandActions[size];
        }

        @Override
        public TraitEnabledCommandActions createFromParcel(Parcel source) {
            return new TraitEnabledCommandActions(source);
        }
    };
}
