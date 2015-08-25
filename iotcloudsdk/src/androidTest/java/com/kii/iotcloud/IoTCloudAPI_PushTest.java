package com.kii.iotcloud;

import android.support.test.runner.AndroidJUnit4;

import com.google.gson.JsonObject;
import com.kii.iotcloud.exception.IoTCloudRestException;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * http://docs.kii.com/rest/#notification_management-manage_the_user_thing_device_installation-register_a_new_device
 * http://docs.kii.com/rest/#notification_management-manage_the_user_thing_device_installation-delete_the_installation
 */
@RunWith(AndroidJUnit4.class)
public class IoTCloudAPI_PushTest extends IoTCloudAPITestBase {
    @Test
    public void installPushGCMTest() throws Exception {
        String deviceToken = UUID.randomUUID().toString();
        String installationID = UUID.randomUUID().toString();

        this.addMockResponseForInstallPush(201, installationID);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        Assert.assertNull(api.getInstallationID());
        String result = api.installPush(deviceToken, PushBackend.GCM);
        Assert.assertNotNull(api.getInstallationID());
        // verify the result
        Assert.assertEquals(installationID, result);
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals(KII_CLOUD_BASE_PATH + "/installations", request.getPath());
        org.junit.Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.InstallationCreationRequest+json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("installationRegistrationID", deviceToken);
        expectedRequestBody.addProperty("deviceType", PushBackend.GCM.getDeviceType());
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void installPushJPushTest() throws Exception {
        String deviceToken = UUID.randomUUID().toString();
        String installationID = UUID.randomUUID().toString();

        this.addMockResponseForInstallPush(201, installationID);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        Assert.assertNull(api.getInstallationID());
        String result = api.installPush(deviceToken, PushBackend.JPUSH);
        Assert.assertNotNull(api.getInstallationID());
        // verify the result
        Assert.assertEquals(installationID, result);
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals(KII_CLOUD_BASE_PATH + "/installations", request.getPath());
        org.junit.Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.InstallationCreationRequest+json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("installationRegistrationID", deviceToken);
        expectedRequestBody.addProperty("deviceType", PushBackend.JPUSH.getDeviceType());
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void installPush400ErrorTest() throws Exception {
        String deviceToken = UUID.randomUUID().toString();
        this.addMockResponse(400);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        try {
            api.installPush(deviceToken, PushBackend.GCM);
            org.junit.Assert.fail("IoTCloudRestException should be thrown");
        } catch (IoTCloudRestException e) {
            org.junit.Assert.assertEquals(400, e.getStatusCode());
        }
        Assert.assertNull(api.getInstallationID());
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals(KII_CLOUD_BASE_PATH + "/installations", request.getPath());
        org.junit.Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.InstallationCreationRequest+json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("installationRegistrationID", deviceToken);
        expectedRequestBody.addProperty("deviceType", PushBackend.GCM.getDeviceType());
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void installPush401ErrorTest() throws Exception {
        String deviceToken = UUID.randomUUID().toString();
        this.addMockResponse(401);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        try {
            api.installPush(deviceToken, PushBackend.GCM);
            org.junit.Assert.fail("IoTCloudRestException should be thrown");
        } catch (IoTCloudRestException e) {
            org.junit.Assert.assertEquals(401, e.getStatusCode());
        }
        Assert.assertNull(api.getInstallationID());
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals(KII_CLOUD_BASE_PATH + "/installations", request.getPath());
        org.junit.Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.InstallationCreationRequest+json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("installationRegistrationID", deviceToken);
        expectedRequestBody.addProperty("deviceType", PushBackend.GCM.getDeviceType());
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void installPush404ErrorTest() throws Exception {
        String deviceToken = UUID.randomUUID().toString();
        this.addMockResponse(404);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        try {
            api.installPush(deviceToken, PushBackend.GCM);
            org.junit.Assert.fail("IoTCloudRestException should be thrown");
        } catch (IoTCloudRestException e) {
            org.junit.Assert.assertEquals(404, e.getStatusCode());
        }
        Assert.assertNull(api.getInstallationID());
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals(KII_CLOUD_BASE_PATH + "/installations", request.getPath());
        org.junit.Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.InstallationCreationRequest+json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("installationRegistrationID", deviceToken);
        expectedRequestBody.addProperty("deviceType", PushBackend.GCM.getDeviceType());
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test(expected = IllegalArgumentException.class)
    public void installPushWithNullPushBackendTest() throws Exception {
        String deviceToken = UUID.randomUUID().toString();
        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        api.installPush(deviceToken, null);
    }
    @Test
    public void uninstallPushTest() throws Exception {
        String installationID = UUID.randomUUID().toString();

        this.addMockResponse(204);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        api.uninstallPush(installationID);
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals(KII_CLOUD_BASE_PATH + "/installations/" + installationID, request.getPath());
        org.junit.Assert.assertEquals("DELETE", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
    @Test
    public void uninstallPush401ErrorTest() throws Exception {
        String installationID = UUID.randomUUID().toString();

        this.addMockResponse(401);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        try {
            api.uninstallPush(installationID);
            org.junit.Assert.fail("IoTCloudRestException should be thrown");
        } catch (IoTCloudRestException e) {
            org.junit.Assert.assertEquals(401, e.getStatusCode());
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals(KII_CLOUD_BASE_PATH + "/installations/" + installationID, request.getPath());
        org.junit.Assert.assertEquals("DELETE", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
    @Test
    public void uninstallPush404ErrorTest() throws Exception {
        String installationID = UUID.randomUUID().toString();

        this.addMockResponse(404);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        try {
            api.uninstallPush(installationID);
            org.junit.Assert.fail("IoTCloudRestException should be thrown");
        } catch (IoTCloudRestException e) {
            org.junit.Assert.assertEquals(404, e.getStatusCode());
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        org.junit.Assert.assertEquals(KII_CLOUD_BASE_PATH + "/installations/" + installationID, request.getPath());
        org.junit.Assert.assertEquals("DELETE", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
    @Test(expected = IllegalArgumentException.class)
    public void uninstallPushWithNullInstallationIDTest() throws Exception {
        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        api.uninstallPush(null);
    }
}
