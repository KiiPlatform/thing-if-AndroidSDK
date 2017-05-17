package com.kii.thingiftrait.gateway;

import android.net.Uri;

import com.kii.thingiftrait.KiiApp;
import com.kii.thingiftrait.exception.UnauthorizedException;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RunWith(RobolectricTestRunner.class)
public class GatewayAPI_GetGatewayInformationTest extends GatewayAPITestBase {
    @Test
    public void getGatewayInformationTest() throws Exception {
        String vendorThingID = UUID.randomUUID().toString();

        GatewayAPI api = this.createGatewayAPIWithLoggedIn();
        this.addMockResponseForGetGatewayInformation(200, vendorThingID);
        GatewayInformation information = api.getGatewayInformation();

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals("/gateway-info", request.getPath());
        org.junit.Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        this.assertRequestHeader(expectedRequestHeaders, request);

        Assert.assertEquals(vendorThingID, information.getVendorThingID());
    }
    @Test(expected = IllegalStateException.class)
    public void getGatewayInformationNoLoggedInTest() throws Exception {
        KiiApp app = getApp(APP_ID, APP_KEY);
        Uri gatewayAddress = getGatewayAddress();
        GatewayAPI api = new GatewayAPI(RuntimeEnvironment.application.getApplicationContext(), app, gatewayAddress);
        api.getGatewayInformation();
    }
    @Test
    public void getGatewayInformation401ErrorTest() throws Exception {
        String vendorThingID = UUID.randomUUID().toString();

        GatewayAPI api = this.createGatewayAPIWithLoggedIn();
        this.addEmptyMockResponse(401);
        try {
            api.getGatewayInformation();
            org.junit.Assert.fail("UnauthorizedException should be thrown");
        } catch (UnauthorizedException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals("/gateway-info", request.getPath());
        org.junit.Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
}
