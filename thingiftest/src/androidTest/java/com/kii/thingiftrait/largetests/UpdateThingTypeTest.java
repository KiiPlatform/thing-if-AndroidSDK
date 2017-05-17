package com.kii.thingiftrait.largetests;

import com.kii.thingiftrait.OnboardWithVendorThingIDOptions;
import com.kii.thingiftrait.Target;
import com.kii.thingiftrait.ThingIFAPI;
import com.kii.thingiftrait.TypedID;

import junit.framework.Assert;

import org.junit.Test;

import java.util.UUID;

public class UpdateThingTypeTest extends LargeTestCaseBase {

    @Test
    public void baseTest() throws Exception {
        ThingIFAPI api = createDefaultThingIFAPI();
        String vendorThingID = UUID.randomUUID().toString();
        String thingPassword = "password";

        // on-boarding thing
        OnboardWithVendorThingIDOptions options =
                new OnboardWithVendorThingIDOptions.Builder().build();
        Target target = api.onboardWithVendorThingID(vendorThingID, thingPassword, options);
        Assert.assertEquals(TypedID.Types.THING, target.getTypedID().getType());
        Assert.assertNotNull(target.getAccessToken());

        Assert.assertNull(api.getThingType());

        api.updateThingType(DEFAULT_THING_TYPE);

        String type = api.getThingType();
        Assert.assertEquals(DEFAULT_THING_TYPE, type);
    }
}
