package com.kii.thingif;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kii.thingif.actions.AirConditionerActions;
import com.kii.thingif.actions.HumidityActions;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.Command;
import com.kii.thingif.command.CommandState;
import com.kii.thingif.query.GroupedHistoryStates;
import com.kii.thingif.query.HistoryState;
import com.kii.thingif.states.AirConditionerState;
import com.kii.thingif.states.HumidityState;
import com.kii.thingif.trigger.Predicate;
import com.kii.thingif.trigger.ServerCode;
import com.kii.thingif.trigger.Trigger;
import com.kii.thingif.trigger.TriggerOptions;
import com.kii.thingif.trigger.TriggeredServerCodeResult;
import com.kii.thingif.utils.JsonUtil;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ThingIFAPITestBase extends SmallTestBase {

    protected static final String APP_ID = "smalltest";
    protected static final String APP_KEY = "abcdefghijklmnopqrstuvwxyz123456789";
    protected static final String BASE_PATH = "/thing-if/apps/smalltest";
    protected static final String KII_CLOUD_BASE_PATH = "/api/apps/" + APP_ID;
    protected static final String ALIAS1 = "AirConditionerAlias";
    protected static final String ALIAS2 = "HumidityAlias";


    private final String SDK_VERSION = "0.13.0";

    protected MockWebServer server;

    protected static Map<String, Class<? extends Action>> getDefaultActionTypes () {
        Map<String, Class<? extends Action>> actionTypes = new HashMap<>();
        actionTypes.put(ALIAS1, AirConditionerActions.class);
        actionTypes.put(ALIAS2, HumidityActions.class);
        return actionTypes;
    }

    protected static Map<String, Class<? extends TargetState>> getDefaultStateTypes () {
        Map<String, Class<? extends TargetState>> stateTypes = new HashMap<>();
        stateTypes.put(ALIAS1, AirConditionerState.class);
        stateTypes.put(ALIAS2, HumidityState.class);
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

    protected ThingIFAPI createDefaultThingIFAPI(Context context, String appID, String appKey) {
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
            try {
                response.setBody(
                        createCommandJson(
                                commandID,
                                issuer,
                                target,
                                aliasActions,
                                aliasActionResults,
                                state,
                                firedByTriggerID,
                                created,
                                modified,
                                title,
                                description,
                                metadata).toString());
            }catch (JSONException ex) {
                throw new RuntimeException(ex);
            }
        }
        this.server.enqueue(response);
    }

    protected void addMockResponseForListCommands(
            int httpStatus,
            JSONArray commands,
            String paginationKey) throws JSONException{
        MockResponse response = new MockResponse().setResponseCode(httpStatus);
        if (commands != null) {
            JSONObject responseBody = new JSONObject();
            responseBody.put("commands", commands);
            if (paginationKey != null) {
                responseBody.put("nextPaginationKey", paginationKey);
            }
            response.setBody(responseBody.toString());
        }
        this.server.enqueue(response);
    }

    protected JSONObject createCommandJson(
            String commandID,
            TypedID issuer,
            TypedID target,
            JSONArray actions,
            JSONArray results,
            CommandState state,
            String firedTriggerID,
            Long created,
            Long modified,
            String title,
            String description,
            JSONObject metadata) throws JSONException{
        JSONObject ret = new JSONObject();
        ret.put("issuer", issuer.toString());
        ret.put("actions", actions);
        if (commandID != null) {
            ret.put("commandID", commandID);
        }
        if (target != null) {
            ret.put("target", target.toString());
        }
        if (results != null) {
            ret.put("actionResults", results);
        }
        if (state != null) {
            ret.put("commandState", state.name());
        }
        if (firedTriggerID != null) {
            ret.put("firedByTriggerID", firedTriggerID);
        }
        if (created != null) {
            ret.put("createdAt", created);
        }
        if (modified != null) {
            ret.put("modifiedAt", modified);
        }
        if (title != null) {
            ret.put("title", title);
        }
        if (description != null) {
            ret.put("description", description);
        }
        if (metadata != null) {
            ret.put("metadata", metadata);
        }
        return ret;
    }

    protected void addMockResponseForGetTriggerWithCommand(
            int httpStatus,
            String triggerID,
            Command command,
            Predicate predicate,
            TriggerOptions options,
            Boolean disabled,
            String disabledReason)
    {
        MockResponse response = new MockResponse().setResponseCode(httpStatus);
        if (httpStatus == 200) {
            JSONObject responseBody = JsonUtil.createTriggerJson(
                    triggerID,
                    command,
                    null,
                    predicate,
                    options,
                    disabled,
                    disabledReason);
            response.setBody(responseBody.toString());
        }
        this.server.enqueue(response);
    }

    protected void addMockResponseForGetTriggerWithServerCode(
            int httpStatus,
            String triggerID,
            ServerCode serverCode,
            Predicate predicate,
            TriggerOptions options,
            Boolean disabled,
            String disabledReason) throws Exception
    {
        MockResponse response = new MockResponse().setResponseCode(httpStatus);
        if (httpStatus == 200) {
            JSONObject responseBody = JsonUtil.createTriggerJson(
                    triggerID,
                    null,
                    serverCode,
                    predicate,
                    options,
                    disabled,
                    disabledReason);
            response.setBody(responseBody.toString());
        }
        this.server.enqueue(response);
    }

    protected void addMockResponseForListTriggers(int httpStatus, Trigger[] triggers, String paginationKey) throws Exception{
        MockResponse response = new MockResponse().setResponseCode(httpStatus);
        if (triggers != null) {
            JSONObject responseBody = new JSONObject();
            JSONArray array = new JSONArray();
            for (Trigger trigger : triggers) {
                array.put(JsonUtil.triggerToJson(trigger));
            }
            responseBody.put("triggers", array);
            responseBody.putOpt("nextPaginationKey", paginationKey);
            response.setBody(responseBody.toString());
        }
        this.server.enqueue(response);
    }
    protected void addMockResponseForPostNewTrigger(int httpStatus, String triggerID) {
        MockResponse response = new MockResponse().setResponseCode(httpStatus);
        if (triggerID != null) {
            JsonObject responseBody = new JsonObject();
            responseBody.addProperty("triggerID", triggerID);
            response.setBody(responseBody.toString());
        }
        this.server.enqueue(response);
    }
    protected void addMockResponseForListTriggeredServerCodeResults(
            int httpStatus,
            TriggeredServerCodeResult[] results,
            String paginationKey) throws Exception{
        MockResponse response = new MockResponse().setResponseCode(httpStatus);
        if (results != null) {
            JSONObject responseBody = new JSONObject();
            JSONArray array = new JSONArray();
            for (TriggeredServerCodeResult result : results) {
                array.put(JsonUtil.triggeredServerCodeResultToJson(result));
            }
            responseBody.put("triggerServerCodeResults", array);
            responseBody.putOpt("nextPaginationKey", paginationKey);
            response.setBody(responseBody.toString());
        }
        this.server.enqueue(response);
    }

    protected void addMockResponseForQueryUngroupedHistoryState(
            int httpStatus,
            List<HistoryState<? extends TargetState>> states,
            String paginationKey) throws Exception {
        MockResponse response = new MockResponse().setResponseCode(httpStatus);
        if (httpStatus == 200) {
            JSONArray stateArray = new JSONArray();
            for (HistoryState state : states) {
                stateArray.put(JsonUtil.historyStateToJson(state));
            }
            JSONObject responseBody = new JSONObject();
            responseBody.put("results", stateArray);
            responseBody.putOpt("nextPaginationKey", paginationKey);
            response.setBody(responseBody.toString());
        }
        this.server.enqueue(response);
    }

    protected void addMockResponseForQueryGroupedHistoryStates(
            int httpStatus,
            List<GroupedHistoryStates<? extends TargetState>> groupedStatesArray) throws Exception {
        MockResponse response = new MockResponse().setResponseCode(httpStatus);
        if (httpStatus == 200) {
            JSONObject responseBody = new JSONObject();
            JSONArray groupedJsonArray = new JSONArray();
            for (GroupedHistoryStates groupedStates: groupedStatesArray) {
                groupedJsonArray.put(JsonUtil.groupedHistoryStateToJson(groupedStates));
            }
            responseBody.put("groupedResults", groupedJsonArray);
            response.setBody(responseBody.toString());
        }
        this.server.enqueue(response);
    }

    protected void assertTriggerOptions(
            TriggerOptions expected,
            Trigger actual)
    {
        Assert.assertEquals(expected.getTitle(), actual.getTitle());
        Assert.assertEquals(expected.getDescription(), actual.getDescription());
        assertJSONObject(expected.getMetadata(), actual.getMetadata());
    }

    protected void addMockResponseForOnBoardEndnode(int httpStatus, String thingID, String accessToken) {
        MockResponse response = new MockResponse().setResponseCode(httpStatus);
        if (thingID != null && accessToken != null) {
            JsonObject responseBody = new JsonObject();
            responseBody.addProperty("endNodeThingID", thingID);
            responseBody.addProperty("accessToken", accessToken);
            response.setBody(responseBody.toString());
        }
        this.server.enqueue(response);
    }
    protected void addMockResponseForGetVendorThingID(int httpStatus, String vendorThingID) {
        MockResponse response = new MockResponse().setResponseCode(httpStatus);
        if (vendorThingID != null) {
            JsonObject responseBody = new JsonObject();
            responseBody.addProperty("_vendorThingID", vendorThingID);
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
}
