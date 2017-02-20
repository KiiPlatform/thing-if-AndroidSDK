package com.kii.thingif;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.Excluder;
import com.kii.thingif.actions.AirConditionerActions;
import com.kii.thingif.actions.HumidityActions;
import com.kii.thingif.actions.ToJSON;
import com.kii.thingif.clause.trigger.AndClauseInTrigger;
import com.kii.thingif.clause.trigger.EqualsClauseInTrigger;
import com.kii.thingif.clause.trigger.NotEqualsClauseInTrigger;
import com.kii.thingif.clause.trigger.OrClauseInTrigger;
import com.kii.thingif.clause.trigger.RangeClauseInTrigger;
import com.kii.thingif.clause.trigger.TriggerClause;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.ActionResult;
import com.kii.thingif.command.AliasAction;
import com.kii.thingif.command.AliasActionResult;
import com.kii.thingif.command.Command;
import com.kii.thingif.command.CommandState;
import com.kii.thingif.states.AirConditionerState;
import com.kii.thingif.states.HumidityState;
import com.kii.thingif.trigger.Condition;
import com.kii.thingif.trigger.Predicate;
import com.kii.thingif.trigger.ScheduleOncePredicate;
import com.kii.thingif.trigger.SchedulePredicate;
import com.kii.thingif.trigger.StatePredicate;
import com.kii.thingif.trigger.TriggerOptions;
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

    protected final String APP_ID = "smalltest";
    protected final String APP_KEY = "abcdefghijklmnopqrstuvwxyz123456789";
    protected static final String BASE_PATH = "/thing-if/apps/smalltest";
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
        try {
            MockResponse response = new MockResponse().setResponseCode(httpStatus);
            if (httpStatus == 200) {
                JSONObject responseBody = new JSONObject();
                if (options != null) {
                    if (options.getTitle() != null) {
                        responseBody.put("title", options.getTitle());
                    }
                    if (options.getDescription() != null) {
                        responseBody.put("description", options.getDescription());
                    }
                    if (options.getMetadata() != null) {
                        responseBody.put("metadata", options.getMetadata());
                    }
                }
                if (triggerID != null) {
                    responseBody.put("triggerID", triggerID);
                }
                if (command != null) {
                    responseBody.put("command", commandToJson(command));
                }
                if (predicate != null) {
                    responseBody.put("predicate", predicateToJson(predicate));
                }
                if (disabled != null) {
                    responseBody.put("disabled", disabled);
                }
                if (disabledReason != null) {
                    responseBody.put("disabledReason", disabledReason);
                }
                response.setBody(responseBody.toString());
            }
            this.server.enqueue(response);
        }catch (JSONException e){
            throw new RuntimeException(e);
        }
    }

    private JSONObject commandToJson(Command cmd) {
        try{
            JSONObject ret = new JSONObject();
            ret.putOpt("title", cmd.getTitle());
            ret.putOpt("description", cmd.getDescription());
            ret.putOpt("metadata", cmd.getMetadata());
            ret.putOpt("commandID", cmd.getCommandID());
            ret.putOpt("createdAt", cmd.getCreated());
            ret.putOpt("modifiedAt", cmd.getModified());
            if (cmd.getCommandState() != null) {
                ret.putOpt("commandState", cmd.getCommandState().name());
            }
            ret.putOpt("firedByTriggerID", cmd.getFiredByTriggerID());

            JSONArray aliasActionsArray = new JSONArray();
            for (AliasAction aliasAction : cmd.getAliasActions()) {
                if (!(aliasAction.getAction() instanceof ToJSON)) {
                    Assert.fail(aliasAction.getAction().getClass().getName()+
                            " not extend ToJSON interface for test purpose");
                }else{
                    aliasActionsArray.put(new JSONObject()
                            .put(aliasAction.getAlias(),
                                    ((ToJSON)aliasAction.getAction()).toJSONArray()));
                }
            }
            if (aliasActionsArray.length() != 0) {
                ret.put("actions", aliasActionsArray);
            }

            if (cmd.getAliasActionResults() != null && cmd.getAliasActionResults().size()!= 0) {
                JSONArray aliasActionResultArray = new JSONArray();
                for (AliasActionResult aliasResult: cmd.getAliasActionResults()) {
                    JSONArray aliasResultJson = new JSONArray();
                    for (ActionResult result: aliasResult.getResults()) {
                        JSONObject resultJson = new JSONObject();
                        resultJson.put("succeeded", result.isSucceeded());
                        resultJson.putOpt("errorMessage", result.getErrorMessage());
                        resultJson.putOpt("data", result.getData());
                        aliasResultJson.put(new JSONObject().put(
                                result.getActionName(),
                                resultJson));
                    }
                    aliasActionResultArray.put(new JSONObject().put(
                            aliasResult.getAlias(),
                            aliasResultJson));
                }
                ret.put("actionResults", aliasActionResultArray);
            }
            return ret;

        }catch (JSONException ex) {
            throw new RuntimeException(ex);
        }
    }

    private JSONObject predicateToJson(Predicate predicate) {
        try {
            JSONObject predicateJson = new JSONObject().put(
                    "eventSource",
                    predicate.getEventSource().name());
            if (predicate instanceof StatePredicate) {
                return predicateJson
                        .put("triggersWhen",
                                ((StatePredicate) predicate).getTriggersWhen().name())
                        .put("condition",
                                triggerClauseToJson(((StatePredicate) predicate).getCondition().getClause()));
            } else if (predicate instanceof ScheduleOncePredicate) {
                return predicateJson.put(
                        "scheduleAt",
                        ((ScheduleOncePredicate) predicate).getScheduleAt());
            } else if (predicate instanceof SchedulePredicate) {
                return predicateJson.put(
                        "schedule",
                        ((SchedulePredicate) predicate).getSchedule());
            } else {
                return null;
            }
        }catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


    private JSONObject triggerClauseToJson(TriggerClause clause) {
        try {
            if (clause instanceof EqualsClauseInTrigger) {
                JSONObject json = new JSONObject();
                EqualsClauseInTrigger eq = (EqualsClauseInTrigger)clause;
                json.put("type", "eq");
                json.put("alias", eq.getAlias());
                json.put("field", eq.getField());
                json.put("value", eq.getValue());
                return json;
            } else if(clause instanceof NotEqualsClauseInTrigger) {
                return new JSONObject()
                        .put("type", "not")
                        .put("clause", triggerClauseToJson(((NotEqualsClauseInTrigger) clause).getEquals()));
            } else if (clause instanceof RangeClauseInTrigger) {
                JSONObject rangeJson = new JSONObject();
                RangeClauseInTrigger range = (RangeClauseInTrigger) clause;
                rangeJson.put("alias", range.getAlias());
                rangeJson.put("field", range.getField());
                rangeJson.putOpt("lowerIncluded", range.getLowerIncluded());
                rangeJson.putOpt("lowerLimit", range.getLowerLimit());
                rangeJson.putOpt("upperIncluded", range.getUpperIncluded());
                rangeJson.putOpt("upperLimit", range.getUpperLimit());
                return rangeJson;
            } else if (clause instanceof AndClauseInTrigger) {
                JSONArray clauses = new JSONArray();
                for (TriggerClause subClause : ((AndClauseInTrigger)clause).getClauses()) {
                    clauses.put(triggerClauseToJson(subClause));
                }
                return new JSONObject()
                        .put("type", "and")
                        .put("clauses", clauses);
            } else if (clause instanceof OrClauseInTrigger) {

                JSONArray clauses = new JSONArray();
                for (TriggerClause subClause : ((OrClauseInTrigger) clause).getClauses()) {
                    clauses.put(triggerClauseToJson(subClause));
                }
                return new JSONObject()
                        .put("type", "or")
                        .put("clauses", clauses);
            }else{
                throw new RuntimeException("not support trigger clause");
            }
        }catch (JSONException ex) {
            throw new RuntimeException(ex);
        }
    }

}
