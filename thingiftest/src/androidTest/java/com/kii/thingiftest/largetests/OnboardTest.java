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
    public void testOnboardWithVendorThingIDAndThingID() throws Exception {
        ThingIFAPI onboardVendorThingIDApi =
                this.createThingIFAPIWithDemoSchema();
        String vendorThingID = UUID.randomUUID().toString();
        String thingPassword = "password";

        // on-boarding thing
        Target onboardVendorThingIDTarget = onboardVendorThingIDApi.onboard(
            vendorThingID, thingPassword,
            (new OnboardWithVendorThingIDOptions.Builder()).setThingType(
                DEMO_THING_TYPE).setLayoutPosition(
                    LayoutPosition.STANDALONE).build());
        Assert.assertNotNull(onboardVendorThingIDTarget);
        Assert.assertNotNull(onboardVendorThingIDTarget.getTypedID());
        Assert.assertEquals(TypedID.Types.THING,
                onboardVendorThingIDTarget.getTypedID().getType());
        Assert.assertNotNull(onboardVendorThingIDTarget.getTypedID().getID());
        Assert.assertNotNull(onboardVendorThingIDTarget.getAccessToken());

        ThingIFAPI onboardThingIDApi =
                copyThingIFAPIWithoutTarget(onboardVendorThingIDApi);
        // on-boarding thing
        Target onboardThingIDTarget = onboardThingIDApi.onboard(
            onboardVendorThingIDTarget.getTypedID().getID(), thingPassword,
            (new OnboardWithThingIDOptions.Builder()).setLayoutPosition(
                LayoutPosition.STANDALONE).build());
        Assert.assertNotNull(onboardThingIDTarget);
        Assert.assertNotNull(onboardThingIDTarget.getTypedID());
        Assert.assertEquals(TypedID.Types.THING,
                onboardThingIDTarget.getTypedID().getType());
        Assert.assertEquals(onboardThingIDTarget.getTypedID().getID(),
                onboardThingIDTarget.getTypedID().getID());
        Assert.assertNotNull(onboardThingIDTarget.getAccessToken());
    }

}
