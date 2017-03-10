package com.kii.thingif.largetests;

import android.support.test.runner.AndroidJUnit4;

import com.kii.thingif.LayoutPosition;
import com.kii.thingif.OnboardWithThingIDOptions;
import com.kii.thingif.OnboardWithVendorThingIDOptions;
import com.kii.thingif.Target;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.TypedID;
import com.kii.thingif.gateway.EndNode;
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
                this.createDefaultThingIFAPI();
        String vendorThingID = UUID.randomUUID().toString();
        String thingPassword = "password";

        // on-boarding thing
        Target onboardVendorThingIDTarget = onboardVendorThingIDApi.onboardWithVendorThingID(
            vendorThingID, thingPassword,
            (new OnboardWithVendorThingIDOptions.Builder())
                .setThingType(DEFAULT_THING_TYPE)
                .setLayoutPosition(LayoutPosition.STANDALONE)
                .build());
        Assert.assertNotNull(onboardVendorThingIDTarget);
        Assert.assertNotNull(onboardVendorThingIDTarget.getTypedID());
        Assert.assertEquals(TypedID.Types.THING,
                onboardVendorThingIDTarget.getTypedID().getType());
        Assert.assertNotNull(onboardVendorThingIDTarget.getTypedID().getID());
        Assert.assertNotNull(onboardVendorThingIDTarget.getAccessToken());

        ThingIFAPI onboardThingIDApi =
                copyThingIFAPIWithoutTarget(onboardVendorThingIDApi);
        // on-boarding thing
        Target onboardThingIDTarget = onboardThingIDApi.onboardWithThingID(
            onboardVendorThingIDTarget.getTypedID().getID(), thingPassword,
            (new OnboardWithThingIDOptions.Builder())
                .setLayoutPosition(LayoutPosition.STANDALONE)
                .build());
        Assert.assertNotNull(onboardThingIDTarget);
        Assert.assertNotNull(onboardThingIDTarget.getTypedID());
        Assert.assertEquals(TypedID.Types.THING,
                onboardThingIDTarget.getTypedID().getType());
        Assert.assertEquals(onboardThingIDTarget.getTypedID().getID(),
                onboardThingIDTarget.getTypedID().getID());
        Assert.assertNotNull(onboardThingIDTarget.getAccessToken());
    }

    @Test
    public void testOnboardWithVendorThingIDAndThingIDWithoutOption() throws Exception {
        ThingIFAPI onboardVendorThingIDApi =
                this.createDefaultThingIFAPI();
        String vendorThingID = UUID.randomUUID().toString();
        String thingPassword = "password";

        // on-boarding thing
        Target onboardVendorThingIDTarget = onboardVendorThingIDApi.onboardWithVendorThingID(
                vendorThingID, thingPassword);
        Assert.assertNotNull(onboardVendorThingIDTarget);
        Assert.assertNotNull(onboardVendorThingIDTarget.getTypedID());
        Assert.assertEquals(TypedID.Types.THING,
                onboardVendorThingIDTarget.getTypedID().getType());
        Assert.assertNotNull(onboardVendorThingIDTarget.getTypedID().getID());
        Assert.assertNotNull(onboardVendorThingIDTarget.getAccessToken());

        ThingIFAPI onboardThingIDApi =
                copyThingIFAPIWithoutTarget(onboardVendorThingIDApi);
        // on-boarding thing
        Target onboardThingIDTarget = onboardThingIDApi.onboardWithThingID(
                onboardVendorThingIDTarget.getTypedID().getID(), thingPassword);
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
        ThingIFAPI gatewayAPI = this.createDefaultThingIFAPI();
        Target gateway = gatewayAPI.onboardWithVendorThingID(
            "gvid-" + UUID.randomUUID().toString(),
            "gatewaypass",
            (new OnboardWithVendorThingIDOptions.Builder())
                .setLayoutPosition(LayoutPosition.GATEWAY).build());
        Assert.assertNotNull(gateway);
        Assert.assertNotNull(gateway.getTypedID());
        Assert.assertEquals(TypedID.Types.THING,
                gateway.getTypedID().getType());
        Assert.assertNotNull(gateway.getTypedID().getID());

        EndNode endnode = gatewayAPI.onboardEndNodeWithGateway(
                new PendingEndNode("evid-" + UUID.randomUUID().toString()),
                "endnodepass");
        Assert.assertNotNull(endnode);
        Assert.assertNotNull(endnode.getTypedID());
        Assert.assertEquals(TypedID.Types.THING,
                endnode.getTypedID().getType());
        Assert.assertNotNull(endnode.getTypedID().getID());
        Assert.assertNotNull(endnode.getAccessToken());
    }
}
