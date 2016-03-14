package com.kii.thingif;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.ActionResult;
import com.kii.thingif.command.Command;
import com.kii.thingif.command.CommandState;
import com.kii.thingif.internal.GsonRepository;
import com.kii.thingif.schema.Schema;
import com.kii.thingif.schema.SchemaBuilder;
import com.kii.thingif.testschemas.LightState;
import com.kii.thingif.testschemas.SetBrightness;
import com.kii.thingif.testschemas.SetBrightnessResult;
import com.kii.thingif.testschemas.SetColor;
import com.kii.thingif.testschemas.SetColorResult;
import com.kii.thingif.testschemas.SetColorTemperature;
import com.kii.thingif.testschemas.SetColorTemperatureResult;
import com.kii.thingif.testschemas.TurnPower;
import com.kii.thingif.testschemas.TurnPowerResult;
import com.kii.thingif.trigger.Predicate;
import com.kii.thingif.trigger.SchedulePredicate;
import com.kii.thingif.trigger.ServerCode;
import com.kii.thingif.trigger.StatePredicate;
import com.kii.thingif.trigger.Trigger;
import com.kii.thingif.trigger.TriggeredServerCodeResult;
import com.kii.thingif.trigger.clause.And;
import com.kii.thingif.trigger.clause.Clause;
import com.kii.thingif.trigger.clause.Equals;
import com.kii.thingif.trigger.clause.NotEquals;
import com.kii.thingif.trigger.clause.Or;
import com.kii.thingif.trigger.clause.Range;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ThingIFAPITestBase extends SmallTestBase {
    protected static final String APP_ID = "smalltest";
    protected static final String APP_KEY = "abcdefghijklmnopqrstuvwxyz123456789";
    protected static final String BASE_PATH = "/thing-if/apps/" + APP_ID;
    protected static final String KII_CLOUD_BASE_PATH = "/api/apps/" + APP_ID;
    protected static final String DEMO_THING_TYPE = "LED";
    protected static final String DEMO_SCHEMA_NAME = "SmartLightDemo";
    protected static final int DEMO_SCHEMA_VERSION = 1;
    private static final String SDK_VERSION = "0.10.0";

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

    public KiiApp getApp(String appId, String appKey) throws NoSuchFieldException, IllegalAccessException {
        String hostName = server.getHostName();
        KiiApp app = KiiApp.Builder.builderWithHostName(appId, appKey, hostName).
                setPort(server.getPort()).setURLSchema("http").build();
        return app;
    }

    protected Schema createDefaultSchema() {
        SchemaBuilder sb = SchemaBuilder.newSchemaBuilder(DEMO_THING_TYPE, DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, LightState.class);
        sb.addActionClass(SetColor.class, SetColorResult.class);
        sb.addActionClass(SetBrightness.class, SetBrightnessResult.class);
        sb.addActionClass(SetColorTemperature.class, SetColorTemperatureResult.class);
        sb.addActionClass(TurnPower.class, TurnPowerResult.class);
        return sb.build();
    }
    protected ThingIFAPIBuilder craeteThingIFAPIBuilderWithDemoSchema(String appID, String appKey) throws Exception {
        String ownerID = UUID.randomUUID().toString();
        Owner owner = new Owner(new TypedID(TypedID.Types.USER, ownerID), "owner-access-token-1234");
        KiiApp app = getApp(appID, appKey);
        ThingIFAPIBuilder builder = ThingIFAPIBuilder.newBuilder(InstrumentationRegistry.getTargetContext(), app, owner);
        builder.addSchema(this.createDefaultSchema());
        return builder;
    }
    protected ThingIFAPI craeteThingIFAPIWithDemoSchema(String appID, String appKey) throws Exception {
        String ownerID = UUID.randomUUID().toString();
        Owner owner = new Owner(new TypedID(TypedID.Types.USER, ownerID), "owner-access-token-1234");
        KiiApp app = getApp(appID, appKey);
        ThingIFAPIBuilder builder = ThingIFAPIBuilder.newBuilder(InstrumentationRegistry.getTargetContext(), app, owner);
        builder.addSchema(this.createDefaultSchema());
        return builder.build();
    }
    protected ThingIFAPI craeteThingIFAPIWithSchema(String appID, String appKey, Schema schema) throws Exception {
        String ownerID = UUID.randomUUID().toString();
        Owner owner = new Owner(new TypedID(TypedID.Types.USER, ownerID), "owner-access-token-1234");
        KiiApp app = getApp(appID, appKey);
        ThingIFAPIBuilder builder = ThingIFAPIBuilder.newBuilder(InstrumentationRegistry.getTargetContext(), app, owner);
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
    protected void addMockResponseForPostNewTrigger(int httpStatus, String triggerID) {
        MockResponse response = new MockResponse().setResponseCode(httpStatus);
        if (triggerID != null) {
            JsonObject responseBody = new JsonObject();
            responseBody.addProperty("triggerID", triggerID);
            response.setBody(responseBody.toString());
        }
        this.server.enqueue(response);
    }
    protected void addMockResponseForGetTriggerWithCommand(int httpStatus, String triggerID, Command command, Predicate predicate, Boolean disabled, String disabledReason, Schema schema) {
        MockResponse response = new MockResponse().setResponseCode(httpStatus);
        if (httpStatus == 200) {
            JsonObject responseBody = new JsonObject();
            if (triggerID != null) {
                responseBody.addProperty("triggerID", triggerID);
            }
            if (command != null) {
                responseBody.add("command", GsonRepository.gson(schema).toJsonTree(command));
            }
            if (predicate != null) {
                responseBody.add("predicate", GsonRepository.gson(schema).toJsonTree(predicate));
            }
            if (disabled != null) {
                responseBody.addProperty("disabled", disabled);
            }
            if (disabledReason != null) {
                responseBody.addProperty("disabledReason", disabledReason);
            }
            response.setBody(responseBody.toString());
        }
        this.server.enqueue(response);
    }
    protected void addMockResponseForGetTriggerWithServerCode(int httpStatus, String triggerID, ServerCode serverCode, Predicate predicate, Boolean disabled, String disabledReason, Schema schema) {
        MockResponse response = new MockResponse().setResponseCode(httpStatus);
        if (httpStatus == 200) {
            JsonObject responseBody = new JsonObject();
            if (triggerID != null) {
                responseBody.addProperty("triggerID", triggerID);
            }
            if (serverCode != null) {
                responseBody.add("serverCode", GsonRepository.gson(schema).toJsonTree(serverCode));
            }
            if (predicate != null) {
                responseBody.add("predicate", GsonRepository.gson(schema).toJsonTree(predicate));
            }
            if (disabled != null) {
                responseBody.addProperty("disabled", disabled);
            }
            if (disabledReason != null) {
                responseBody.addProperty("disabledReason", disabledReason);
            }
            response.setBody(responseBody.toString());
        }
        this.server.enqueue(response);
    }
    protected void addMockResponseForListTriggers(int httpStatus, Trigger[] triggers, String paginationKey, Schema schema) {
        MockResponse response = new MockResponse().setResponseCode(httpStatus);
        if (triggers != null) {
            JsonObject responseBody = new JsonObject();
            JsonArray array = new JsonArray();
            for (Trigger trigger : triggers) {
                array.add(GsonRepository.gson(schema).toJsonTree(trigger));
            }
            responseBody.add("triggers", array);
            if (paginationKey != null) {
                responseBody.addProperty("nextPaginationKey", paginationKey);
            }
            response.setBody(responseBody.toString());
        }
        this.server.enqueue(response);
    }
    protected void addMockResponseForListTriggeredServerCodeResults(int httpStatus, TriggeredServerCodeResult[] results, String paginationKey) {
        MockResponse response = new MockResponse().setResponseCode(httpStatus);
        if (results != null) {
            JsonObject responseBody = new JsonObject();
            JsonArray array = new JsonArray();
            for (TriggeredServerCodeResult result : results) {
                array.add(GsonRepository.gson().toJsonTree(result));
            }
            responseBody.add("triggerServerCodeResults", array);
            if (paginationKey != null) {
                responseBody.addProperty("nextPaginationKey", paginationKey);
            }
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
    protected void addMockResponseForListCommands(int httpStatus, Command[] commands, String paginationKey, Schema schema) {
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
                                                CommandState state, Long created, Long modified, Schema schema) {
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
                responseBody.addProperty("commandState", state.name());
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
    protected void addMockResponse(int httpStatus, JsonElement body) {
        this.server.enqueue(new MockResponse().setResponseCode(httpStatus).setBody(body.toString()));
    }
    protected void addEmptyMockResponse(int httpStatus) {
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
    protected void assertCommand(Schema schema, Command expected, Command actual) {
        if (expected == null &&  actual == null) {
            return;
        }
        Assert.assertEquals(expected.getCommandID(), actual.getCommandID());
        Assert.assertEquals(expected.getCommandState(), actual.getCommandState());
        Assert.assertEquals(expected.getActions().size(), actual.getActions().size());
        for (int i = 0; i < expected.getActions().size(); i++) {
            Action expectedAction = expected.getActions().get(i);
            Action actualAction = actual.getActions().get(i);
            Assert.assertEquals(GsonRepository.gson(schema).toJsonTree(expectedAction), GsonRepository.gson(schema).toJsonTree(actualAction));
        }
        if (expected.getActionResults() == null) {
            Assert.assertNull(actual.getActionResults());
        } else {
            Assert.assertEquals(expected.getActionResults().size(), actual.getActionResults().size());
            for (int i = 0; i < expected.getActionResults().size(); i++) {
                ActionResult expectedActionResult = expected.getActionResults().get(i);
                ActionResult actualActionResult = actual.getActionResults().get(i);
                Assert.assertEquals(GsonRepository.gson(schema).toJsonTree(expectedActionResult), GsonRepository.gson(schema).toJsonTree(actualActionResult));
            }
        }
        Assert.assertEquals(expected.getSchemaName(), actual.getSchemaName());
        Assert.assertEquals(expected.getSchemaVersion(), actual.getSchemaVersion());
        Assert.assertEquals(expected.getIssuerID(), actual.getIssuerID());
        Assert.assertEquals(expected.getTargetID(), actual.getTargetID());
        Assert.assertEquals(expected.getFiredByTriggerID(), actual.getFiredByTriggerID());
        Assert.assertEquals(expected.getCreated(), actual.getCreated());
        Assert.assertEquals(expected.getModified(), actual.getModified());
    }
    protected void assertServerCode(ServerCode expected, ServerCode actual) {
        if (expected == null && actual == null) {
            return;
        }
        Assert.assertEquals(expected.getEndpoint(), actual.getEndpoint());
        Assert.assertEquals(expected.getExecutorAccessToken(), actual.getExecutorAccessToken());
        Assert.assertEquals(expected.getTargetAppID(), actual.getTargetAppID());
        assertJSONObject(expected.getParameters(), actual.getParameters());
    }
    protected void assertPredicate(Predicate expected, Predicate actual) {
        Assert.assertEquals(expected.getClass(), actual.getClass());
        if (expected instanceof StatePredicate) {
            StatePredicate esp = (StatePredicate)expected;
            StatePredicate asp = (StatePredicate)actual;
            Assert.assertEquals(esp.getEventSource(), asp.getEventSource());
            Assert.assertEquals(esp.getTriggersWhen(), asp.getTriggersWhen());
            this.assertClause(esp.getCondition().getClause(), asp.getCondition().getClause());
        } else if (expected instanceof SchedulePredicate) {
            SchedulePredicate esp = (SchedulePredicate)expected;
            SchedulePredicate asp = (SchedulePredicate)actual;
            Assert.assertEquals(esp.getEventSource(), asp.getEventSource());
            Assert.assertEquals(esp.getSchedule().getCronExpression(), asp.getSchedule().getCronExpression());
        }
    }
    protected void assertClause(Clause expected, Clause actual) {
        Assert.assertEquals(expected.getClass(), actual.getClass());
        if (expected instanceof Equals) {
            Equals ee = (Equals)expected;
            Equals ae = (Equals)actual;
            Assert.assertEquals(ee.getField(), ae.getField());
            Assert.assertEquals(ee.getValue(), ae.getValue());
        } else if (expected instanceof NotEquals) {
            NotEquals ene = (NotEquals)expected;
            NotEquals ane = (NotEquals)actual;
            this.assertClause(ene.getEquals(), ane.getEquals());
        } else if (expected instanceof Range) {
            Range er = (Range)expected;
            Range ar = (Range)actual;
            Assert.assertEquals(er.getField(), ar.getField());
            Assert.assertEquals(er.getLowerLimit(), ar.getLowerLimit());
            Assert.assertEquals(er.getUpperLimit(), ar.getUpperLimit());
            Assert.assertEquals(er.getLowerIncluded(), ar.getLowerIncluded());
            Assert.assertEquals(er.getUpperIncluded(), ar.getUpperIncluded());
        } else if (expected instanceof And) {
            And ea = (And)expected;
            And aa = (And)actual;
            for (int i = 0; i < ea.getClauses().length; i++) {
                this.assertClause(ea.getClauses()[i], aa.getClauses()[i]);
            }
        } else if (expected instanceof Or) {
            Or eo = (Or)expected;
            Or ao = (Or)actual;
            for (int i = 0; i < eo.getClauses().length; i++) {
                this.assertClause(eo.getClauses()[i], ao.getClauses()[i]);
            }
        }
    }
    protected void assertTrigger(Schema schema, Trigger expected, Trigger actual) {
        Assert.assertEquals(expected.getTriggerID(), actual.getTriggerID());
        Assert.assertEquals(expected.disabled(), actual.disabled());
        Assert.assertEquals(expected.getDisabledReason(), actual.getDisabledReason());
        this.assertPredicate(expected.getPredicate(), actual.getPredicate());
        this.assertCommand(schema, expected.getCommand(), actual.getCommand());
        this.assertServerCode(expected.getServerCode(), actual.getServerCode());
    }
    protected void assertTriggerServerCodeResult(TriggeredServerCodeResult expected, TriggeredServerCodeResult actual) {
        Assert.assertEquals(expected.isSucceeded(), actual.isSucceeded());
        Assert.assertEquals(expected.getReturnedValue(), actual.getReturnedValue());
        Assert.assertEquals(expected.getExecutedAt(), actual.getExecutedAt());
        assertServerError(expected.getError(), actual.getError());
    }
    protected void assertServerError(ServerError expected, ServerError actual) {
        if (expected == null || actual == null) {
            if (expected == null && actual == null) {
                return;
            }
            Assert.fail("expected is " + expected + " but actual is " + actual);
        }
        Assert.assertEquals(expected.getErrorMessage(), actual.getErrorMessage());
        Assert.assertEquals(expected.getErrorCode(), actual.getErrorCode());
        Assert.assertEquals(expected.getDetailMessage(), actual.getDetailMessage());
    }
    protected void clearSharedPreferences() throws Exception {
        SharedPreferences sharedPreferences = InstrumentationRegistry.getTargetContext().getSharedPreferences("com.kii.thingif.preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

}
