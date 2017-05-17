package com.kii.thingiftrait.largetests;

import com.kii.thingiftrait.OnboardWithVendorThingIDOptions;
import com.kii.thingiftrait.Target;
import com.kii.thingiftrait.ThingIFAPI;
import com.kii.thingiftrait.TypedID;

import junit.framework.Assert;

import org.junit.Test;

import java.util.UUID;

public class VendorThingIDTest extends LargeTestCaseBase {

    @Test
    public void baseTest() throws Exception {
        ThingIFAPI api = createDefaultThingIFAPI();
        String vendorThingID1 = UUID.randomUUID().toString();
        String vendorThingID2 = UUID.randomUUID().toString();
        String password1 = "password-1";
        String password2 = "password-2";

        Assert.assertFalse("Random maked UUIDs are same. Please rerun.",
                vendorThingID1.equals(vendorThingID2));

        // on-boarding thing
        OnboardWithVendorThingIDOptions options =
                new OnboardWithVendorThingIDOptions.Builder().build();
        Target target = api.onboardWithVendorThingID(vendorThingID1, password1, options);
        Assert.assertEquals(TypedID.Types.THING, target.getTypedID().getType());
        Assert.assertNotNull(target.getAccessToken());

        Assert.assertEquals(vendorThingID1, api.getVendorThingID());

        api.updateVendorThingID(vendorThingID2, password2);

        Assert.assertEquals(vendorThingID2, api.getVendorThingID());
    }
}
