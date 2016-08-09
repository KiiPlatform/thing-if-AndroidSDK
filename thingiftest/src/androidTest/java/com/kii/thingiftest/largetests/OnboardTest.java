package com.kii.thingiftest.largetests;

import android.support.test.runner.AndroidJUnit4;

import com.kii.thingif.LayoutPosition;
import com.kii.thingif.OnboardWithThingIDOptions;
import com.kii.thingif.OnboardWithVendorThingIDOptions;
import com.kii.thingif.Target;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.TypedID;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

@RunWith(AndroidJUnit4.class)

public class OnboardTest extends LargeTestCaseBase {

    @Test
    public void testOnboardWithVendorThingID() throws Exception {
        ThingIFAPI api = this.createThingIFAPIWithDemoSchema();
        String vendorThingID = UUID.randomUUID().toString();
        String thingPassword = "password";
        OnboardWithVendorThingIDOptions.Builder builder = new OnboardWithVendorThingIDOptions.Builder();
        builder.setThingType(DEMO_THING_TYPE).setLayoutPosition(LayoutPosition.STANDALONE);

        // on-boarding thing
        Target target = api.onboard(vendorThingID, thingPassword, builder.build());
        Assert.assertNotNull(target);
        Assert.assertNotNull(target.getTypedID());
        Assert.assertEquals(TypedID.Types.THING, target.getTypedID().getType());
        Assert.assertNotNull(target.getTypedID().getID());
        Assert.assertNotNull(target.getAccessToken());
    }

    @Test
    public void testOnboardWithThingID() throws Exception {
        ThingIFAPI api = this.createThingIFAPIWithDemoSchema();
        String thingID = "th.9980daa00022-b539-6e11-d1e5-0ea2199b";
        String thingPassword = "password";
        OnboardWithThingIDOptions.Builder builder = new OnboardWithThingIDOptions.Builder();
        builder.setLayoutPosition(LayoutPosition.STANDALONE);

        // on-boarding thing
        Target target = api.onboard(thingID, thingPassword, builder.build());
        Assert.assertNotNull(target);
        Assert.assertNotNull(target.getTypedID());
        Assert.assertEquals(TypedID.Types.THING, target.getTypedID().getType());
        Assert.assertEquals(thingID, target.getTypedID().getID());
        Assert.assertNotNull(target.getAccessToken());
    }
}
