package com.kii.thingif.command;

import android.os.Parcel;

import java.util.List;

public class TraitEnabledActionResults extends CommandActionResults {


    public class TraitAliasActionResults {
        private String traitAlias;
        private List<ActionResult> results;
        TraitAliasActionResults(String traitAlias, List<ActionResult> results) {
            this.traitAlias = traitAlias;
            this.results = results;
        }

        public String getTraitAlias() {
            return traitAlias;
        }

        public List<ActionResult> getResults() {
            return results;
        }
    }

    public TraitEnabledActionResults(List<TraitAliasActionResults> results) {
        super(results);
    }

    public void addActionResult(TraitAliasActionResults result) {
        this.actionResults.add(result);
    }

    // Implementation of Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    protected TraitEnabledActionResults(Parcel in) {
        super(in);
        this.actionResults = in.readArrayList(TraitAliasActionResults.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeArray(this.actionResults.toArray());
    }

    public static final Creator<TraitEnabledActionResults> CREATOR = new Creator<TraitEnabledActionResults>(){
        @Override
        public TraitEnabledActionResults[] newArray(int size) {
            return new TraitEnabledActionResults[size];
        }

        @Override
        public TraitEnabledActionResults createFromParcel(Parcel source) {
            return new TraitEnabledActionResults(source);
        }
    };
}
