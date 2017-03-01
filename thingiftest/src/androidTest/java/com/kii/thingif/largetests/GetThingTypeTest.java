package com.kii.thingif.largetests;

import android.support.test.runner.AndroidJUnit4;

import com.kii.thingif.OnboardWithVendorThingIDOptions;
import com.kii.thingif.Target;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.TypedID;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

@RunWith(AndroidJUnit4.class)
public class GetThingTypeTest extends LargeTestCaseBase {

    @Test
    public void getThingTypeTest() throws Exception {
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

        String type = api.getThingType();
        Assert.assertEquals(DEFAULT_THING_TYPE, type);
    }

    @Test
    public void getNullTest() throws Exception {
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
    }
}
