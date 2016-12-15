package com.kii.thingif;

import android.os.Parcel;

public final class NonTraitAlias implements Alias {

    private NonTraitAlias(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NonTraitAlias> CREATOR = new Creator<NonTraitAlias>() {
        @Override
        public NonTraitAlias createFromParcel(Parcel source) {
            return new NonTraitAlias(source);
        }

        @Override
        public NonTraitAlias[] newArray(int size) {
            return new NonTraitAlias[size];
        }
    };
}
