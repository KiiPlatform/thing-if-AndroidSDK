package com.kii.thingif;

import android.support.annotation.NonNull;

public final class TraitAlias extends Alias {

    private @NonNull String aliasName;
    public TraitAlias(@NonNull String name){
        this.aliasName = name;
    }

    @NonNull
    public String getAliasName() {
        return aliasName;
    }
}
