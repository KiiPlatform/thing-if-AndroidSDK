package com.kii.thingiftrait;

import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 * Represents owner of things.
 * All {@link ThingIFAPI} operations will be performed with the owner's access token.
 */
public class Owner {

    private final TypedID typedID;
    private final String accessToken;

    public Owner(@NonNull TypedID ownerID, @NonNull String accessToken) {
        if (ownerID == null) {
            throw new IllegalArgumentException("ownerID is null");
        }
        if (TextUtils.isEmpty(accessToken)) {
            throw new IllegalArgumentException("accessToken is null or empty");
        }
        this.typedID = ownerID;
        this.accessToken = accessToken;
    }

    public TypedID getTypedID() {
        return this.typedID;
    }

    public String getAccessToken() {
        return this.accessToken;
    }
}
