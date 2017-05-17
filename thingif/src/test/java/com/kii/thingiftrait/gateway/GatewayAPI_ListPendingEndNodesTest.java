package com.kii.thingiftrait.gateway;

import android.net.Uri;

import com.kii.thingiftrait.KiiApp;
import com.kii.thingiftrait.exception.BadRequestException;
import com.kii.thingiftrait.exception.ConflictException;
import com.kii.thingiftrait.exception.NotFoundException;
import com.kii.thingiftrait.exception.ServiceUnavailableException;
import com.kii.thingiftrait.exception.UnauthorizedException;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RunWith(RobolectricTestRunner.class)
public class GatewayAPI_ListPendingEndNodesTest extends GatewayAPITestBase {
    @Test
    public void listPendingEndNodesTest() throws Exception {
        GatewayAPI api = this.createGatewayAPIWithLoggedIn();

        PendingEndNode pendingEndNode1 = new PendingEndNode(new JSONObject("{\"vendorThingID\":\"abcd-1234\"}"));
        PendingEndNode pendingEndNode2 = new PendingEndNode(new JSONObject("{\"vendorThingID\":\"efgh-5678\"}"));
        PendingEndNode pendingEndNode3 = new PendingEndNode(new JSONObject("{\"vendorThingID\":\"ijkl-9012\", \"thingProperties\":{\"debug\":true}}"));

        addMockResponseForListPendingEndNodes(200, pendingEndNode1, pendingEndNode2, pendingEndNode3);
        List<PendingEndNode> nodes = api.listPendingEndNodes();

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals("/CUSTOM/apps/" + APP_ID + "/gateway/end-nodes/pending", request.getPath());
        org.junit.Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        this.assertRequestHeader(expectedRequestHeaders, request);

        Assert.assertEquals(3, nodes.size());
        Assert.assertEquals(pendingEndNode1.getVendorThingID(), nodes.get(0).getVendorThingID());
        Assert.assertNull(pendingEndNode1.getThingProperties());
        Assert.assertEquals(pendingEndNode2.getVendorThingID(), nodes.get(1).getVendorThingID());
        Assert.assertNull(pendingEndNode2.getThingProperties());
        Assert.assertEquals(pendingEndNode3.getVendorThingID(), nodes.get(2).getVendorThingID());
        assertJSONObject(pendingEndNode3.getThingProperties(), nodes.get(2).getThingProperties());
    }
    @Test
    public void listPendingEndNodesEmptyTest() throws Exception {
        GatewayAPI api = this.createGatewayAPIWithLoggedIn();

        addMockResponseForListPendingEndNodes(200);
        List<PendingEndNode> nodes = api.listPendingEndNodes();

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals("/CUSTOM/apps/" + APP_ID + "/gateway/end-nodes/pending", request.getPath());
        org.junit.Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        this.assertRequestHeader(expectedRequestHeaders, request);

        Assert.assertEquals(0, nodes.size());
    }
    @Test(expected = IllegalStateException.class)
    public void listPendingEndNodesNoLoggedInTest() throws Exception {
        KiiApp app = getApp(APP_ID, APP_KEY);
        Uri gatewayAddress = getGatewayAddress();
        GatewayAPI api = new GatewayAPI(RuntimeEnvironment.application.getApplicationContext(), app, gatewayAddress);
        api.listPendingEndNodes();
    }
    @Test
    public void listPendingEndNodes400ErrorTest() throws Exception {
        GatewayAPI api = this.createGatewayAPIWithLoggedIn();

        addMockResponseForListPendingEndNodes(400);
        try {
            api.listPendingEndNodes();
            Assert.fail("BadRequestException should be thrown");
        } catch (BadRequestException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals("/CUSTOM/apps/" + APP_ID + "/gateway/end-nodes/pending", request.getPath());
        org.junit.Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
    @Test
    public void listPendingEndNodes401ErrorTest() throws Exception {
        GatewayAPI api = this.createGatewayAPIWithLoggedIn();

        addMockResponseForListPendingEndNodes(401);
        try {
            api.listPendingEndNodes();
            Assert.fail("UnauthorizedException should be thrown");
        } catch (UnauthorizedException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals("/CUSTOM/apps/" + APP_ID + "/gateway/end-nodes/pending", request.getPath());
        org.junit.Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
    @Test
    public void listPendingEndNodes404ErrorTest() throws Exception {
        GatewayAPI api = this.createGatewayAPIWithLoggedIn();

        addMockResponseForListPendingEndNodes(404);
        try {
            api.listPendingEndNodes();
            Assert.fail("NotFoundException should be thrown");
        } catch (NotFoundException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals("/CUSTOM/apps/" + APP_ID + "/gateway/end-nodes/pending", request.getPath());
        org.junit.Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
    @Test
    public void listPendingEndNodes409ErrorTest() throws Exception {
        GatewayAPI api = this.createGatewayAPIWithLoggedIn();

        addMockResponseForListPendingEndNodes(409);
        try {
            api.listPendingEndNodes();
            Assert.fail("ConflictException should be thrown");
        } catch (ConflictException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals("/CUSTOM/apps/" + APP_ID + "/gateway/end-nodes/pending", request.getPath());
        org.junit.Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
    @Test
    public void listPendingEndNodes503ErrorTest() throws Exception {
        GatewayAPI api = this.createGatewayAPIWithLoggedIn();

        addMockResponseForListPendingEndNodes(503);
        try {
            api.listPendingEndNodes();
            Assert.fail("ServiceUnavailableException should be thrown");
        } catch (ServiceUnavailableException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals("/CUSTOM/apps/" + APP_ID + "/gateway/end-nodes/pending", request.getPath());
        org.junit.Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
}
