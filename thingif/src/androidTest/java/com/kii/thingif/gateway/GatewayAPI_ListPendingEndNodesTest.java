package com.kii.thingif.gateway;

import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.kii.thingif.KiiApp;
import com.kii.thingif.exception.BadRequestException;
import com.kii.thingif.exception.ConflictException;
import com.kii.thingif.exception.NotFoundException;
import com.kii.thingif.exception.ServiceUnavailableException;
import com.kii.thingif.exception.UnauthorizedException;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class GatewayAPI_ListPendingEndNodesTest extends GatewayAPITestBase {
    @Test
    public void listPendingEndNodesTest() throws Exception {
        GatewayAPI api = this.craeteGatewayAPIWithLoggedIn();

        EndNode endNode1 = new EndNode(new JSONObject("{\"vendorThingID\":\"abcd-1234\"}"));
        EndNode endNode2 = new EndNode(new JSONObject("{\"vendorThingID\":\"efgh-5678\"}"));
        EndNode endNode3 = new EndNode(new JSONObject("{\"vendorThingID\":\"ijkl-9012\", \"thingProperties\":{\"debug\":true}}"));

        addMockResponseForListPendingEndNodes(200, endNode1, endNode2, endNode3);
        List<EndNode> nodes = api.listPendingEndNodes();

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals("/CUSTOM/apps/" + APP_ID + "/gateway/end-nodes/pending", request.getPath());
        org.junit.Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        this.assertRequestHeader(expectedRequestHeaders, request);

        Assert.assertEquals(3, nodes.size());
        Assert.assertEquals(endNode1.getVendorThingID(), nodes.get(0).getVendorThingID());
        Assert.assertNull(endNode1.getThingProperties());
        Assert.assertEquals(endNode2.getVendorThingID(), nodes.get(1).getVendorThingID());
        Assert.assertNull(endNode2.getThingProperties());
        Assert.assertEquals(endNode3.getVendorThingID(), nodes.get(2).getVendorThingID());
        assertJSONObject(endNode3.getThingProperties(), nodes.get(2).getThingProperties());
    }
    @Test
    public void listPendingEndNodesEmptyTest() throws Exception {
        GatewayAPI api = this.craeteGatewayAPIWithLoggedIn();

        addMockResponseForListPendingEndNodes(200);
        List<EndNode> nodes = api.listPendingEndNodes();

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals("/CUSTOM/apps/" + APP_ID + "/gateway/end-nodes/pending", request.getPath());
        org.junit.Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        this.assertRequestHeader(expectedRequestHeaders, request);

        Assert.assertEquals(0, nodes.size());
    }
    @Test(expected = IllegalStateException.class)
    public void listPendingEndNodesNoLoggedInTest() throws Exception {
        KiiApp app = getApp(APP_ID, APP_KEY);
        Uri gatewayAddress = getGatewayAddress();
        GatewayAPI api = new GatewayAPI(InstrumentationRegistry.getTargetContext(), app, gatewayAddress);
        api.listPendingEndNodes();
    }
    @Test
    public void listPendingEndNodes400ErrorTest() throws Exception {
        GatewayAPI api = this.craeteGatewayAPIWithLoggedIn();

        addMockResponseForListPendingEndNodes(400);
        try {
            api.listPendingEndNodes();
            Assert.fail("BadRequestException should be thrown");
        } catch (BadRequestException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals("/CUSTOM/apps/" + APP_ID + "/gateway/end-nodes/pending", request.getPath());
        org.junit.Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
    @Test
    public void listPendingEndNodes401ErrorTest() throws Exception {
        GatewayAPI api = this.craeteGatewayAPIWithLoggedIn();

        addMockResponseForListPendingEndNodes(401);
        try {
            api.listPendingEndNodes();
            Assert.fail("UnauthorizedException should be thrown");
        } catch (UnauthorizedException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals("/CUSTOM/apps/" + APP_ID + "/gateway/end-nodes/pending", request.getPath());
        org.junit.Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
    @Test
    public void listPendingEndNodes404ErrorTest() throws Exception {
        GatewayAPI api = this.craeteGatewayAPIWithLoggedIn();

        addMockResponseForListPendingEndNodes(404);
        try {
            api.listPendingEndNodes();
            Assert.fail("NotFoundException should be thrown");
        } catch (NotFoundException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals("/CUSTOM/apps/" + APP_ID + "/gateway/end-nodes/pending", request.getPath());
        org.junit.Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
    @Test
    public void listPendingEndNodes409ErrorTest() throws Exception {
        GatewayAPI api = this.craeteGatewayAPIWithLoggedIn();

        addMockResponseForListPendingEndNodes(409);
        try {
            api.listPendingEndNodes();
            Assert.fail("ConflictException should be thrown");
        } catch (ConflictException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals("/CUSTOM/apps/" + APP_ID + "/gateway/end-nodes/pending", request.getPath());
        org.junit.Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
    @Test
    public void listPendingEndNodes503ErrorTest() throws Exception {
        GatewayAPI api = this.craeteGatewayAPIWithLoggedIn();

        addMockResponseForListPendingEndNodes(503);
        try {
            api.listPendingEndNodes();
            Assert.fail("ServiceUnavailableException should be thrown");
        } catch (ServiceUnavailableException e) {
        }

        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals("/CUSTOM/apps/" + APP_ID + "/gateway/end-nodes/pending", request.getPath());
        org.junit.Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("Authorization", "Bearer " + ACCESS_TOKEN);
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
}
