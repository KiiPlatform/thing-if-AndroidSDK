package com.kii.thingif.command;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class TraitEnabledActionResults implements Parcelable {
    private List<TraitActionResults> actionResults;

    public TraitEnabledActionResults(){
        this.actionResults = new ArrayList<>();
    }

    public TraitEnabledActionResults(List<TraitActionResults> results) {
        this.actionResults = new ArrayList<>();
        this.actionResults.addAll(results);
    }

    public void addActionResult(TraitActionResults result) {
        this.actionResults.add(result);
    }

    public void addActionResult(String alias, List<ActionResult> results) {
        this.actionResults.add(new TraitActionResults(alias, results));
    }

    public List toArray(){
        return this.actionResults;
    }
    // Implementation of Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    protected TraitEnabledActionResults(Parcel in) {
        this.actionResults = new ArrayList<>();
        in.readList(this.actionResults, null);
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
