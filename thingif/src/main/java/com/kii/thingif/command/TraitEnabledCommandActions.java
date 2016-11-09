package com.kii.thingif.command;

import android.os.Parcel;

import java.util.List;

public class TraitEnabledCommandActions extends CommandActions {
    public class TraitAliasActions {
        private String traitAlias;
        private List<Action> actions;

        public TraitAliasActions(
                String traitAlias,
                List<Action> actions
        ){
            this.traitAlias = traitAlias;
            this.actions = actions;
        }
        public String getTraitAlias() {
            return traitAlias;
        }

        public List<Action> getActions() {
            return actions;
        }
    }

    public TraitEnabledCommandActions(List<TraitAliasActions> aliasActions){
        super(aliasActions);
    }

    public void addAction(TraitAliasActions aliasActions) {
        actions.add(aliasActions);
    }

    // Implementation of Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    protected TraitEnabledCommandActions(Parcel in) {
        super(in);
        this.actions = in.readArrayList(TraitAliasActions.class.getClassLoader());
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
