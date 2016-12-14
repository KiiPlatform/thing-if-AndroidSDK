package com.kii.thingif;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.kii.thingif.command.Command;

public final class TraitAlias implements Alias {

    private @NonNull final String aliasName;
    public TraitAlias(@NonNull String name){
        this.aliasName = name;
    }

    @NonNull
    public String getAliasName() {
        return aliasName;
    }

    private TraitAlias(Parcel in) {
        this.aliasName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.aliasName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TraitAlias> CREATOR = new Creator<TraitAlias>() {
        @Override
        public TraitAlias createFromParcel(Parcel source) {
            return new TraitAlias(source);
        }

        @Override
        public TraitAlias[] newArray(int size) {
            return new TraitAlias[size];
        }
    };
}
