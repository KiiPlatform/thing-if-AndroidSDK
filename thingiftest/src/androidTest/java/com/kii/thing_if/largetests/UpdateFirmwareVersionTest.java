package com.kii.thing_if.largetests;

import com.kii.thing_if.OnboardWithVendorThingIDOptions;
import com.kii.thing_if.Target;
import com.kii.thing_if.ThingIFAPI;
import com.kii.thing_if.TypedID;

import junit.framework.Assert;

import org.junit.Test;

import java.util.UUID;

public class UpdateFirmwareVersionTest extends LargeTestCaseBase {

    @Test
    public void baseTest() throws Exception {
        ThingIFAPI api = createDefaultThingIFAPI();
        String vendorThingID = UUID.randomUUID().toString();
        String thingPassword = "password";

        // on-boarding thing
        OnboardWithVendorThingIDOptions options =
                new OnboardWithVendorThingIDOptions.Builder()
                        .setThingType(DEFAULT_THING_TYPE).build();
        Target target = api.onboardWithVendorThingID(vendorThingID, thingPassword, options);
        Assert.assertEquals(TypedID.Types.THING, target.getTypedID().getType());
        Assert.assertNotNull(target.getAccessToken());

        Assert.assertNull(api.getFirmwareVersion());

        api.updateFirmwareVersion(DEFAULT_FIRMWARE_VERSION);

        String fv = api.getFirmwareVersion();
        Assert.assertEquals(DEFAULT_FIRMWARE_VERSION, fv);
    }
}
