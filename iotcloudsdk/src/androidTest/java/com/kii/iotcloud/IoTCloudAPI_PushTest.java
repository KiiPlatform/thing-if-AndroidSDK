package com.kii.iotcloud;

import android.support.test.runner.AndroidJUnit4;

import com.google.gson.JsonObject;
import com.kii.iotcloud.exception.BadRequestException;
import com.kii.iotcloud.exception.NotFoundException;
import com.kii.iotcloud.exception.UnauthorizedException;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.junit.Assert;
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
        Assert.assertEquals(KII_CLOUD_BASE_PATH + "/installations", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

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
    public void installPushGCMDevelopmentTest() throws Exception {
        String deviceToken = UUID.randomUUID().toString();
        String installationID = UUID.randomUUID().toString();

        this.addMockResponseForInstallPush(201, installationID);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        Assert.assertNull(api.getInstallationID());
        String result = api.installPush(deviceToken, PushBackend.GCM,true);
        Assert.assertNotNull(api.getInstallationID());
        // verify the result
        Assert.assertEquals(installationID, result);
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(KII_CLOUD_BASE_PATH + "/installations", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders.put("Content-Type", "application/vnd.kii.InstallationCreationRequest+json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.addProperty("installationRegistrationID", deviceToken);
        expectedRequestBody.addProperty("deviceType", PushBackend.GCM.getDeviceType());
        expectedRequestBody.addProperty("development", true);
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
        Assert.assertEquals(KII_CLOUD_BASE_PATH + "/installations", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

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
        this.addEmptyMockResponse(400);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        try {
            api.installPush(deviceToken, PushBackend.GCM);
            Assert.fail("IoTCloudRestException should be thrown");
        } catch (BadRequestException e) {
            Assert.assertEquals(400, e.getStatusCode());
        }
        Assert.assertNull(api.getInstallationID());
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(KII_CLOUD_BASE_PATH + "/installations", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

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
        this.addEmptyMockResponse(401);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        try {
            api.installPush(deviceToken, PushBackend.GCM);
            Assert.fail("IoTCloudRestException should be thrown");
        } catch (UnauthorizedException e) {
            Assert.assertEquals(401, e.getStatusCode());
        }
        Assert.assertNull(api.getInstallationID());
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(KII_CLOUD_BASE_PATH + "/installations", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

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
        this.addEmptyMockResponse(404);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        try {
            api.installPush(deviceToken, PushBackend.GCM);
            Assert.fail("IoTCloudRestException should be thrown");
        } catch (NotFoundException e) {
        }
        Assert.assertNull(api.getInstallationID());
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(KII_CLOUD_BASE_PATH + "/installations", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

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

        this.addEmptyMockResponse(204);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        api.uninstallPush(installationID);
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(KII_CLOUD_BASE_PATH + "/installations/" + installationID, request.getPath());
        Assert.assertEquals("DELETE", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
    @Test
    public void uninstallPush401ErrorTest() throws Exception {
        String installationID = UUID.randomUUID().toString();

        this.addEmptyMockResponse(401);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        try {
            api.uninstallPush(installationID);
            Assert.fail("IoTCloudRestException should be thrown");
        } catch (UnauthorizedException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(KII_CLOUD_BASE_PATH + "/installations/" + installationID, request.getPath());
        Assert.assertEquals("DELETE", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
    @Test
    public void uninstallPush404ErrorTest() throws Exception {
        String installationID = UUID.randomUUID().toString();

        this.addEmptyMockResponse(404);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        try {
            api.uninstallPush(installationID);
            Assert.fail("IoTCloudRestException should be thrown");
        } catch (NotFoundException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(KII_CLOUD_BASE_PATH + "/installations/" + installationID, request.getPath());
        Assert.assertEquals("DELETE", request.getMethod());

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
