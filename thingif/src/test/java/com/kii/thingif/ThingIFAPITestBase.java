package com.kii.thingif;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kii.thingif.actions.AirConditionerActions;
import com.kii.thingif.actions.HumidityActions;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.AliasAction;
import com.kii.thingif.command.AliasActionResult;
import com.kii.thingif.command.CommandState;
import com.kii.thingif.states.AirConditionerState;
import com.kii.thingif.states.HumidityState;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ThingIFAPITestBase extends SmallTestBase {

    protected final String APP_ID = "smalltest";
    protected final String APP_KEY = "abcdefghijklmnopqrstuvwxyz123456789";
    protected static final String BASE_PATH = "/thing-if/apps/smalltest";


    private final String SDK_VERSION = "0.13.0";

    protected MockWebServer server;

    protected static Map<String, Class<? extends Action>> getDefaultActionTypes () {
        Map<String, Class<? extends Action>> actionTypes = new HashMap<>();
        actionTypes.put("airConditionerAlias", AirConditionerActions.class);
        actionTypes.put("humidityAlias", HumidityActions.class);
        return actionTypes;
    }

    protected static Map<String, Class<? extends TargetState>> getDefaultStateTypes () {
        Map<String, Class<? extends TargetState>> stateTypes = new HashMap<>();
        stateTypes.put("airConditionerAlias", AirConditionerState.class);
        stateTypes.put("humidityAlias", HumidityState.class);
        return stateTypes;
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

    protected ThingIFAPI createDefaultThingIFAPI(Context context, String appID, String appKey) throws Exception {
        String ownerID = UUID.randomUUID().toString();
        Owner owner = new Owner(new TypedID(TypedID.Types.USER, ownerID), "owner-access-token-1234");
        KiiApp app = getApp(appID, appKey);
        ThingIFAPI.Builder builder = ThingIFAPI.Builder.newBuilder(
                context,
                app,
                owner,
                getDefaultActionTypes(),
                getDefaultStateTypes());
        return builder.build();
    }

    protected ThingIFAPI.Builder createDefaultThingIFAPIBuilder(Context context, String appID, String appKey) throws Exception {
        String ownerID = UUID.randomUUID().toString();
        Owner owner = new Owner(new TypedID(TypedID.Types.USER, ownerID), "owner-access-token-1234");
        KiiApp app = getApp(appID, appKey);
        return ThingIFAPI.Builder.newBuilder(
                context,
                app,
                owner,
                getDefaultActionTypes(),
                getDefaultStateTypes());
    }

    public KiiApp getApp(String appId, String appKey) {
        String hostName = server.getHostName();
        KiiApp app = KiiApp.Builder.builderWithHostName(appId, appKey, hostName).
                setPort(server.getPort()).setURLSchema("http").build();
        return app;
    }

    /**
     * Utilities of checking request header.
     * Don't include X-Kii-SDK header in expected param and don't remove it from
     * actual param.
     * @param expected
     * @param actual
     */
    protected void assertRequestHeader(Map<String, String> expected, RecordedRequest actual) {
        Map<String, List<String>> actualMap = new HashMap<String, List<String>>();
        for (String headerName : actual.getHeaders().names()) {
            actualMap.put(headerName, actual.getHeaders().values(headerName));
        }
        // following headers are added by OkHttp client automatically. So we need to ignore them.
        actualMap.remove("Content-Length");
        actualMap.remove("Host");
        actualMap.remove("Connection");
        actualMap.remove("Accept-Encoding");
        actualMap.remove("User-Agent");

        // Check X-Kii-SDK Header
        List<String> kiiSDK = actualMap.remove("X-Kii-SDK");
        Assert.assertEquals(1, kiiSDK.size());
        Pattern p = Pattern.compile("sn=at;sv=" + SDK_VERSION + ";pv=\\d*");
        Matcher m = p.matcher(kiiSDK.get(0));
        Assert.assertTrue(m.matches());

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
    protected void assertRequestBody(String expected, RecordedRequest actual) {
        this.assertRequestBody(new JsonParser().parse(expected), actual);
    }
    protected void assertRequestBody(JSONObject expected, RecordedRequest actual) {
        this.assertRequestBody(new JsonParser().parse(expected.toString()), actual);
    }
    protected void assertRequestBody(JsonElement expected, RecordedRequest actual) {
        Assert.assertEquals("request body", expected, new JsonParser().parse(actual.getBody().readUtf8()));
    }
    protected void addEmptyMockResponse(int httpStatus) {
        this.server.enqueue(new MockResponse().setResponseCode(httpStatus));
    }

    protected void clearSharedPreferences(Context context) throws Exception {
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.kii.thingif.preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
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

    protected void addMockResponseForGetCommand(
            int httpStatus,
            String commandID,
            TypedID issuer,
            TypedID target,
            JSONArray aliasActions,
            JSONArray aliasActionResults,
            CommandState state,
            Long created,
            Long modified,
            String firedByTriggerID,
            String title,
            String description,
            JSONObject metadata) {

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
            responseBody.add("actions", new JsonParser().parse(aliasActions.toString()));
            if (aliasActionResults != null) {
                responseBody.add("actionResults", new JsonParser().parse(aliasActionResults.toString()));
            }
            if (state != null) {
                responseBody.addProperty("commandState", state.name());
            }
            if (created != null) {
                responseBody.addProperty("createdAt", created);
            }
            if (modified != null) {
                responseBody.addProperty("modifiedAt", modified);
            }
            if (title != null) {
                responseBody.addProperty("title", title);
            }
            if (description != null) {
                responseBody.addProperty("description", description);
            }
            if (metadata != null) {
                responseBody.add("metadata", new JsonParser().parse(metadata.toString()));
            }
            if (firedByTriggerID != null) {
                responseBody.addProperty("firedByTriggerID", firedByTriggerID);
            }
            response.setBody(responseBody.toString());
        }
        this.server.enqueue(response);
    }
}