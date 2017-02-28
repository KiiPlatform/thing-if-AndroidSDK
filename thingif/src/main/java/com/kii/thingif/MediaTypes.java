package com.kii.thingif;

import com.squareup.okhttp.MediaType;

public class MediaTypes {
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json");
    public static final MediaType MEDIA_TYPE_INSTALLATION_CREATION_REQUEST = MediaType.parse("application/vnd.kii.InstallationCreationRequest+json");
    public static final MediaType MEDIA_TYPE_ONBOARDING_WITH_THING_ID_BY_OWNER_REQUEST = MediaType.parse("application/vnd.kii.OnboardingWithThingIDByOwner+json");
    public static final MediaType MEDIA_TYPE_ONBOARDING_WITH_VENDOR_THING_ID_BY_OWNER_REQUEST = MediaType.parse("application/vnd.kii.OnboardingWithVendorThingIDByOwner+json");
    public static final MediaType MEDIA_TYPE_ONBOARDING_ENDNODE_WITH_GATEWAY_THING_ID_REQUEST = MediaType.parse("application/vnd.kii.OnboardingEndNodeWithGatewayThingID+json");
    public static final MediaType MEDIA_TYPE_ONBOARDING_ENDNODE_WITH_GATEWAY_VENDOR_THING_ID_REQUEST = MediaType.parse("application/vnd.kii.OnboardingEndNodeWithGatewayVendorThingID+json");
    public static final MediaType MEDIA_TYPE_VENDOR_THING_ID_UPDATE_REQUEST = MediaType.parse("application/vnd.kii.VendorThingIDUpdateRequest+json");
    public static final MediaType MEDIA_TYPE_POST_NEW_COMMAND_TRAIT = MediaType.parse("application/vnd.kii.CommandCreationRequest+json");
    public static final MediaType MEDIA_TYPE_THING_FIRMWARE_VERSION_UPDATE_REQUEST = MediaType.parse("application/vnd.kii.ThingFirmwareVersionUpdateRequest+json");
}
