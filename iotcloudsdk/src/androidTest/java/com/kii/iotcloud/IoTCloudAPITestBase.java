package com.kii.iotcloud;

import android.test.mock.MockContext;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kii.iotcloud.command.Action;
import com.kii.iotcloud.command.ActionResult;
import com.kii.iotcloud.command.Command;
import com.kii.iotcloud.command.CommandState;
import com.kii.iotcloud.schema.Schema;
import com.kii.iotcloud.schema.SchemaBuilder;
import com.kii.iotcloud.testmodel.LightState;
import com.kii.iotcloud.testmodel.SetBrightness;
import com.kii.iotcloud.testmodel.SetBrightnessResult;
import com.kii.iotcloud.testmodel.SetColor;
import com.kii.iotcloud.testmodel.SetColorResult;
import com.kii.iotcloud.testmodel.SetColorTemperature;
import com.kii.iotcloud.testmodel.SetColorTemperatureResult;
import com.kii.iotcloud.testmodel.TurnPower;
import com.kii.iotcloud.testmodel.TurnPowerResult;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class IoTCloudAPITestBase extends SmallTestBase {
    protected static final String APP_ID = "smalltest";
    protected static final String APP_KEY = "abcdefghijklmnopqrstuvwxyz123456789";
    protected static final String BASE_PATH = "/iot-api/apps/" + APP_ID;
    protected static final String KII_CLOUD_BASE_PATH = "/api/apps/" + APP_ID;
    protected static final String DEMO_THING_TYPE = "LED";
    protected static final String DEMO_SCHEMA_NAME = "SmartLightDemo";
    protected static final int DEMO_SCHEMA_VERSION = 1;

    protected MockWebServer server;

    @Before
    public void before() throws Exception {
        super.before();
        this.server = new MockWebServer();
        this.server.start();
    }
    @After
    public void after() throws Exception {
        this.server.shutdown();
    }

    protected Schema createDefaultSchema() {
        SchemaBuilder sb = SchemaBuilder.newSchemaBuilder(DEMO_THING_TYPE, DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, LightState.class);
        sb.addActionClass(SetColor.class, SetColorResult.class);
        sb.addActionClass(SetBrightness.class, SetBrightnessResult.class);
        sb.addActionClass(SetColorTemperature.class, SetColorTemperatureResult.class);
        sb.addActionClass(TurnPower.class, TurnPowerResult.class);
        return sb.build();
    }
    protected IoTCloudAPI craeteIoTCloudAPIWithDemoSchema(String appID, String appKey) throws Exception {
        String ownerID = UUID.randomUUID().toString();
        Owner owner = new Owner(new TypedID(TypedID.Types.USER, ownerID), "owner-access-token-1234");
        URL baseUrl = this.server.getUrl("/");
        IoTCloudAPIBuilder builder = IoTCloudAPIBuilder.newBuilder(new MockContext(), appID, appKey, baseUrl.toString(), owner);
        builder.addSchema(this.createDefaultSchema());
        return builder.build();
    }
    protected IoTCloudAPI craeteIoTCloudAPIWithSchema(String appID, String appKey, Schema schema) throws Exception {
        String ownerID = UUID.randomUUID().toString();
        Owner owner = new Owner(new TypedID(TypedID.Types.USER, ownerID), "owner-access-token-1234");
        URL baseUrl = this.server.getUrl("/");
        IoTCloudAPIBuilder builder = IoTCloudAPIBuilder.newBuilder(new MockContext(), appID, appKey, baseUrl.toString(), owner);
        builder.addSchema(schema);
        return builder.build();
    }
    protected void addMockResponseForOnBoard(int httpStatus, String thingID, String accessToken) {
        MockResponse response = new MockResponse().setResponseCode(httpStatus);
        if (thingID != null && accessToken != null) {
            JsonObject responseBody = new JsonObject();
            responseBody.addProperty("thingID", thingID);
            responseBody.addProperty("accessToken", accessToken);
            response.setBody(responseBody.toString());
        }
        this.server.enqueue(response);
    }
    protected void addMockResponseForPostNewCommand(int httpStatus, String commandID) {
        MockResponse response = new MockResponse().setResponseCode(httpStatus);
        if (commandID != null) {
            JsonObject responseBody = new JsonObject();
            responseBody.addProperty("commandID", commandID);
            response.setBody(responseBody.toString());
        }
        this.server.enqueue(response);
    }
    protected void addMockResponseForListCommands(int httpStatus, Schema schema, Command[] commands, String paginationKey) {
        MockResponse response = new MockResponse().setResponseCode(httpStatus);
        if (commands != null) {
            JsonObject responseBody = new JsonObject();
            JsonArray array = new JsonArray();
            for (Command command : commands) {
                array.add(GsonRepository.gson(schema).toJsonTree(command));
            }
            responseBody.add("commands", array);
            if (paginationKey != null) {
                responseBody.addProperty("nextPaginationKey", paginationKey);
            }
            response.setBody(responseBody.toString());
        }
        this.server.enqueue(response);
    }
    protected void addMockResponseForGetCommand(int httpStatus, String commandID, TypedID issuer, TypedID target,
                                                List<Action> actions, List<ActionResult> actionResults,
                                                CommandState state, Schema schema, Long created, Long modified) {
        MockResponse response = new MockResponse().setResponseCode(httpStatus);
        if (httpStatus == 200) {
            JsonObject responseBody = new JsonObject();
            responseBody.addProperty("commandID", commandID);
            if (issuer != null) {
                responseBody.addProperty("issuer", issuer.toString());
            }
            if (target != null) {
                responseBody.addProperty("target", target.toString());
            }
            if (actions != null) {
                JsonArray array = new JsonArray();
                for (Action action : actions) {
                    array.add(GsonRepository.gson(schema).toJsonTree(action));
                }
                responseBody.add("actions", array);
            }
            if (actionResults != null) {
                JsonArray array = new JsonArray();
                for (ActionResult actionResult : actionResults) {
                    array.add(GsonRepository.gson(schema).toJsonTree(actionResult));
                }
                responseBody.add("actionResults", array);
            }
            if (state != null) {
                responseBody.addProperty("state", state.name());
            }
            responseBody.addProperty("schema", schema.getSchemaName());
            responseBody.addProperty("schemaVersion", schema.getSchemaVersion());
            if (created != null) {
                responseBody.addProperty("createdAt", created);
            }
            if (modified != null) {
                responseBody.addProperty("modifiedAt", modified);
            }
            response.setBody(responseBody.toString());
        }
        this.server.enqueue(response);
    }
    protected void addMockResponseForInstallPush(int httpStatus, String installationID) {
        MockResponse response = new MockResponse().setResponseCode(httpStatus);
        if (installationID != null) {
            JsonObject responseBody = new JsonObject();
            responseBody.addProperty("installationID", installationID);
            response.setBody(responseBody.toString());
        }
        this.server.enqueue(response);

    }
    protected void addMockResponse(int httpStatus) {
        this.server.enqueue(new MockResponse().setResponseCode(httpStatus));
    }
    protected void assertRequestBody(String expected, RecordedRequest actual) {
        this.assertRequestBody(new JsonParser().parse(expected), actual);
    }
    protected void assertRequestBody(JSONObject expected, RecordedRequest actual) {
        this.assertRequestBody(new JsonParser().parse(expected.toString()), actual);
    }
    protected void assertRequestBody(JsonElement expected, RecordedRequest actual) {
        Assert.assertEquals("request body", expected, new JsonParser().parse(actual.getBody().readUtf8()));
    }
    protected void assertRequestHeader(Map<String, String> expected, RecordedRequest actual) {
        Map<String, List<String>> actualMap = actual.getHeaders().toMultimap();
        // following headers are added by OkHttp client automatically. So we need to ignore them.
        actualMap.remove("Content-Length");
        actualMap.remove("Host");
        actualMap.remove("Connection");
        actualMap.remove("Accept-Encoding");
        actualMap.remove("User-Agent");

        Assert.assertEquals("number of request headers", expected.size(), actualMap.size());
        for (Map.Entry<String, String> h : expected.entrySet()) {
            String expectedHeaderValue = h.getValue();
            if ("Content-Type".equalsIgnoreCase(h.getKey())) {
                // OkHttp adds charset to the Content-Type automatically.
                if (expectedHeaderValue.indexOf("; charset=utf-8") < 0) {
                    expectedHeaderValue += "; charset=utf-8";
                }
            }
            Assert.assertEquals("request header(" + h.getKey() + ")", expectedHeaderValue, actualMap.get(h.getKey()).get(0));
        }
    }
    public void assertCommand(Schema schema, Command expected, Command actual) {
        Assert.assertEquals(expected.getCommandID(), actual.getCommandID());
        Assert.assertEquals(expected.getCommandState(), actual.getCommandState());
        Assert.assertEquals(expected.getActions().size(), actual.getActions().size());
        for (int i = 0; i < expected.getActions().size(); i++) {
            Action expectedAction = expected.getActions().get(i);
            Action actualAction = actual.getActions().get(i);
            Assert.assertEquals(GsonRepository.gson(schema).toJsonTree(expectedAction), GsonRepository.gson(schema).toJsonTree(actualAction));
        }
        Assert.assertEquals(expected.getActionResults().size(), actual.getActionResults().size());
        for (int i = 0; i < expected.getActionResults().size(); i++) {
            ActionResult expectedActionResult = expected.getActionResults().get(i);
            ActionResult actualActionResult = actual.getActionResults().get(i);
            Assert.assertEquals(GsonRepository.gson(schema).toJsonTree(expectedActionResult), GsonRepository.gson(schema).toJsonTree(actualActionResult));
        }
        Assert.assertEquals(expected.getSchemaName(), actual.getSchemaName());
        Assert.assertEquals(expected.getSchemaVersion(), actual.getSchemaVersion());
        Assert.assertEquals(expected.getIssuerID(), actual.getIssuerID());
        Assert.assertEquals(expected.getTargetID(), actual.getTargetID());
        Assert.assertEquals(expected.getFiredByTriggerID(), actual.getFiredByTriggerID());
        Assert.assertEquals(expected.getCreated(), actual.getCreated());
        Assert.assertEquals(expected.getModified(), actual.getModified());
    }
}
