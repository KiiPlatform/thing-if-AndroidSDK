package com.kii.thing_if.largetests;

import android.support.test.runner.AndroidJUnit4;

import com.kii.thing_if.KiiApp;
import com.kii.thing_if.LayoutPosition;
import com.kii.thing_if.OnboardWithThingIDOptions;
import com.kii.thing_if.OnboardWithVendorThingIDOptions;
import com.kii.thing_if.Owner;
import com.kii.thing_if.Target;
import com.kii.thing_if.ThingIFAPI;
import com.kii.thing_if.TypedID;
import com.kii.thing_if.gateway.EndNode;
import com.kii.thing_if.gateway.PendingEndNode;

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

        // check stored api.
        ThingIFAPI loadedAPI1 = ThingIFAPI.loadFromStoredInstance(this.context);
        Assert.assertNotNull(loadedAPI1);
        assertThingIFAPI("check laodedAPI1", onboardVendorThingIDApi, loadedAPI1);

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

        // check stored api.
        ThingIFAPI loadedAPI2 = ThingIFAPI.loadFromStoredInstance(this.context);
        Assert.assertNotNull(loadedAPI2);
        assertThingIFAPI("check laodedAPI2", onboardThingIDApi, loadedAPI2);
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

        // check stored api.
        ThingIFAPI loadedAPI1 = ThingIFAPI.loadFromStoredInstance(this.context);
        Assert.assertNotNull(loadedAPI1);
        assertThingIFAPI("check laodedAPI1", onboardVendorThingIDApi, loadedAPI1);

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

        // check stored api.
        ThingIFAPI loadedAPI2 = ThingIFAPI.loadFromStoredInstance(this.context);
        Assert.assertNotNull(loadedAPI2);
        assertThingIFAPI("check laodedAPI2", onboardThingIDApi, loadedAPI2);
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

    private void assertThingIFAPI(String tag, ThingIFAPI expected, ThingIFAPI actual) throws Exception {

        KiiApp expectedApp = expected.getApp();
        KiiApp actualApp = actual.getApp();
        Assert.assertEquals(tag,
                expectedApp.getAppID(),
                actualApp.getAppID());
        Assert.assertEquals(tag,
                expectedApp.getAppKey(),
                actualApp.getAppKey());
        Assert.assertEquals(tag,
                expectedApp.getBaseUrl(),
                actualApp.getBaseUrl());
        Assert.assertEquals(tag,
                expectedApp.getHostName(),
                actualApp.getHostName());
        Assert.assertEquals(tag,
                expectedApp.getSiteName(),
                actualApp.getSiteName());
        Assert.assertEquals(tag,
                expectedApp.getSite(),
                actualApp.getSite());

        Assert.assertEquals(tag,
                expected.getAppID(),
                actual.getAppID());
        Assert.assertEquals(tag,
                expected.getAppKey(),
                actual.getAppKey());
        Assert.assertEquals(tag,
                expected.getBaseUrl(),
                actual.getBaseUrl());

        Owner expectedOwner = expected.getOwner();
        Owner actualOwner = actual.getOwner();
        Assert.assertEquals(tag,
                expectedOwner.getTypedID(),
                actualOwner.getTypedID());
        Assert.assertEquals(tag,
                expectedOwner.getAccessToken(),
                actualOwner.getAccessToken());

        Target expectedTarget = expected.getTarget();
        Target actualTarget = actual.getTarget();
        Assert.assertEquals(tag,
                expectedTarget.getTypedID(),
                actualTarget.getTypedID());
        Assert.assertEquals(tag,
                expectedTarget.getAccessToken(),
                actualTarget.getAccessToken());

        Assert.assertEquals(tag,
                expected.getTag(),
                actual.getTag());
        Assert.assertEquals(tag,
                expected.getActionTypes(),
                actual.getActionTypes());
        Assert.assertEquals(tag,
                expected.getStateTypes(),
                actual.getStateTypes());
    }
}
