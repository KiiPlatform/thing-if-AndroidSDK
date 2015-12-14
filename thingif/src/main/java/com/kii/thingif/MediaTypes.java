package com.kii.thingif;

import com.squareup.okhttp.MediaType;

public class MediaTypes {
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json");
    public static final MediaType MEDIA_TYPE_INSTALLATION_CREATION_REQUEST = MediaType.parse("application/vnd.kii.InstallationCreationRequest+json");
    public static final MediaType MEDIA_TYPE_ONBOARDING_WITH_THING_ID_BY_OWNER_REQUEST = MediaType.parse("application/vnd.kii.OnboardingWithThingIDByOwner+json");
    public static final MediaType MEDIA_TYPE_ONBOARDING_WITH_VENDOR_THING_ID_BY_OWNER_REQUEST = MediaType.parse("application/vnd.kii.OnboardingWithVendorThingIDByOwner+json");
}