package com.kii.thing_if.largetests;

import android.support.test.runner.AndroidJUnit4;

import com.kii.thing_if.OnboardWithVendorThingIDOptions;
import com.kii.thing_if.Target;
import com.kii.thing_if.ThingIFAPI;
import com.kii.thing_if.TypedID;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

@RunWith(AndroidJUnit4.class)
public class GetFirmwareVersionTest extends LargeTestCaseBase {

    @Test
    public void getFirmwareVersionTest() throws Exception {
        ThingIFAPI api = createDefaultThingIFAPI();
        String vendorThingID = UUID.randomUUID().toString();
        String thingPassword = "password";

        // on-boarding thing
        OnboardWithVendorThingIDOptions options =
                new OnboardWithVendorThingIDOptions.Builder()
                        .setThingType(DEFAULT_THING_TYPE)
                        .setFirmwareVersion(DEFAULT_FIRMWARE_VERSION).build();
        Target target = api.onboardWithVendorThingID(vendorThingID, thingPassword, options);
        Assert.assertEquals(TypedID.Types.THING, target.getTypedID().getType());
        Assert.assertNotNull(target.getAccessToken());

        String fv = api.getFirmwareVersion();
        Assert.assertEquals(DEFAULT_FIRMWARE_VERSION, fv);
    }

    @Test
    public void getNullTest() throws Exception {
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

        String fv = api.getFirmwareVersion();
        Assert.assertNull(fv);
    }
}
