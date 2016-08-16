package com.kii.thingiftest.largetests;

import android.support.test.runner.AndroidJUnit4;

import com.kii.thingif.DataGroupingInterval;
import com.kii.thingif.LayoutPosition;
import com.kii.thingif.OnboardEndnodeWithGatewayOptions;
import com.kii.thingif.OnboardWithThingIDOptions;
import com.kii.thingif.OnboardWithVendorThingIDOptions;
import com.kii.thingif.Target;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.TypedID;
import com.kii.thingif.gateway.PendingEndNode;

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
                    LayoutPosition.STANDALONE).setDataGroupingInterval(
                        DataGroupingInterval.INTERVAL_12_HOURS).build());
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
                LayoutPosition.STANDALONE).setDataGroupingInterval(
                    DataGroupingInterval.INTERVAL_12_HOURS).build());
        Assert.assertNotNull(onboardThingIDTarget);
        Assert.assertNotNull(onboardThingIDTarget.getTypedID());
        Assert.assertEquals(TypedID.Types.THING,
                onboardThingIDTarget.getTypedID().getType());
        Assert.assertEquals(onboardThingIDTarget.getTypedID().getID(),
                onboardThingIDTarget.getTypedID().getID());
        Assert.assertNotNull(onboardThingIDTarget.getAccessToken());
    }

    @Test
    public void testOnboardEndnodeWithGateway() throws Exception {
        ThingIFAPI gatewayAPI = this.createThingIFAPIWithDemoSchema();
        Target gateway = gatewayAPI.onboard(
            "gvid-" + UUID.randomUUID().toString(),
            "gatewaypass",
            (new OnboardWithVendorThingIDOptions.Builder()).setLayoutPosition(
                LayoutPosition.GATEWAY).build());
        Assert.assertNotNull(gateway);
        Assert.assertNotNull(gateway.getTypedID());
        Assert.assertEquals(TypedID.Types.THING,
                gateway.getTypedID().getType());
        Assert.assertNotNull(gateway.getTypedID().getID());

        Target endnode = gatewayAPI.onboardEndnodeWithGateway(
            new PendingEndNode("evid-" + UUID.randomUUID().toString()),
            "endnodepass",
            (new OnboardEndnodeWithGatewayOptions.Builder()).setDataGroupingInterval(
                DataGroupingInterval.INTERVAL_12_HOURS).build());
        Assert.assertNotNull(endnode);
        Assert.assertNotNull(endnode.getTypedID());
        Assert.assertEquals(TypedID.Types.THING,
                endnode.getTypedID().getType());
        Assert.assertNotNull(endnode.getTypedID().getID());
        Assert.assertNotNull(endnode.getAccessToken());
    }
}
