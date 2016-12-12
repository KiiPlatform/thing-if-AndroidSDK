package com.kii.thingif.core.gateway;

import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.kii.thingif.core.KiiApp;
import com.kii.thingif.core.exception.UnauthorizedException;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class GatewayAPI_GetGatewayInformationTest extends GatewayAPITestBase {
    @Test
    public void getGatewayInformationTest() throws Exception {
        String vendorThingID = UUID.randomUUID().toString();

        GatewayAPI api = this.craeteGatewayAPIWithLoggedIn();
        this.addMockResponseForGetGatewayInformation(200, vendorThingID);
        GatewayInformation information = api.getGatewayInformation();

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals("/gateway-info", request.getPath());
        org.junit.Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        this.assertRequestHeader(expectedRequestHeaders, request);

        Assert.assertEquals(vendorThingID, information.getVendorThingID());
    }
    @Test(expected = IllegalStateException.class)
    public void getGatewayInformationNoLoggedInTest() throws Exception {
        KiiApp app = getApp(APP_ID, APP_KEY);
        Uri gatewayAddress = getGatewayAddress();
        GatewayAPI api = new GatewayAPI(InstrumentationRegistry.getTargetContext(), app, gatewayAddress);
        api.getGatewayInformation();
    }
    @Test
    public void getGatewayInformation401ErrorTest() throws Exception {
        String vendorThingID = UUID.randomUUID().toString();

        GatewayAPI api = this.craeteGatewayAPIWithLoggedIn();
        this.addEmptyMockResponse(401);
        try {
            api.getGatewayInformation();
            org.junit.Assert.fail("UnauthorizedException should be thrown");
        } catch (UnauthorizedException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals("/gateway-info", request.getPath());
        org.junit.Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
}
