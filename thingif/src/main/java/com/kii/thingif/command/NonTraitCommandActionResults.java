package com.kii.thingif.command;

import android.os.Parcel;

import java.util.List;

public class NonTraitCommandActionResults extends CommandActionResults {
    public NonTraitCommandActionResults(List<ActionResult> actionResults) {
        super(actionResults);
    }

    public void addActionResults(ActionResult actionResult) {
        this.actionResults.add(actionResult);
    }

    // Implementation of Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    protected NonTraitCommandActionResults(Parcel in) {
        super(in);
        this.actionResults = in.readArrayList(ActionResult.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeArray(this.actionResults.toArray());
    }

    public static final Creator<NonTraitCommandActionResults> CREATOR = new Creator<NonTraitCommandActionResults>(){
        @Override
        public NonTraitCommandActionResults[] newArray(int size) {
            return new NonTraitCommandActionResults[size];
        }

        @Override
        public NonTraitCommandActionResults createFromParcel(Parcel source) {
            return new NonTraitCommandActionResults(source);
        }
    };
}
